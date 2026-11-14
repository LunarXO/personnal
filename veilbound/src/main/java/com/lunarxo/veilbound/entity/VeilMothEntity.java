package com.lunarxo.veilbound.entity;

import com.lunarxo.veilbound.ModDimensions;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class VeilMothEntity extends Bat {
    public VeilMothEntity(EntityType<? extends Bat> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Bat.createAttributes();
    }

    public static boolean checkVeilSpawn(EntityType<? extends VeilMothEntity> type, ServerLevelAccessor level,
            MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getLevel().dimension() == ModDimensions.THE_VEIL
                && level.getLevel().getMaxLocalRawBrightness(pos) < 8;
    }
}
