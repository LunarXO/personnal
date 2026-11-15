package com.realmwalker;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class ModDimensions {
    public static final ResourceKey<Level> LUMENFEN = ResourceKey.create(Registries.DIMENSION, Realmwalker.id("lumenfen"));
    public static final ResourceKey<Level> SALTGLASS_EXPANSE = ResourceKey.create(Registries.DIMENSION, Realmwalker.id("saltglass_expanse"));
    public static final ResourceKey<Level> COGSPIRE = ResourceKey.create(Registries.DIMENSION, Realmwalker.id("cogspire"));
    public static final ResourceKey<Level> FROSTMARROW = ResourceKey.create(Registries.DIMENSION, Realmwalker.id("frostmarrow"));
    public static final ResourceKey<Level> STORMSPIRE = ResourceKey.create(Registries.DIMENSION, Realmwalker.id("stormspire"));
    public static final ResourceKey<Level> EVERBLOOM = ResourceKey.create(Registries.DIMENSION, Realmwalker.id("everbloom"));

    /** The five adventure dimensions, in fixed progression order. Index 0 is the first. */
    public static final ResourceKey<Level>[] ADVENTURE_DIMS = new ResourceKey[] {
            LUMENFEN, SALTGLASS_EXPANSE, COGSPIRE, FROSTMARROW, STORMSPIRE
    };

    public static int indexOf(ResourceKey<Level> dim) {
        for (int i = 0; i < ADVENTURE_DIMS.length; i++) {
            if (ADVENTURE_DIMS[i].equals(dim)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isAdventureDim(ResourceKey<Level> dim) {
        return indexOf(dim) >= 0;
    }

    private ModDimensions() {
    }
}
