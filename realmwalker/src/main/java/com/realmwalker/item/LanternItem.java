package com.realmwalker.item;

import com.realmwalker.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * A throwable/placeable light source (Spore Lantern, Aurora Lantern).
 * useOn places the lantern block; using in the air throws it, and it places itself where it lands.
 */
public class LanternItem extends BlockItem {
    public LanternItem(Block block, Properties properties) {
        super(block, properties.stacksTo(16));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        if (level.getBlockState(pos).canBeReplaced() && this.getBlock().defaultBlockState().canSurvive(level, pos)) {
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos, this.getBlock().defaultBlockState());
                level.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                Player player = context.getPlayer();
                if (player == null || !player.getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    /** Places the lantern block when thrown and it lands. */
    public static void placeWhereLanded(Level level, BlockPos pos, Block lantern) {
        if (!level.isClientSide && level.getBlockState(pos).canBeReplaced()) {
            level.setBlockAndUpdate(pos, lantern.defaultBlockState());
            level.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static boolean isLanternOf(Block block, net.minecraft.world.item.ItemStack stack) {
        return stack.getItem() instanceof LanternItem lanternItem && lanternItem.getBlock() == block;
    }
}
