package com.lunarxo.veilbound.client;

import com.lunarxo.veilbound.Veilbound;
import com.lunarxo.veilbound.client.particle.ShardSparkleParticle;
import com.lunarxo.veilbound.registry.ModEntities;
import com.lunarxo.veilbound.registry.ModParticles;

import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = Veilbound.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VeilboundClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.GLIMMERFOX.get(),
                ctx -> mobRenderer(ctx, new FoxModel(ctx.bakeLayer(ModelLayers.FOX)), 0.4F, Veilbound.id("textures/entity/glimmerfox.png")));
        event.registerEntityRenderer(ModEntities.SHARDLING.get(),
                ctx -> mobRenderer(ctx, new SlimeModel(ctx.bakeLayer(ModelLayers.SLIME)), 0.2F, Veilbound.id("textures/entity/shardling.png")));
        event.registerEntityRenderer(ModEntities.VEIL_MOTH.get(),
                ctx -> mobRenderer(ctx, new BatModel(ctx.bakeLayer(ModelLayers.BAT)), 0.3F, Veilbound.id("textures/entity/veil_moth.png")));
        event.registerEntityRenderer(ModEntities.HOLLOW_HUSK.get(),
                ctx -> mobRenderer(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, Veilbound.id("textures/entity/hollow_husk.png")));
        event.registerEntityRenderer(ModEntities.VEIL_REAVER.get(),
                ctx -> mobRenderer(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.6F, Veilbound.id("textures/entity/veil_reaver.png")));
        event.registerEntityRenderer(ModEntities.EMBER_WISP.get(),
                ctx -> mobRenderer(ctx, new BlazeModel(ctx.bakeLayer(ModelLayers.BLAZE)), 0.3F, Veilbound.id("textures/entity/ember_wisp.png")));
        event.registerEntityRenderer(ModEntities.CRYSTAL_GOLEM.get(),
                ctx -> mobRenderer(ctx, new IronGolemModel(ctx.bakeLayer(ModelLayers.IRON_GOLEM)), 0.9F, Veilbound.id("textures/entity/crystal_golem.png")));
        event.registerEntityRenderer(ModEntities.HOLLOW_KING.get(),
                ctx -> mobRenderer(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.8F, Veilbound.id("textures/entity/hollow_king.png")));

        event.registerEntityRenderer(ModEntities.EMBER_ORB.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(ModEntities.SHARD_BOLT.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(ModEntities.GLOOM_ORB.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(ModEntities.STORM_BOLT.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(ModEntities.FIREBALL.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(ModEntities.VEIL_ARROW.get(), VeilArrowRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SHARD_SPARKLE.get(), ShardSparkleParticle.Provider::new);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Mob> EntityRenderer<T> mobRenderer(EntityRendererProvider.Context ctx, EntityModel<?> model, float shadow, ResourceLocation texture) {
        return (EntityRenderer<T>) new SimpleMobRenderer<Mob>((EntityModel<Mob>) model, ctx, shadow, texture);
    }

    private static class SimpleMobRenderer<T extends Mob> extends MobRenderer<T, EntityModel<T>> {
        private final ResourceLocation texture;

        SimpleMobRenderer(EntityModel<T> model, EntityRendererProvider.Context ctx, float shadow, ResourceLocation texture) {
            super(ctx, model, shadow);
            this.texture = texture;
        }

        @Override
        public ResourceLocation getTextureLocation(T entity) {
            return this.texture;
        }
    }
}
