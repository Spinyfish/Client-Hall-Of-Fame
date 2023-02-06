package dev.elysium.client.ui.gui.components;

import dev.elysium.base.mods.Mod;
import dev.elysium.client.ui.font.TTFFontRenderer;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;

public class Button {
    public double x, x2, y, y2;
    public int color;
    public String idname;
    public int timer = 0;
    public int custom;
    public TTFFontRenderer font;

    public Mod module;

    public Button(String id, double x, double y, double x2, double y2, int color) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.idname = id;
    }

    public Button(String id, double x, double y, double x2, double y2) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.color = -1;
        this.idname = id;
    }

    public Button(String id, int x, int y, int x2, int y2, int color, int custom) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.idname = id;
        this.custom = custom;
    }

    public void draw(int rounding) {
        RenderUtils.drawRoundedRect(x, y, x2, y2, rounding, this.color);
    }

    public void draw() {
        Gui.drawRect(x, y, x2, y2, this.color);
    }

    public void draw(int rounding, int color) {
        RenderUtils.drawRoundedRect(x, y, x2, y2, rounding, color);
    }

    public boolean isMouseOverMe(int mouseX, int mouseY) {
        double _x, _y, _x2, _y2;
        if(x < x2) {
            _x = x;
            _x2 = x2;
        }
        else {
            _x2 = x;
            _x = x2;
        }

        if(y < y2) {
            _y = y;
            _y2 = y2;
        }
        else {
            _y2 = y;
            _y = y2;
        }

        return (mouseX > _x && mouseX < _x2) && (mouseY > _y && mouseY < _y2);
    }
}
