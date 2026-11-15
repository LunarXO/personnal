package com.realmwalker.block;

import com.realmwalker.quest.QuestTracker;
import com.realmwalker.registry.ModItems;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** A buried ruin in the Saltglass Expanse. Excavating (right-clicking) it yields loot and quest progress. */
public class BuriedRuinBlock extends Block {
    public BuriedRuinBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            drop(level, pos, new ItemStack(Items.QUARTZ, 2));
            if (level.getRandom().nextInt(2) == 0) {
                drop(level, pos, new ItemStack(ModItems.PRISM_SHARD.get()));
            }
            QuestTracker.add(serverPlayer, "saltglass_ruins", 1);
            level.playSound(null, pos, ModSoundEvents.SHRINE_LOOT.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        return InteractionResult.CONSUME;
    }

    private static void drop(Level level, BlockPos pos, ItemStack stack) {
        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack));
    }
}
