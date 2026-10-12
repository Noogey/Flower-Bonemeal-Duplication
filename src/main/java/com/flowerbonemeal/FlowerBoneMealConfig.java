package com.flowerbonemeal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The mod's settings, stored in .minecraft/config/flowerbonemeal.json.
 * true  = bone meal makes the flower drop a copy of itself
 * false = bone meal does what it does in vanilla
 */
public final class FlowerBoneMealConfig {

    /** Master switch. false = the whole mod is off and bone meal works exactly like vanilla. */
    public boolean modEnabled = true;

    /** Flowering azalea (and modded flowers that grow into a tree when bone-mealed). */
    public boolean floweringAzaleaDropsCopy = true;

    /** Mangrove propagule (and modded flowers that grow in stages when bone-mealed). */
    public boolean mangrovePropaguleDropsCopy = false;

    /** Pink petals and wildflowers (and modded flowers that add petals when bone-mealed). */
    public boolean pinkPetalsAndWildflowersDropCopy = true;

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
        save(); // writes the file on first run, and adds any options missing from an older file
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
