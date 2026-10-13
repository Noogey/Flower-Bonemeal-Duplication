package com.flowerbonemeal.client;

import com.flowerbonemeal.FlowerBoneMealConfig;
import com.flowerbonemeal.FlowerBoneMealConfig.AzaleaMode;
import com.flowerbonemeal.FlowerBoneMealConfig.WitherGoldenMode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Settings screen opened from Mod Menu's "Configure" button.
 * Each row has its name on the left and a button on the right; clicking the button moves to the
 * next choice. While "Mod Enabled" is No, the other rows are greyed out.
 */
public class FlowerBoneMealConfigScreen extends Screen {

    private static final int ROW_HEIGHT = 25;
    private static final int BUTTON_WIDTH = 140;
    private static final int TOP = 40;

    /** One row on the screen: its label, where it sits, and its button. */
    private record Row(Component label, int y, Button button) {}

    private final Screen parent;
    private final List<Row> rows = new ArrayList<>();
    private final List<Button> settingButtons = new ArrayList<>();
    private int left;
    private int buttonX;

    public FlowerBoneMealConfigScreen(Screen parent) {
        super(Component.translatable("flowerbonemeal.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        rows.clear();
        settingButtons.clear();
        FlowerBoneMealConfig config = FlowerBoneMealConfig.get();

        int panelWidth = Math.min(this.width - 40, 460);
        this.left = (this.width - panelWidth) / 2;
        this.buttonX = this.left + panelWidth - BUTTON_WIDTH;

        // Row 1: master switch.
        addRow("mod_enabled", 0, false,
                () -> yesNo(config.modEnabled),
                () -> {
                    config.modEnabled = !config.modEnabled;
                    updateSettingButtons();
                });

        // Row 2: mangrove propagule (Yes/No).
        addRow("mangrove_propagule", 1, true,
                () -> yesNo(config.mangrovePropaguleDropsCopy),
                () -> config.mangrovePropaguleDropsCopy = !config.mangrovePropaguleDropsCopy);

        // Row 3: azaleas (Flowering Only -> Flowering and Regular -> Neither).
        addRow("azalea", 2, true,
                () -> mode("azalea", config.azaleaMode.name(), config.azaleaMode != AzaleaMode.NEITHER),
                () -> config.azaleaMode = config.azaleaMode.next());

        // Row 4: torchflower and pitcher plant (Yes/No).
        addRow("torchflower_pitcher", 3, true,
                () -> yesNo(config.torchflowerAndPitcherPlantDropCopy),
                () -> config.torchflowerAndPitcherPlantDropCopy = !config.torchflowerAndPitcherPlantDropCopy);

        // Row 5: wither rose and golden dandelion (Wither Rose Only -> Golden Dandelion Only -> Both -> Off).
        addRow("wither_golden", 4, true,
                () -> mode("wither_golden", config.witherRoseAndGoldenDandelion.name(),
                        config.witherRoseAndGoldenDandelion != WitherGoldenMode.OFF),
                () -> config.witherRoseAndGoldenDandelion = config.witherRoseAndGoldenDandelion.next());

        updateSettingButtons();

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .pos(this.width / 2 - 100, this.height - 28).size(200, 20).build());
    }

    private void addRow(String name, int index, boolean isSetting, Supplier<Component> text, Runnable onClick) {
        String key = "flowerbonemeal.option." + name;
        int y = TOP + index * ROW_HEIGHT;

        Button button = Button.builder(text.get(), b -> {
                    onClick.run();
                    b.setMessage(text.get());
                    FlowerBoneMealConfig.save(); // write config/flowerbonemeal.json immediately
                })
                .pos(this.buttonX, y).size(BUTTON_WIDTH, 20).build();
        button.setTooltip(Tooltip.create(Component.translatable(key + ".tooltip")));

        this.addRenderableWidget(button);
        rows.add(new Row(Component.translatable(key), y, button));
        if (isSetting) settingButtons.add(button);
    }

    private static Component yesNo(boolean value) {
        return value
                ? Component.translatable("flowerbonemeal.option.yes").withStyle(ChatFormatting.GREEN)
                : Component.translatable("flowerbonemeal.option.no").withStyle(ChatFormatting.RED);
    }

    /** Text for a multi-choice button, e.g. "flowerbonemeal.option.azalea.flowering_only". Green unless it's the "off" choice. */
    private static Component mode(String setting, String enumName, boolean on) {
        return Component.translatable("flowerbonemeal.option." + setting + "." + enumName.toLowerCase(Locale.ROOT))
                .withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED);
    }

    /** Grey out the settings while the whole mod is switched off. */
    private void updateSettingButtons() {
        boolean enabled = FlowerBoneMealConfig.get().modEnabled;
        for (Button button : settingButtons) button.active = enabled;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
        for (Row row : rows) {
            int color = row.button().active ? 0xFFFFFFFF : 0xFF808080;
            graphics.text(this.font, row.label().getVisualOrderText(), this.left, row.y() + 6, color, true);
        }
    }

    @Override
    public void onClose() {
        FlowerBoneMealConfig.save();
        this.minecraft.gui.setScreen(this.parent);
    }
}
