package com.lunarxo.veilbound.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

public class VeilArrowEntity extends Arrow {
    public VeilArrowEntity(EntityType<? extends VeilArrowEntity> type, Level level) {
        super(type, level);
    }
}
