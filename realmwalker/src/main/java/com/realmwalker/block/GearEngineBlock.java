package com.realmwalker.block;

import com.realmwalker.quest.QuestTracker;
import com.realmwalker.registry.ModBlocks;
import com.realmwalker.registry.ModItems;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A broken Gear Engine in Cogspire. Repair it by right-clicking while holding a Wrench with Spring Coils in the inventory.
 */
public class GearEngineBlock extends Block {
    public GearEngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        boolean hasWrench = player.getMainHandItem().is(ModItems.WRENCH.get()) || player.getOffhandItem().is(ModItems.WRENCH.get());
        int coils = player.getInventory().countItem(ModItems.SPRING_COIL.get());
        if (!hasWrench || coils <= 0) {
            player.displayClientMessage(Component.translatable("message.realmwalker.engine_needs_wrench_coil"), true);
            return InteractionResult.CONSUME;
        }
        player.getInventory().clearOrCountMatchingItems(stack -> stack.is(ModItems.SPRING_COIL.get()), 1, player.inventoryMenu);
        QuestTracker.add(serverPlayer, "cogspire_engines", 1);
        level.playSound(null, pos, ModSoundEvents.SHRINE_LOOT.get(), SoundSource.BLOCKS, 1.0F, 1.2F);
        level.setBlockAndUpdate(pos, ModBlocks.COG_PLATING.get().defaultBlockState());
        return InteractionResult.CONSUME;
    }
}
