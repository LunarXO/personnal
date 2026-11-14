package com.lunarxo.veilbound.block.entity;

import com.lunarxo.veilbound.VeilboundConfig;
import com.lunarxo.veilbound.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class GravityAnchorBlockEntity extends BlockEntity {
    public GravityAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRAVITY_ANCHOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GravityAnchorBlockEntity blockEntity) {
        if (level.isClientSide || level.getGameTime() % 10L != 0L) {
            return;
        }
        AABB box = new AABB(pos).inflate(VeilboundConfig.gravityAnchorRadius);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {
            entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false, true));
            entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 0, false, false, true));
        }
    }
}
