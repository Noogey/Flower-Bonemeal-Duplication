package com.flowerbonemeal;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class FlowerBoneMeal {
    public static final String MOD_ID = "flowerbonemeal";

    /** Blocks the mod may duplicate (all flowers; vanilla-handled ones are filtered out below). */
    public static final TagKey<Block> DUPLICATES_WITH_BONE_MEAL = tag("duplicates_with_bone_meal");

    /**
     * Always left to vanilla. By default: lilac, peony, rose bush and sunflower (vanilla already
     * drops a copy) and pink petals and wildflowers (vanilla already grows a petal).
     */
    public static final TagKey<Block> KEEPS_VANILLA_BONE_MEAL = tag("keeps_vanilla_bone_meal");

    // Which setting controls a block. A datapack can add modded flowers to these.
    public static final TagKey<Block> MANGROVE_PROPAGULE_SETTING = tag("behaves_like_mangrove_propagule");
    public static final TagKey<Block> FLOWERING_AZALEA_SETTING = tag("behaves_like_flowering_azalea");
    public static final TagKey<Block> REGULAR_AZALEA_SETTING = tag("behaves_like_azalea");
    public static final TagKey<Block> TORCHFLOWER_PITCHER_SETTING = tag("torchflower_and_pitcher_plant");
    public static final TagKey<Block> WITHER_ROSE_SETTING = tag("wither_rose");
    public static final TagKey<Block> GOLDEN_DANDELION_SETTING = tag("golden_dandelion");

    /** Which setting (if any) controls a flower. */
    public enum Group {
        VANILLA,             // never touched by the mod
        MANGROVE_PROPAGULE,
        FLOWERING_AZALEA,
        REGULAR_AZALEA,
        TORCHFLOWER_PITCHER,
        WITHER_ROSE,
        GOLDEN_DANDELION,
        ALWAYS               // every other flower: always drops a copy
    }

    /**
     * Sorts a flower into a group. Tags are checked first, then the block's code type, so modded
     * flowers built on the same vanilla classes are picked up automatically:
     * - adds petals like pink petals          (FlowerBedBlock)              -> vanilla
     * - tall flower that already drops a copy (bone-mealable DoublePlant)   -> vanilla
     * - grows in stages like a propagule      (MangrovePropaguleBlock)      -> mangrove setting
     * - grows into a tree like azalea         (AzaleaBlock / SaplingBlock)  -> flowering azalea mode
     */
    public static Group groupOf(BlockState state) {
        if (state.is(KEEPS_VANILLA_BONE_MEAL)) return Group.VANILLA;
        if (state.is(MANGROVE_PROPAGULE_SETTING)) return Group.MANGROVE_PROPAGULE;
        if (state.is(REGULAR_AZALEA_SETTING)) return Group.REGULAR_AZALEA;
        if (state.is(FLOWERING_AZALEA_SETTING)) return Group.FLOWERING_AZALEA;
        if (state.is(TORCHFLOWER_PITCHER_SETTING)) return Group.TORCHFLOWER_PITCHER;
        if (state.is(WITHER_ROSE_SETTING)) return Group.WITHER_ROSE;
        if (state.is(GOLDEN_DANDELION_SETTING)) return Group.GOLDEN_DANDELION;

        Block block = state.getBlock();
        if (block instanceof FlowerBedBlock) return Group.VANILLA;
        if (block instanceof DoublePlantBlock && block instanceof BonemealableBlock) return Group.VANILLA;
        if (block instanceof MangrovePropaguleBlock) return Group.MANGROVE_PROPAGULE;
        if (block instanceof AzaleaBlock || block instanceof SaplingBlock) return Group.FLOWERING_AZALEA;
        return Group.ALWAYS;
    }

    /** True if bone meal on this block should drop a copy, false if vanilla behaviour should run. */
    public static boolean shouldDropCopy(BlockState state) {
        FlowerBoneMealConfig config = FlowerBoneMealConfig.get();
        if (!config.modEnabled) return false; // mod switched off: vanilla behaviour everywhere
        if (!state.is(DUPLICATES_WITH_BONE_MEAL)) return false;
        return switch (groupOf(state)) {
            case VANILLA -> false;
            case MANGROVE_PROPAGULE -> config.mangrovePropaguleDropsCopy;
            case FLOWERING_AZALEA -> config.azaleaMode != FlowerBoneMealConfig.AzaleaMode.NEITHER;
            case REGULAR_AZALEA -> config.azaleaMode == FlowerBoneMealConfig.AzaleaMode.FLOWERING_AND_REGULAR;
            case TORCHFLOWER_PITCHER -> config.torchflowerAndPitcherPlantDropCopy;
            case WITHER_ROSE -> config.witherRoseAndGoldenDandelion.witherRose();
            case GOLDEN_DANDELION -> config.witherRoseAndGoldenDandelion.goldenDandelion();
            case ALWAYS -> true;
        };
    }

    /**
     * True if the green bone meal particles should be hidden for this block: the mangrove
     * propagule and both azaleas, but only while their setting makes them drop a copy.
     */
    public static boolean hidesGrowthParticles(BlockState state) {
        if (!shouldDropCopy(state)) return false;
        Group group = groupOf(state);
        return group == Group.MANGROVE_PROPAGULE
                || group == Group.FLOWERING_AZALEA
                || group == Group.REGULAR_AZALEA;
    }

    private static TagKey<Block> tag(String path) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, path));
    }

    private FlowerBoneMeal() {}
}
