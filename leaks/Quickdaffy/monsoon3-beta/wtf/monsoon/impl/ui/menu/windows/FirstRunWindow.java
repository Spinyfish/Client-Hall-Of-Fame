/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.ui.menu.windows;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.ui.menu.windows.Window;
import wtf.monsoon.impl.ui.primitive.Click;

public class FirstRunWindow
extends Window {
    private final ArrayList<String> content = new ArrayList<String>(Arrays.asList("Hey there!", "Thanks for choosing to buy Monsoon.", "", "As it looks like you are new to using Monsoon,", "here's how to get started!", "", "The GUI can be opened by pressing [RSHIFT]", "whilst ingame", "", "The modules can be expanded to show their", "settings by right clicking on them", "", "Some of these settings have subsettings!", "Right click to see if they have any!", "", "We hope you enjoy using Monsoon!"));

    public FirstRunWindow(float x, float y, float width, float height, float header) {
        super(x, y, width, height, header);
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);
        Wrapper.getFont().drawString("First time using Monsoon?", this.getX() + 4.0f, this.getY() + 1.0f, Color.WHITE, false);
        float y = this.getY() + this.getHeader() + 2.0f;
        for (String line : this.content) {
            Wrapper.getFont().drawString(line, this.getX() + 4.0f, y, Color.WHITE, false);
            y += (float)Wrapper.getFont().getHeight();
        }
        this.setHeight(y - this.getY() + 4.0f);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, Click click) {
        super.mouseClicked(mouseX, mouseY, click);
    }
}

