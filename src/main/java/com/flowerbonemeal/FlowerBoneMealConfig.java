package com.flowerbonemeal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** The mod's settings, stored in .minecraft/config/flowerbonemeal.json. */
public final class FlowerBoneMealConfig {

    /** Choices for the azalea setting. */
    public enum AzaleaMode {
        /** Flowering azalea drops a copy; regular azalea grows a tree (vanilla). */
        FLOWERING_ONLY,
        /** Both drop a copy (neither grows a tree from bone meal). */
        FLOWERING_AND_REGULAR,
        /** Neither drops a copy; both grow a tree (vanilla). */
        NEITHER;

        public AzaleaMode next() { return values()[(ordinal() + 1) % values().length]; }
    }

    /** Choices for the wither rose and golden dandelion setting. */
    public enum WitherGoldenMode {
        WITHER_ROSE_ONLY,
        GOLDEN_DANDELION_ONLY,
        BOTH,
        OFF;

        public WitherGoldenMode next() { return values()[(ordinal() + 1) % values().length]; }
        public boolean witherRose() { return this == WITHER_ROSE_ONLY || this == BOTH; }
        public boolean goldenDandelion() { return this == GOLDEN_DANDELION_ONLY || this == BOTH; }
    }

    /** Master switch. false = the whole mod is off and bone meal works exactly like vanilla. */
    public boolean modEnabled = true;

    /** true = mangrove propagule drops a copy; false = vanilla (grows toward a tree). */
    public boolean mangrovePropaguleDropsCopy = false;

    public AzaleaMode azaleaMode = AzaleaMode.FLOWERING_ONLY;

    /** true = torchflower and pitcher plant drop a copy; false = bone meal does nothing to them (vanilla). */
    public boolean torchflowerAndPitcherPlantDropCopy = true;

    public WitherGoldenMode witherRoseAndGoldenDandelion = WitherGoldenMode.BOTH;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve(FlowerBoneMeal.MOD_ID + ".json");
    private static FlowerBoneMealConfig instance;

    public static FlowerBoneMealConfig get() {
        if (instance == null) load();
        return instance;
    }

    public static synchronized void load() {
        FlowerBoneMealConfig loaded = null;
        if (Files.exists(FILE)) {
            try (Reader reader = Files.newBufferedReader(FILE)) {
                loaded = GSON.fromJson(reader, FlowerBoneMealConfig.class);
            } catch (Exception e) {
                System.err.println("[flowerbonemeal] Could not read " + FILE + ", using defaults: " + e);
            }
        }
        instance = loaded != null ? loaded : new FlowerBoneMealConfig();
        // A misspelled mode in a hand-edited file loads as null; fall back to the default.
        if (instance.azaleaMode == null) instance.azaleaMode = AzaleaMode.FLOWERING_ONLY;
        if (instance.witherRoseAndGoldenDandelion == null) instance.witherRoseAndGoldenDandelion = WitherGoldenMode.BOTH;
        save(); // writes the file on first run, and tidies up settings from older versions
    }

    public static synchronized void save() {
        if (instance == null) return;
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(instance, writer);
            }
        } catch (IOException e) {
            System.err.println("[flowerbonemeal] Could not save " + FILE + ": " + e);
        }
    }
}
