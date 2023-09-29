package me.jinthium.straight.api.clickgui.dropdown.components;

import me.jinthium.straight.api.clickgui.dropdown.Screen;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.Setting;
import me.jinthium.straight.impl.utils.misc.HoveringUtil;
import me.jinthium.straight.impl.utils.tuples.Pair;

import java.awt.*;

public abstract class SettingComponent<T extends Setting> implements Screen {
    private final T setting;

    public float x, y, width, height, alpha;
    public boolean typing;
    public float panelLimitY;
    public Pair<Color, Color> clientColors;
    public Color settingRectColor, textColor;
    public float countSize = 1;


    public SettingComponent(T setting) {
        this.setting = setting;
    }

    public boolean isHoveringBox(int mouseX, int mouseY) {
        return HoveringUtil.isHovering(x,y,width,height, mouseX, mouseY);
    }

    //Idk why 40 is perfect for it
    public boolean isClickable(float bottomY) {
        return bottomY > panelLimitY && bottomY < panelLimitY + 17 + Module.allowedClickGuiHeight;
    }

    public T getSetting() {
        return setting;
    }
}
