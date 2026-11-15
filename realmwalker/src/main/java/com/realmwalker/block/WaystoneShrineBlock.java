package com.realmwalker.block;

import com.realmwalker.quest.QuestTracker;
import com.realmwalker.registry.ModItems;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * An Overworld shrine containing a Waystone Shard. The first shrine looted also grants the Wayfarer's Codex.
 */
public class WaystoneShrineBlock extends Block {
    public WaystoneShrineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            drop(level, pos, new net.minecraft.world.item.ItemStack(ModItems.WAYSTONE_SHARD.get()));
            if (!serverPlayer.getPersistentData().getBoolean("realmwalker:got_codex")) {
                serverPlayer.getPersistentData().putBoolean("realmwalker:got_codex", true);
                drop(level, pos, new net.minecraft.world.item.ItemStack(ModItems.WAYFARERS_CODEX.get()));
                serverPlayer.displayClientMessage(Component.translatable("message.realmwalker.codex_found"), true);
            }
            QuestTracker.fire(serverPlayer, "overworld_shrine");
            level.playSound(null, pos, ModSoundEvents.SHRINE_LOOT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.CONSUME;
    }

    private static void drop(Level level, BlockPos pos, net.minecraft.world.item.ItemStack stack) {
        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack));
    }
}
