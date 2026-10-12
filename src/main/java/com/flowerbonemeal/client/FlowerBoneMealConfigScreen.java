package com.flowerbonemeal.client;

import com.flowerbonemeal.FlowerBoneMealConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Settings screen opened from Mod Menu's "Configure" button.
 * Each row has its name on the left and a Yes/No button on the right.
 * The first row turns the whole mod on or off; while it is off, the other rows are greyed out.
 */
public class FlowerBoneMealConfigScreen extends Screen {

    private static final int ROW_HEIGHT = 25;
    private static final int TOGGLE_WIDTH = 100;
    private static final int TOP = 40;
    private static final Component YES = Component.translatable("flowerbonemeal.option.yes").withStyle(ChatFormatting.GREEN);
    private static final Component NO = Component.translatable("flowerbonemeal.option.no").withStyle(ChatFormatting.RED);

    /** One row on the screen: its label, where it sits, and its button. */
    private record Row(Component label, int y, Button button) {}

    private final Screen parent;
    private final List<Row> rows = new ArrayList<>();
    private final List<Button> featureButtons = new ArrayList<>();
    private int left;

    public FlowerBoneMealConfigScreen(Screen parent) {
        super(Component.translatable("flowerbonemeal.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        rows.clear();
        featureButtons.clear();
        FlowerBoneMealConfig config = FlowerBoneMealConfig.get();

        int panelWidth = Math.min(this.width - 40, 440);
        this.left = (this.width - panelWidth) / 2;
        int toggleX = this.left + panelWidth - TOGGLE_WIDTH;

        // Row 1: master switch. Turning it off makes bone meal behave exactly like vanilla.
        addRow("mod_enabled", toggleX, 0,
                () -> config.modEnabled,
                value -> {
                    config.modEnabled = value;
                    updateFeatureButtons();
                },
                false);

        // Rows 2-5: the per-flower toggles.
        addRow("flowering_azalea", toggleX, 1,
                () -> config.floweringAzaleaDropsCopy, value -> config.floweringAzaleaDropsCopy = value, true);
        addRow("mangrove_propagule", toggleX, 2,
                () -> config.mangrovePropaguleDropsCopy, value -> config.mangrovePropaguleDropsCopy = value, true);
        addRow("pink_petals_wildflowers", toggleX, 3,
                () -> config.pinkPetalsAndWildflowersDropCopy, value -> config.pinkPetalsAndWildflowersDropCopy = value, true);
        addRow("wither_rose", toggleX, 4,
                () -> config.witherRoseDropsCopy, value -> config.witherRoseDropsCopy = value, true);

        updateFeatureButtons();

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .pos(this.width / 2 - 100, this.height - 28).size(200, 20).build());
    }

    private void addRow(String name, int toggleX, int index, Supplier<Boolean> getter,
                        Consumer<Boolean> setter, boolean isFeature) {
        String key = "flowerbonemeal.option." + name;
        int y = TOP + index * ROW_HEIGHT;

        Button button = Button.builder(getter.get() ? YES : NO, b -> {
                    boolean newValue = !getter.get();
                    setter.accept(newValue);
                    b.setMessage(newValue ? YES : NO);
                    FlowerBoneMealConfig.save(); // write config/flowerbonemeal.json immediately
                })
                .pos(toggleX, y).size(TOGGLE_WIDTH, 20).build();
        button.setTooltip(Tooltip.create(Component.translatable(key + ".tooltip")));

        this.addRenderableWidget(button);
        rows.add(new Row(Component.translatable(key), y, button));
        if (isFeature) featureButtons.add(button);
    }

    /** Grey out the per-flower toggles while the whole mod is switched off. */
    private void updateFeatureButtons() {
        boolean enabled = FlowerBoneMealConfig.get().modEnabled;
        for (Button button : featureButtons) button.active = enabled;
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
