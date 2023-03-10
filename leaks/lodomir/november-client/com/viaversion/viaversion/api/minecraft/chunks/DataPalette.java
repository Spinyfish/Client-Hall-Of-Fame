/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft.chunks;

public interface DataPalette {
    public int index(int var1, int var2, int var3);

    public int idAt(int var1);

    default public int idAt(int sectionX, int sectionY, int sectionZ) {
        return this.idAt(this.index(sectionX, sectionY, sectionZ));
    }

    public void setIdAt(int var1, int var2);

    default public void setIdAt(int sectionX, int sectionY, int sectionZ, int id) {
        this.setIdAt(this.index(sectionX, sectionY, sectionZ), id);
    }

    public int idByIndex(int var1);

    public void setIdByIndex(int var1, int var2);

    public int paletteIndexAt(int var1);

    public void setPaletteIndexAt(int var1, int var2);

    public void addId(int var1);

    public void replaceId(int var1, int var2);

    public int size();

    public void clear();
}

