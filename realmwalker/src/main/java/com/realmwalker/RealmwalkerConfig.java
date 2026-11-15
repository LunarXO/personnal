package com.realmwalker;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Realmwalker.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RealmwalkerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue REQUIRE_PREVIOUS_BOSS = BUILDER
            .comment("If true, a Waystone portal to a dimension can only be lit after the previous dimension's boss advancement.")
            .define("requirePreviousBoss", true);

    private static final ModConfigSpec.BooleanValue SHARED_PROGRESS = BUILDER
            .comment("If true, quest progress is shared by all players on the server. If false, each player tracks their own progress.")
            .define("sharedProgress", false);

    private static final ModConfigSpec.DoubleValue BOSS_HEALTH_MULTIPLIER = BUILDER
            .comment("Multiplier applied to every boss's max health.")
            .defineInRange("bossHealthMultiplier", 1.0, 0.1, 10.0);

    private static final ModConfigSpec.BooleanValue ENABLE_COLD_METER = BUILDER
            .comment("If true, the Frostmarrow cold meter freezes exposed players over time unless they wear a Parka or stand near warmth.")
            .define("enableColdMeter", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean requirePreviousBoss = true;
    public static boolean sharedProgress = false;
    public static double bossHealthMultiplier = 1.0;
    public static boolean enableColdMeter = true;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        requirePreviousBoss = REQUIRE_PREVIOUS_BOSS.get();
        sharedProgress = SHARED_PROGRESS.get();
        bossHealthMultiplier = BOSS_HEALTH_MULTIPLIER.get();
        enableColdMeter = ENABLE_COLD_METER.get();
    }
}
