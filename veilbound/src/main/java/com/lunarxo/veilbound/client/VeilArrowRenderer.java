package com.lunarxo.veilbound.client;

import com.lunarxo.veilbound.Veilbound;
import com.lunarxo.veilbound.entity.projectile.VeilArrowEntity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class VeilArrowRenderer extends ArrowRenderer<VeilArrowEntity> {
    private static final ResourceLocation TEXTURE = Veilbound.id("textures/entity/veil_arrow.png");

    public VeilArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(VeilArrowEntity entity) {
        return TEXTURE;
    }
}
