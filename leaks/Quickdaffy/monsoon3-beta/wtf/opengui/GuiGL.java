/*
 * Decompiled with CFR 0.152.
 */
package wtf.opengui;

import wtf.opengui.IRenderer;
import wtf.opengui.Screen;

public class GuiGL {
    int dWidth = 0;
    int dHeight = 0;
    int mx;
    int my;
    IRenderer renderer;
    Screen s = null;

    public void resize(int dWidth, int dHeight) {
        this.dWidth = dWidth;
        this.dHeight = dHeight;
    }

    public void render(Object c) {
        this.s = (Screen)c;
        Screen.elements.forEach(e -> {
            e.setRenderer(this.renderer);
            e.render(this.mx, this.my);
        });
    }

    public void onOpen() {
        if (this.s != null) {
            Screen.elements.clear();
            this.s.build();
            System.out.println(Screen.elements.size());
        }
    }

    public void onClose() {
    }

    public void mouseMove(int mouseX, int mouseY) {
        this.mx = mouseX;
        this.my = mouseY;
    }

    public void mouseClick(int mouseX, int mouseY) {
    }

    public void mouseRelease(int mouseX, int mouseY) {
    }

    public void mouseScroll(float amount) {
    }

    public void keyPress(int keycode) {
    }

    public void setRenderer(IRenderer renderer) {
        this.renderer = renderer;
    }
}

