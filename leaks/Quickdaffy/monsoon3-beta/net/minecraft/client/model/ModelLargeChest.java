/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;

public class ModelLargeChest
extends ModelChest {
    public ModelLargeChest() {
        this.chestLid = new ModelRenderer(this, 0, 0).setTextureSize(128, 64);
        this.chestLid.addBox(0.0f, -5.0f, -14.0f, 30, 5, 14, 0.0f);
        this.chestLid.rotationPointX = 1.0f;
        this.chestLid.rotationPointY = 7.0f;
        this.chestLid.rotationPointZ = 15.0f;
        this.chestKnob = new ModelRenderer(this, 0, 0).setTextureSize(128, 64);
        this.chestKnob.addBox(-1.0f, -2.0f, -15.0f, 2, 4, 1, 0.0f);
        this.chestKnob.rotationPointX = 16.0f;
        this.chestKnob.rotationPointY = 7.0f;
        this.chestKnob.rotationPointZ = 15.0f;
        this.chestBelow = new ModelRenderer(this, 0, 19).setTextureSize(128, 64);
        this.chestBelow.addBox(0.0f, 0.0f, 0.0f, 30, 10, 14, 0.0f);
        this.chestBelow.rotationPointX = 1.0f;
        this.chestBelow.rotationPointY = 6.0f;
        this.chestBelow.rotationPointZ = 1.0f;
    }
}

