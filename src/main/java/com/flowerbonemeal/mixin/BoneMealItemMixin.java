package com.flowerbonemeal.mixin;

import com.flowerbonemeal.FlowerBoneMeal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hooks the one method that both players and dispensers go through when applying bone meal.
 * If the target is a flower (and its toggle is on), it drops a copy of itself and the flower's
 * normal bone meal behaviour is skipped. Otherwise vanilla runs untouched.
 */
@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin {

    @Inject(method = "growCrop", at = @At("HEAD"), cancellable = true)
    private static void flowerbonemeal$duplicateFlower(ItemStack boneMeal, Level level, BlockPos pos,
                                                       CallbackInfoReturnable<Boolean> cir) {
        BlockState state = level.getBlockState(pos);
        if (!FlowerBoneMeal.shouldDropCopy(state)) return;

        ItemStack copy = new ItemStack(state.getBlock().asItem());
        if (copy.isEmpty()) return; // block has no item form; fall back to vanilla

        // Only the server changes the world; the client just needs "true" so the hand swings
        // and the bone meal sound and particles play (particles are hidden for some blocks, below).
        if (level instanceof ServerLevel) {
            Block.popResource(level, pos, copy);
            boneMeal.shrink(1);
        }
        cir.setReturnValue(true);
    }

    /**
     * Hides the green "plant growth" particles for the mangrove propagule and azaleas while they
     * are set to drop a copy. The bone meal sound still plays. Runs on the client, where the
     * particles are drawn.
     *
     * The full method signature is written out and require = 0 is set, so if a future game
     * version changes this method, the hook is skipped (particles show) instead of crashing.
     */
    @Inject(method = "addGrowthParticles(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;I)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    private static void flowerbonemeal$hideParticles(LevelAccessor level, BlockPos pos, int data, CallbackInfo ci) {
        if (FlowerBoneMeal.hidesGrowthParticles(level.getBlockState(pos))) {
            ci.cancel();
        }
    }
}
