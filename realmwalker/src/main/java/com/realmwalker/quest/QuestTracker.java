package com.realmwalker.quest;

import java.util.HashMap;
import java.util.Map;

import com.realmwalker.RealmwalkerConfig;
import com.realmwalker.registry.ModTriggers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.HolderLookup;

import com.mojang.serialization.Codec;

public final class QuestTracker {
    private static final String KEY_PREFIX = "realmwalker:quest:";

    private QuestTracker() {
    }

    public static int get(Player player, String event) {
        if (RealmwalkerConfig.sharedProgress && player.level().getServer() != null) {
            return SharedProgress.get(player.level().getServer()).data.getOrDefault(event, 0);
        }
        return player.getPersistentData().getInt(KEY_PREFIX + event);
    }

    public static void set(Player player, String event, int value) {
        if (RealmwalkerConfig.sharedProgress && player.level().getServer() != null) {
            SharedProgress sp = SharedProgress.get(player.level().getServer());
            sp.data.put(event, value);
            sp.setDirty();
            return;
        }
        player.getPersistentData().putInt(KEY_PREFIX + event, value);
    }

    /** Increments the counter for the event and fires the generic trigger with the new total. */
    public static void add(ServerPlayer player, String event, int amount) {
        int total = get(player, event) + amount;
        set(player, event, total);
        ModTriggers.GENERIC.get().trigger(player, event, total);
    }

    /** Fires the generic trigger for an event without changing the counter (one-shot events). */
    public static void fire(ServerPlayer player, String event) {
        ModTriggers.GENERIC.get().trigger(player, event, Math.max(get(player, event), 1));
    }

    public static class SharedProgress extends SavedData {
        public static final Codec<SharedProgress> CODEC = Codec.unboundedMap(Codec.STRING, Codec.INT)
                .xmap(map -> {
                    SharedProgress sp = new SharedProgress();
                    sp.data.putAll(map);
                    return sp;
                }, sp -> new HashMap<>(sp.data));

        public static final SavedDataType<SharedProgress> TYPE =
                new SavedDataType<>("realmwalker_shared_progress", SharedProgress::new, CODEC, null);

        public final Map<String, Integer> data = new HashMap<>();

        public static SharedProgress get(MinecraftServer server) {
            return server.overworld().getDataStorage().computeIfAbsent(TYPE);
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            data.forEach(tag::putInt);
            return tag;
        }
    }
}
