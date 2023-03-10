/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.material;

public class MapColor {
    public static final MapColor[] mapColorArray = new MapColor[64];
    public static final MapColor airColor = new MapColor(0, 0);
    public static final MapColor grassColor = new MapColor(1, 8368696);
    public static final MapColor sandColor = new MapColor(2, 16247203);
    public static final MapColor clothColor = new MapColor(3, 0xC7C7C7);
    public static final MapColor tntColor = new MapColor(4, 0xFF0000);
    public static final MapColor iceColor = new MapColor(5, 0xA0A0FF);
    public static final MapColor ironColor = new MapColor(6, 0xA7A7A7);
    public static final MapColor foliageColor = new MapColor(7, 31744);
    public static final MapColor snowColor = new MapColor(8, 0xFFFFFF);
    public static final MapColor clayColor = new MapColor(9, 10791096);
    public static final MapColor dirtColor = new MapColor(10, 9923917);
    public static final MapColor stoneColor = new MapColor(11, 0x707070);
    public static final MapColor waterColor = new MapColor(12, 0x4040FF);
    public static final MapColor woodColor = new MapColor(13, 9402184);
    public static final MapColor quartzColor = new MapColor(14, 0xFFFCF5);
    public static final MapColor adobeColor = new MapColor(15, 14188339);
    public static final MapColor magentaColor = new MapColor(16, 11685080);
    public static final MapColor lightBlueColor = new MapColor(17, 6724056);
    public static final MapColor yellowColor = new MapColor(18, 0xE5E533);
    public static final MapColor limeColor = new MapColor(19, 8375321);
    public static final MapColor pinkColor = new MapColor(20, 15892389);
    public static final MapColor grayColor = new MapColor(21, 0x4C4C4C);
    public static final MapColor silverColor = new MapColor(22, 0x999999);
    public static final MapColor cyanColor = new MapColor(23, 5013401);
    public static final MapColor purpleColor = new MapColor(24, 8339378);
    public static final MapColor blueColor = new MapColor(25, 3361970);
    public static final MapColor brownColor = new MapColor(26, 6704179);
    public static final MapColor greenColor = new MapColor(27, 6717235);
    public static final MapColor redColor = new MapColor(28, 0x993333);
    public static final MapColor blackColor = new MapColor(29, 0x191919);
    public static final MapColor goldColor = new MapColor(30, 16445005);
    public static final MapColor diamondColor = new MapColor(31, 6085589);
    public static final MapColor lapisColor = new MapColor(32, 4882687);
    public static final MapColor emeraldColor = new MapColor(33, 55610);
    public static final MapColor obsidianColor = new MapColor(34, 8476209);
    public static final MapColor netherrackColor = new MapColor(35, 0x700200);
    public int colorValue;
    public final int colorIndex;
    private static final String __OBFID = "CL_00000544";

    private MapColor(int index, int color) {
        if (index < 0 || index > 63) {
            throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
        }
        this.colorIndex = index;
        this.colorValue = color;
        MapColor.mapColorArray[index] = this;
    }

    public int func_151643_b(int p_151643_1_) {
        int short1 = 220;
        if (p_151643_1_ == 3) {
            short1 = 135;
        }
        if (p_151643_1_ == 2) {
            short1 = 255;
        }
        if (p_151643_1_ == 1) {
            short1 = 220;
        }
        if (p_151643_1_ == 0) {
            short1 = 180;
        }
        int i = (this.colorValue >> 16 & 0xFF) * short1 / 255;
        int j = (this.colorValue >> 8 & 0xFF) * short1 / 255;
        int k = (this.colorValue & 0xFF) * short1 / 255;
        return 0xFF000000 | i << 16 | j << 8 | k;
    }
}

