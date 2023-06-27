package net.minecraft.client.model;

import rip.athena.client.modules.impl.fpssettings.*;
import rip.athena.client.*;
import rip.athena.client.modules.*;

public class ModelChest extends ModelBase
{
    public ModelRenderer chestLid;
    public ModelRenderer chestBelow;
    public ModelRenderer chestKnob;
    
    public ModelChest() {
        (this.chestLid = new ModelRenderer(this, 0, 0).setTextureSize(64, 64)).addBox(0.0f, -5.0f, -14.0f, 14, 5, 14, 0.0f);
        this.chestLid.rotationPointX = 1.0f;
        this.chestLid.rotationPointY = 7.0f;
        this.chestLid.rotationPointZ = 15.0f;
        (this.chestKnob = new ModelRenderer(this, 0, 0).setTextureSize(64, 64)).addBox(-1.0f, -2.0f, -15.0f, 2, 4, 1, 0.0f);
        this.chestKnob.rotationPointX = 8.0f;
        this.chestKnob.rotationPointY = 7.0f;
        this.chestKnob.rotationPointZ = 15.0f;
        (this.chestBelow = new ModelRenderer(this, 0, 19).setTextureSize(64, 64)).addBox(0.0f, 0.0f, 0.0f, 14, 10, 14, 0.0f);
        this.chestBelow.rotationPointX = 1.0f;
        this.chestBelow.rotationPointY = 6.0f;
        this.chestBelow.rotationPointZ = 1.0f;
    }
    
    public void renderAll() {
        final OptimizerMod mod = (OptimizerMod)Athena.INSTANCE.getModuleManager().get(OptimizerMod.class);
        if (mod.noBetterChests) {
            this.chestBelow.render(0.0625f);
        }
        else {
            this.chestKnob.rotateAngleX = this.chestLid.rotateAngleX;
            this.chestLid.render(0.0625f);
            this.chestKnob.render(0.0625f);
            this.chestBelow.render(0.0625f);
        }
    }
}
