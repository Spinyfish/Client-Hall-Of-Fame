/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resources.data;

import net.minecraft.client.resources.data.IMetadataSection;

public class FontMetadataSection
implements IMetadataSection {
    private final float[] charWidths;
    private final float[] charLefts;
    private final float[] charSpacings;

    public FontMetadataSection(float[] p_i1310_1_, float[] p_i1310_2_, float[] p_i1310_3_) {
        this.charWidths = p_i1310_1_;
        this.charLefts = p_i1310_2_;
        this.charSpacings = p_i1310_3_;
    }
}

