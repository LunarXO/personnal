package com.realmwalker.block;

import com.realmwalker.quest.QuestTracker;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

/** An unlit beacon fire site in Frostmarrow. Light it with flint and steel. */
public class BeaconFireSiteBlock extends Block {
    public BeaconFireSiteBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }
        if (!stack.is(Items.FLINT_AND_STEEL)) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            BlockState campfire = Blocks.CAMPFIRE.defaultBlockState().setValue(BlockStateProperties.LIT, true);
            level.setBlockAndUpdate(pos, campfire);
            QuestTracker.add(serverPlayer, "frostmarrow_beacons", 1);
            level.playSound(null, pos, ModSoundEvents.SPARK_IGNITE.get(), SoundSource.BLOCKS, 1.0F, 0.7F);
            stack.hurtAndBreak(1, serverPlayer, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        }
        return InteractionResult.CONSUME;
    }
}
