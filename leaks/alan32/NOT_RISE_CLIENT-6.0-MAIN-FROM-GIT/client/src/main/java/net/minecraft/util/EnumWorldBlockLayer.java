package net.minecraft.util;

public enum EnumWorldBlockLayer {
    SOLID("Solid"),
    CUTOUT_MIPPED("Mipped Cutout"),
    CUTOUT("Cutout"),
    TRANSLUCENT("Translucent");

    private final String layerName;

    EnumWorldBlockLayer(final String layerNameIn) {
        this.layerName = layerNameIn;
    }

    public String toString() {
        return this.layerName;
    }
}
