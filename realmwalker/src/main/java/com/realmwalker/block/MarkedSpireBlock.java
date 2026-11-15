package com.realmwalker.block;

import com.realmwalker.quest.QuestTracker;
import com.realmwalker.registry.ModBlocks;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** A marked spire in Stormspire. Placing a Lightning Rod item on it charges the spire and counts toward the quest. */
public class MarkedSpireBlock extends Block {
    public MarkedSpireBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }
        if (!stack.is(ModBlocks.LIGHTNING_ROD.get().asItem())) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            level.setBlockAndUpdate(pos.above(), ModBlocks.LIGHTNING_ROD.get().defaultBlockState());
            QuestTracker.add(serverPlayer, "stormspire_rods", 1);
            level.playSound(null, pos, ModSoundEvents.LIGHTNING_CHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.5F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.CONSUME;
    }
}
