package com.lunarxo.veilbound.block.entity;

import com.lunarxo.veilbound.VeilboundConfig;
import com.lunarxo.veilbound.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ResonantSpireBlockEntity extends BlockEntity {
    public ResonantSpireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANT_SPIRE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ResonantSpireBlockEntity blockEntity) {
        if (level.getGameTime() % 20L != 0L) {
            return;
        }
        if (!level.isClientSide) {
            AABB box = new AABB(pos).inflate(VeilboundConfig.resonantSpireRadius);
            for (Player player : level.getEntitiesOfClass(Player.class, box)) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, false, false, true));
            }
        }
    }
}
