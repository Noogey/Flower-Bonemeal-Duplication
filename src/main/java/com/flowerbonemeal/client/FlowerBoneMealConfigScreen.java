package com.flowerbonemeal.client;

import com.flowerbonemeal.FlowerBoneMealConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/** Settings screen opened from Mod Menu's "Configure" button. Built like Mod Menu's own options screen. */
public class FlowerBoneMealConfigScreen extends OptionsSubScreen {

    private static final Component TRUE = Component.translatable("flowerbonemeal.option.true");
    private static final Component FALSE = Component.translatable("flowerbonemeal.option.false");

    public FlowerBoneMealConfigScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("flowerbonemeal.config.title"));
    }

    @Override
    protected void addOptions() {
        if (this.list == null) return;
        FlowerBoneMealConfig config = FlowerBoneMealConfig.get();
        this.list.addBig(toggle("flowering_azalea", config.floweringAzaleaDropsCopy,
                value -> config.floweringAzaleaDropsCopy = value));
        this.list.addBig(toggle("mangrove_propagule", config.mangrovePropaguleDropsCopy,
                value -> config.mangrovePropaguleDropsCopy = value));
        this.list.addBig(toggle("pink_petals_wildflowers", config.pinkPetalsAndWildflowersDropCopy,
                value -> config.pinkPetalsAndWildflowersDropCopy = value));
    }

    private static OptionInstance<Boolean> toggle(String name, boolean current, Consumer<Boolean> setter) {
        String key = "flowerbonemeal.option." + name;
        Component description = Component.translatable(key + ".tooltip");
        return new OptionInstance<>(
                key,
                value -> Tooltip.create(description),
                (caption, value) -> value ? TRUE : FALSE,
                OptionInstance.BOOLEAN_VALUES,
                current,
                newValue -> {
                    setter.accept(newValue);
                    FlowerBoneMealConfig.save(); // write config/flowerbonemeal.json immediately
                });
    }

    @Override
    public void removed() {
        FlowerBoneMealConfig.save();
    }
}
