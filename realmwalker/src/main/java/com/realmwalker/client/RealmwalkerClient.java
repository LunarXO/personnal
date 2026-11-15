package com.realmwalker.client;

import com.realmwalker.Realmwalker;
import com.realmwalker.client.gui.CodexScreen;
import com.realmwalker.registry.ModEntities;
import com.realmwalker.registry.ModMenuTypes;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Realmwalker.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RealmwalkerClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(ModEntities.SPORE_CRAWLER.get(), ctx -> mob(ctx, new SpiderModel(ctx.bakeLayer(ModelLayers.SPIDER)), 0.5F, 1.0F, Realmwalker.id("textures/entity/lumenfen/spore_crawler.png")));
        event.registerEntityRenderer(ModEntities.MIREWRAITH.get(), ctx -> mob(ctx, new VexModel(ctx.bakeLayer(ModelLayers.VEX)), 0.4F, 1.0F, Realmwalker.id("textures/entity/lumenfen/mirewraith.png")));
        event.registerEntityRenderer(ModEntities.CAPLING.get(), ctx -> mob(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, 1.0F, Realmwalker.id("textures/entity/lumenfen/capling.png")));
        event.registerEntityRenderer(ModEntities.BOG_LURKER.get(), ctx -> mob(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, 1.0F, Realmwalker.id("textures/entity/lumenfen/bog_lurker.png")));
        event.registerEntityRenderer(ModEntities.GLOW_MOTH.get(), ctx -> mob(ctx, new BlazeModel(ctx.bakeLayer(ModelLayers.BLAZE)), 0.4F, 1.0F, Realmwalker.id("textures/entity/lumenfen/glow_moth.png")));
        event.registerEntityRenderer(ModEntities.MIREFATHER.get(), ctx -> mob(ctx, new IronGolemModel(ctx.bakeLayer(ModelLayers.IRON_GOLEM)), 1.0F, 3.0F, Realmwalker.id("textures/entity/lumenfen/mirefather.png")));
        event.registerEntityRenderer(ModEntities.GLASS_SCORPION.get(), ctx -> mob(ctx, new SpiderModel(ctx.bakeLayer(ModelLayers.SPIDER)), 0.5F, 1.0F, Realmwalker.id("textures/entity/saltglass_expanse/glass_scorpion.png")));
        event.registerEntityRenderer(ModEntities.MIRAGE_PHANTOM.get(), ctx -> mob(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, 1.0F, Realmwalker.id("textures/entity/saltglass_expanse/mirage_phantom.png")));
        event.registerEntityRenderer(ModEntities.SALT_HUSK.get(), ctx -> mob(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, 1.0F, Realmwalker.id("textures/entity/saltglass_expanse/salt_husk.png")));
        event.registerEntityRenderer(ModEntities.DUNE_STRIDER.get(), ctx -> mob(ctx, new StriderModel(ctx.bakeLayer(ModelLayers.STRIDER)), 0.5F, 1.0F, Realmwalker.id("textures/entity/saltglass_expanse/dune_strider.png")));
        event.registerEntityRenderer(ModEntities.BASIN_EEL.get(), ctx -> mob(ctx, new StriderModel(ctx.bakeLayer(ModelLayers.STRIDER)), 0.5F, 1.0F, Realmwalker.id("textures/entity/saltglass_expanse/basin_eel.png")));
        event.registerEntityRenderer(ModEntities.PRISM_COLOSSUS.get(), ctx -> mob(ctx, new IronGolemModel(ctx.bakeLayer(ModelLayers.IRON_GOLEM)), 1.0F, 2.6F, Realmwalker.id("textures/entity/saltglass_expanse/prism_colossus.png")));
        event.registerEntityRenderer(ModEntities.CLOCKWORK_SOLDIER.get(), ctx -> mob(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, 1.0F, Realmwalker.id("textures/entity/cogspire/clockwork_soldier.png")));
        event.registerEntityRenderer(ModEntities.RIVET_DRONE.get(), ctx -> mob(ctx, new BlazeModel(ctx.bakeLayer(ModelLayers.BLAZE)), 0.4F, 1.0F, Realmwalker.id("textures/entity/cogspire/rivet_drone.png")));
        event.registerEntityRenderer(ModEntities.STEAM_GOLEM.get(), ctx -> mob(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, 1.0F, Realmwalker.id("textures/entity/cogspire/steam_golem.png")));
        event.registerEntityRenderer(ModEntities.SCRAP_HOUND.get(), ctx -> mob(ctx, new WolfModel(ctx.bakeLayer(ModelLayers.WOLF)), 0.4F, 1.0F, Realmwalker.id("textures/entity/cogspire/scrap_hound.png")));
        event.registerEntityRenderer(ModEntities.GEARWRIGHT.get(), ctx -> mob(ctx, new BlazeModel(ctx.bakeLayer(ModelLayers.BLAZE)), 0.4F, 1.0F, Realmwalker.id("textures/entity/cogspire/gearwright.png")));
        event.registerEntityRenderer(ModEntities.GRAND_MECHANIST.get(), ctx -> mob(ctx, new IronGolemModel(ctx.bakeLayer(ModelLayers.IRON_GOLEM)), 1.0F, 3.4F, Realmwalker.id("textures/entity/cogspire/grand_mechanist.png")));
        event.registerEntityRenderer(ModEntities.FROSTBOUND.get(), ctx -> mob(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, 1.0F, Realmwalker.id("textures/entity/frostmarrow/frostbound.png")));
        event.registerEntityRenderer(ModEntities.RIME_WOLF.get(), ctx -> mob(ctx, new WolfModel(ctx.bakeLayer(ModelLayers.WOLF)), 0.4F, 1.0F, Realmwalker.id("textures/entity/frostmarrow/rime_wolf.png")));
        event.registerEntityRenderer(ModEntities.MARROW_WRAITH.get(), ctx -> mob(ctx, new VexModel(ctx.bakeLayer(ModelLayers.VEX)), 0.4F, 1.0F, Realmwalker.id("textures/entity/frostmarrow/marrow_wraith.png")));
        event.registerEntityRenderer(ModEntities.ICICLE_SPITTER.get(), ctx -> mob(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, 1.0F, Realmwalker.id("textures/entity/frostmarrow/icicle_spitter.png")));
        event.registerEntityRenderer(ModEntities.AURORA_SPRITE.get(), ctx -> mob(ctx, new VexModel(ctx.bakeLayer(ModelLayers.VEX)), 0.4F, 1.0F, Realmwalker.id("textures/entity/frostmarrow/aurora_sprite.png")));
        event.registerEntityRenderer(ModEntities.RIBCAGE_WYRM.get(), ctx -> mob(ctx, new EnderDragonModel(ctx.bakeLayer(ModelLayers.ENDER_DRAGON)), 1.0F, 2.2F, Realmwalker.id("textures/entity/frostmarrow/ribcage_wyrm.png")));
        event.registerEntityRenderer(ModEntities.STORM_HAWK.get(), ctx -> mob(ctx, new PhantomModel(ctx.bakeLayer(ModelLayers.PHANTOM)), 0.6F, 1.0F, Realmwalker.id("textures/entity/stormspire/storm_hawk.png")));
        event.registerEntityRenderer(ModEntities.THUNDER_IMP.get(), ctx -> mob(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, 1.0F, Realmwalker.id("textures/entity/stormspire/thunder_imp.png")));
        event.registerEntityRenderer(ModEntities.GALE_ELEMENTAL.get(), ctx -> mob(ctx, new VexModel(ctx.bakeLayer(ModelLayers.VEX)), 0.4F, 1.0F, Realmwalker.id("textures/entity/stormspire/gale_elemental.png")));
        event.registerEntityRenderer(ModEntities.CLOUD_JELLY.get(), ctx -> mob(ctx, new VexModel(ctx.bakeLayer(ModelLayers.VEX)), 0.4F, 1.0F, Realmwalker.id("textures/entity/stormspire/cloud_jelly.png")));
        event.registerEntityRenderer(ModEntities.ROD_KNIGHT.get(), ctx -> mob(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F, 1.0F, Realmwalker.id("textures/entity/stormspire/rod_knight.png")));
        event.registerEntityRenderer(ModEntities.TEMPEST_SOVEREIGN.get(), ctx -> mob(ctx, new GhastModel(ctx.bakeLayer(ModelLayers.GHAST)), 1.0F, 3.8F, Realmwalker.id("textures/entity/stormspire/tempest_sovereign.png")));

        event.registerEntityRenderer(ModEntities.MOB_BOLT.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(ModEntities.HOOK.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(ModEntities.BONE_SPEAR.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.CODEX.get(), CodexScreen::new);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Mob> EntityRenderer<T> mob(EntityRendererProvider.Context ctx, EntityModel<?> model, float shadow, float scale, ResourceLocation texture) {
        return (EntityRenderer<T>) new ScaledMobRenderer<Mob>((EntityModel<Mob>) model, ctx, shadow, scale, texture);
    }

    private static class ScaledMobRenderer<T extends Mob> extends MobRenderer<T, EntityModel<T>> {
        private final float scale;
        private final ResourceLocation texture;

        ScaledMobRenderer(EntityModel<T> model, EntityRendererProvider.Context ctx, float shadow, float scale, ResourceLocation texture) {
            super(ctx, model, shadow);
            this.scale = scale;
            this.texture = texture;
        }

        @Override
        protected void scale(T entity, PoseStack poseStack, float partialTick) {
            if (this.scale != 1.0F) {
                poseStack.scale(this.scale, this.scale, this.scale);
            }
        }

        @Override
        public ResourceLocation getTextureLocation(T entity) {
            return this.texture;
        }
    }
}
