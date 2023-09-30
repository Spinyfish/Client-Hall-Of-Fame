/*
 * Decompiled with CFR 0.152.
 */
package wtf.opengui.prims;

import wtf.opengui.Element;

public class EleBox
extends Element {
    public EleBox() {
        this.setType("box");
    }

    @Override
    public void render(int mx, int my) {
        this.getRenderer().fill(this);
    }

    @Override
    public void mouseClicked(int mx, int my, int btn) {
    }

    @Override
    public void mouseReleased(int mx, int my, int btn) {
    }
}

