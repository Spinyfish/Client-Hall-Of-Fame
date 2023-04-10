package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;

public class LayerBipedArmor extends LayerArmorBase
{

    public LayerBipedArmor(RendererLivingEntity rendererIn)
    {
        super(rendererIn);
    }

    protected void initArmor()
    {
        this.field_177189_c = new ModelBiped(0.5F);
        this.field_177186_d = new ModelBiped(1.0F);
    }

    protected void setupArmorVisibility(ModelBiped armorModel, int armorSlot)
    {
        this.func_177194_a(armorModel);

        switch (armorSlot)
        {
            case 1:
                armorModel.bipedRightLeg.showModel = true;
                armorModel.bipedLeftLeg.showModel = true;
                break;

            case 2:
                armorModel.bipedBody.showModel = true;
                armorModel.bipedRightLeg.showModel = true;
                armorModel.bipedLeftLeg.showModel = true;
                break;

            case 3:
                armorModel.bipedBody.showModel = true;
                armorModel.bipedRightArm.showModel = true;
                armorModel.bipedLeftArm.showModel = true;
                break;

            case 4:
                armorModel.bipedHead.showModel = true;
                armorModel.bipedHeadwear.showModel = true;
        }
    }

    protected void func_177194_a(ModelBiped p_177194_1_)
    {
        p_177194_1_.setInvisible(false);
    }

    protected void func_177179_a(ModelBase p_177179_1_, int p_177179_2_)
    {
        this.setupArmorVisibility((ModelBiped)p_177179_1_, p_177179_2_);
    }
}
