package com.lunarxo.veilbound;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Veilbound.MODID, bus = EventBusSubscriber.Bus.MOD)
public class VeilboundConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue PORTAL_COOLDOWN_TICKS = BUILDER
            .comment("Cooldown in ticks between two uses of a Veil portal")
            .defineInRange("portalCooldownTicks", 60, 0, 72000);

    private static final ModConfigSpec.IntValue GRAVITY_ANCHOR_RADIUS = BUILDER
            .comment("Radius in blocks of the Gravity Anchor's slow-falling aura")
            .defineInRange("gravityAnchorRadius", 8, 1, 64);

    private static final ModConfigSpec.IntValue RESONANT_SPIRE_RADIUS = BUILDER
            .comment("Radius in blocks of the Resonant Spire's regeneration aura")
            .defineInRange("resonantSpireRadius", 12, 1, 64);

    private static final ModConfigSpec.IntValue HOLLOW_KING_HEALTH = BUILDER
            .comment("Max health of the Hollow King boss")
            .defineInRange("hollowKingHealth", 300, 20, 10000);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int portalCooldownTicks = 60;
    public static int gravityAnchorRadius = 8;
    public static int resonantSpireRadius = 12;
    public static int hollowKingHealth = 300;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        portalCooldownTicks = PORTAL_COOLDOWN_TICKS.get();
        gravityAnchorRadius = GRAVITY_ANCHOR_RADIUS.get();
        resonantSpireRadius = RESONANT_SPIRE_RADIUS.get();
        hollowKingHealth = HOLLOW_KING_HEALTH.get();
    }
}
