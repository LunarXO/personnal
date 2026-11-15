package com.realmwalker.block;

import com.realmwalker.ModDimensions;
import com.realmwalker.entity.BossBase;
import com.realmwalker.quest.QuestTracker;
import com.realmwalker.registry.ModBlocks;
import com.realmwalker.registry.ModEntities;
import com.realmwalker.registry.ModItems;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

/**
 * The heart of a boss arena. Right-clicking it records reaching the arena; right-clicking with the
 * dimension's key item summons the boss.
 */
public class ArenaHeartBlock extends Block {
    public ArenaHeartBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return activate(state, level, pos, player, ItemStack.EMPTY);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return activate(state, level, pos, player, stack);
    }

    private InteractionResult activate(BlockState state, Level level, BlockPos pos, Player player, ItemStack held) {
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        var dim = level.dimension();
        String keyEvent = switch (dim.location().getPath()) {
            case "lumenfen" -> "lumenfen_reach_arena";
            case "saltglass_expanse" -> "saltglass_reach_arena";
            case "cogspire" -> "cogspire_reach_arena";
            case "frostmarrow" -> "frostmarrow_reach_arena";
            case "stormspire" -> "stormspire_reach_arena";
            default -> null;
        };
        if (keyEvent != null) {
            QuestTracker.fire(serverPlayer, keyEvent);
        }

        boolean needsKey = dim.equals(ModDimensions.LUMENFEN) || dim.equals(ModDimensions.SALTGLASS_EXPANSE);
        boolean hasKey = dim.equals(ModDimensions.LUMENFEN) && held.is(ModItems.MURK_KEY.get())
                || dim.equals(ModDimensions.SALTGLASS_EXPANSE) && held.is(ModItems.PRISM_KEY.get());
        if (needsKey && !hasKey) {
            player.displayClientMessage(Component.translatable("message.realmwalker.arena_needs_key"), true);
            return InteractionResult.CONSUME;
        }

        BossBase boss = switch (dim.location().getPath()) {
            case "lumenfen" -> ModEntities.MIREFATHER.get().create(serverLevel);
            case "saltglass_expanse" -> ModEntities.PRISM_COLOSSUS.get().create(serverLevel);
            case "cogspire" -> ModEntities.GRAND_MECHANIST.get().create(serverLevel);
            case "frostmarrow" -> ModEntities.RIBCAGE_WYRM.get().create(serverLevel);
            case "stormspire" -> ModEntities.TEMPEST_SOVEREIGN.get().create(serverLevel);
            default -> null;
        };
        if (boss == null) {
            return InteractionResult.PASS;
        }
        boss.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, player.getYRot() + 180.0F, 0.0F);
        serverLevel.addFreshEntity(boss);
        boss.roar();
        level.playSound(null, pos, ModSoundEvents.BOSS_ROAR.get(), SoundSource.HOSTILE, 2.0F, 0.6F);
        if (!player.getAbilities().instabuild && needsKey) {
            held.shrink(1);
        }
        level.setBlockAndUpdate(pos, ModBlocks.ARENA_HEART_SPENT.get().defaultBlockState());
        return InteractionResult.CONSUME;
    }
}
