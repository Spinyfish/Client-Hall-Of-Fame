/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.ui.menu.windows;

import java.awt.Color;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.ui.menu.windows.Window;
import wtf.monsoon.impl.ui.primitive.Click;

public class ErrorWindow
extends Window {
    private final String title;
    private final String[] content;

    public ErrorWindow(String title, String[] content, float x, float y, float width, float height, float header) {
        super(x, y, width, height, header);
        this.title = title;
        this.content = content;
        this.setHeight(this.getHeader() + 2.0f + (float)(content.length * Wrapper.getFont().getHeight()));
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);
        Wrapper.getFont().drawString(this.title, this.getX() + 4.0f, this.getY() + 1.0f, Color.WHITE, false);
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

