/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.opengui;

import java.awt.Color;
import wtf.monsoon.impl.ui.ScalableScreen;
import wtf.opengui.CStyle;
import wtf.opengui.Screen;

public class TestOpenGuiScreen
extends ScalableScreen
implements Screen {
    @Override
    public void build() {
        this.ele("box", "#red");
        this.ele("box", "#green");
        this.ele("box", "#blue");
        this.ele("box", "#yellow");
        this.style("box", () -> {
            CStyle s = new CStyle();
            s.fill_color = Color.RED;
            s.radius = 1.0f;
            s.height = 80.0f;
            s.width = 160.0f;
            return s;
        });
        this.style("#red", () -> {
            CStyle s = new CStyle();
            s.fill_color = Color.RED;
            return s;
        });
        this.style("#green", () -> {
            CStyle s = new CStyle();
            s.fill_color = Color.GREEN;
            return s;
        });
        this.style("#blue", () -> {
            CStyle s = new CStyle();
            s.fill_color = Color.BLUE;
            return s;
        });
        this.style("#yellow", () -> {
            CStyle s = new CStyle();
            s.fill_color = Color.YELLOW;
            return s;
        });
        Screen.super.build();
    }

    @Override
    public void init() {
    }

    @Override
    public void render(float mouseX, float mouseY) {
    }

    @Override
    public void click(float mouseX, float mouseY, int mouseButton) {
    }
}

