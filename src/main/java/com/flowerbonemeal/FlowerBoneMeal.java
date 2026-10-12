package com.flowerbonemeal;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class FlowerBoneMeal {
    public static final String MOD_ID = "flowerbonemeal";

    /** Every block in this tag drops a copy of itself when bone-mealed (subject to the toggles). */
    public static final TagKey<Block> DUPLICATES_WITH_BONE_MEAL = tag("duplicates_with_bone_meal");

    /** Always keeps vanilla bone meal behaviour, whatever else applies. Empty by default. */
    public static final TagKey<Block> KEEPS_VANILLA_BONE_MEAL = tag("keeps_vanilla_bone_meal");

    /**
     * Manual overrides for modded flowers whose bone meal behaviour can't be detected from code.
     * Anything in one of these is controlled by the matching toggle.
     */
    public static final TagKey<Block> BEHAVES_LIKE_FLOWERING_AZALEA = tag("behaves_like_flowering_azalea");
    public static final TagKey<Block> BEHAVES_LIKE_MANGROVE_PROPAGULE = tag("behaves_like_mangrove_propagule");
    public static final TagKey<Block> BEHAVES_LIKE_PINK_PETALS = tag("behaves_like_pink_petals");

    /** Which toggle (if any) controls a flower. */
    public enum Group { FLOWERING_AZALEA, MANGROVE_PROPAGULE, PINK_PETALS, OTHER }

    /**
     * Sorts a flower into a group. Tags are checked first; otherwise the block's code type is
     * used, so modded flowers built on the same vanilla classes are picked up automatically:
     * - grows in stages like a propagule  -> MangrovePropaguleBlock (checked first: it is also a sapling)
     * - adds petals like pink petals       -> FlowerBedBlock (pink petals, wildflowers)
     * - grows into a tree like azalea      -> AzaleaBlock or SaplingBlock
     */
    public static Group groupOf(BlockState state) {
        if (state.is(BEHAVES_LIKE_MANGROVE_PROPAGULE)) return Group.MANGROVE_PROPAGULE;
        if (state.is(BEHAVES_LIKE_PINK_PETALS)) return Group.PINK_PETALS;
        if (state.is(BEHAVES_LIKE_FLOWERING_AZALEA)) return Group.FLOWERING_AZALEA;

        Block block = state.getBlock();
        if (block instanceof MangrovePropaguleBlock) return Group.MANGROVE_PROPAGULE;
        if (block instanceof FlowerBedBlock) return Group.PINK_PETALS;
        if (block instanceof AzaleaBlock || block instanceof SaplingBlock) return Group.FLOWERING_AZALEA;
        return Group.OTHER;
    }

    /** True if bone meal on this flower should drop a copy, false if vanilla behaviour should run. */
    public static boolean shouldDropCopy(BlockState state) {
        if (!state.is(DUPLICATES_WITH_BONE_MEAL) || state.is(KEEPS_VANILLA_BONE_MEAL)) return false;
        FlowerBoneMealConfig config = FlowerBoneMealConfig.get();
        return switch (groupOf(state)) {
            case FLOWERING_AZALEA -> config.floweringAzaleaDropsCopy;
            case MANGROVE_PROPAGULE -> config.mangrovePropaguleDropsCopy;
            case PINK_PETALS -> config.pinkPetalsAndWildflowersDropCopy;
            case OTHER -> true;
        };
    }

    private static TagKey<Block> tag(String path) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, path));
    }

    private FlowerBoneMeal() {}
}
