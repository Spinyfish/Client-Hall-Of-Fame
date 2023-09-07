package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

public class ModelArmorStandArmor extends ModelBiped
{
    public ModelArmorStandArmor()
    {
        this(0.0F);
    }

    public ModelArmorStandArmor(float modelSize)
    {
        this(modelSize, 64, 32);
    }

    protected ModelArmorStandArmor(float modelSize, int textureWidthIn, int textureHeightIn)
    {
        super(modelSize, 0.0F, textureWidthIn, textureHeightIn);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        if (entityIn instanceof EntityArmorStand)
        {
            EntityArmorStand entityarmorstand = (EntityArmorStand)entityIn;
            this.bipedHead.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getHeadRotation().getX();
            this.bipedHead.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getHeadRotation().getY();
            this.bipedHead.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getHeadRotation().getZ();
            this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
            this.bipedBody.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getBodyRotation().getX();
            this.bipedBody.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getBodyRotation().getY();
            this.bipedBody.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getBodyRotation().getZ();
            this.bipedLeftArm.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getLeftArmRotation().getX();
            this.bipedLeftArm.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getLeftArmRotation().getY();
            this.bipedLeftArm.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getLeftArmRotation().getZ();
            this.bipedRightArm.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getRightArmRotation().getX();
            this.bipedRightArm.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getRightArmRotation().getY();
            this.bipedRightArm.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getRightArmRotation().getZ();
            this.bipedLeftLeg.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getLeftLegRotation().getX();
            this.bipedLeftLeg.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getLeftLegRotation().getY();
            this.bipedLeftLeg.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getLeftLegRotation().getZ();
            this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
            this.bipedRightLeg.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getRightLegRotation().getX();
            this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getRightLegRotation().getY();
            this.bipedRightLeg.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getRightLegRotation().getZ();
            this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
            copyModelAngles(this.bipedHead, this.bipedHeadwear);
        }
    }
}
