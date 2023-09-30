/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.opengui;

import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.opengui.Element;
import wtf.opengui.IRenderer;

public class IRendererImpl
implements IRenderer {
    @Override
    public void fill(Element ele) {
        RoundedUtils.round(ele.getX(), ele.getY(), ele.getStyle().width, ele.getStyle().height, ele.getStyle().radius, ele.getStyle().fill_color);
    }

    @Override
    public void text(String text, Element ele) {
    }
}

