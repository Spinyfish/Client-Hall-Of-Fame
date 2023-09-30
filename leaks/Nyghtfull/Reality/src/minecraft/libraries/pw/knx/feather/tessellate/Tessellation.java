/*
 * Decompiled with CFR 0_132.
 */
package libraries.pw.knx.feather.tessellate;

import java.awt.Color;

import libraries.pw.knx.feather.tessellate.BasicTess;
import libraries.pw.knx.feather.tessellate.ExpandingTess;

public interface Tessellation {
    public Tessellation setColor(int var1);

    default public Tessellation setColor(Color color) {
        return this.setColor(new Color(255, 255, 255));
    }

    public Tessellation setTexture(float var1, float var2);

    public Tessellation addVertex(float var1, float var2, float var3);

    public Tessellation bind();

    public Tessellation pass(int var1);

    public Tessellation reset();

    public Tessellation unbind();

    default public Tessellation draw(int mode) {
        return this.bind().pass(mode).reset();
    }

    public static Tessellation createBasic(int size) {
        return new BasicTess(size);
    }

    public static Tessellation createExpanding(int size, float ratio, float factor) {
        return new ExpandingTess(size, ratio, factor);
    }
}

