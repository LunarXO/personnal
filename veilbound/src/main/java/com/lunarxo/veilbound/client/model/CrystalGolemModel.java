package com.lunarxo.veilbound.client.model;

import com.lunarxo.veilbound.entity.CrystalGolemEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrystalGolemModel extends HierarchicalModel<CrystalGolemEntity> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public CrystalGolemModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        return IronGolemModel.createBodyLayer();
    }

    @Override
    public void setupAnim(CrystalGolemEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
        this.head.xRot = headPitch * (float) (Math.PI / 180.0);
        this.rightLeg.xRot = -1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.leftLeg.xRot = 1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.yRot = 0.0F;
        if (entity.swinging) {
            float swing = (float) entity.swingTime / 5.0F;
            this.rightArm.xRot = -2.0F + 1.5F * Mth.triangleWave(swing, 10.0F);
            this.leftArm.xRot = -2.0F + 1.5F * Mth.triangleWave(swing, 10.0F);
        } else {
            this.rightArm.xRot = (-0.2F + 1.5F * Mth.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
            this.leftArm.xRot = (-0.2F - 1.5F * Mth.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
