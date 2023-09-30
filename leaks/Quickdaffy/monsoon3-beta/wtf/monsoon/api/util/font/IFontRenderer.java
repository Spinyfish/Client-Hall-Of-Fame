/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.font;

import java.awt.Color;

public interface IFontRenderer {
    public int drawStringWithShadow(String var1, float var2, float var3, int var4);

    public int drawString(String var1, float var2, float var3, int var4);

    public int drawString(String var1, float var2, float var3, int var4, boolean var5);

    public int drawStringWithShadow(String var1, float var2, float var3, Color var4);

    public int drawString(String var1, float var2, float var3, Color var4);

    public int drawString(String var1, float var2, float var3, Color var4, boolean var5);

    public float getStringWidthF(String var1);

    public int getStringWidth(String var1);

    public int getHeight();

    public float getHeightF();
}

