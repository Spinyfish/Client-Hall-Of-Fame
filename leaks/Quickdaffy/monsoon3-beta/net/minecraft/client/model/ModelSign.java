/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelSign
extends ModelBase {
    public ModelRenderer signBoard = new ModelRenderer(this, 0, 0);
    public ModelRenderer signStick;

    public ModelSign() {
        this.signBoard.addBox(-12.0f, -14.0f, -1.0f, 24, 12, 2, 0.0f);
        this.signStick = new ModelRenderer(this, 0, 14);
        this.signStick.addBox(-1.0f, -2.0f, -1.0f, 2, 14, 2, 0.0f);
    }

    public void renderSign() {
        this.signBoard.render(0.0625f);
        this.signStick.render(0.0625f);
    }
}

