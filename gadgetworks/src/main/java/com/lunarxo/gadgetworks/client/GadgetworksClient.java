package com.lunarxo.gadgetworks.client;

import com.lunarxo.gadgetworks.Gadgetworks;
import com.lunarxo.gadgetworks.entity.MiniRedstoneGolem;
import com.lunarxo.gadgetworks.registry.ModEntities;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Gadgetworks.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GadgetworksClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MINI_REDSTONE_GOLEM.get(), MiniRedstoneGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.REDSTONE_SPARK.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
    }

    public static class MiniRedstoneGolemRenderer extends MobRenderer<MiniRedstoneGolem, HumanoidModel<MiniRedstoneGolem>> {
        private static final ResourceLocation TEXTURE = Gadgetworks.id("textures/entity/mini_redstone_golem.png");

        public MiniRedstoneGolemRenderer(EntityRendererProvider.Context context) {
            super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.3F);
        }

        @Override
        public ResourceLocation getTextureLocation(MiniRedstoneGolem entity) {
            return TEXTURE;
        }

        @Override
        protected void scale(MiniRedstoneGolem entity, com.mojang.blaze3d.vertex.PoseStack poseStack, float partialTick) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
    }
}
