package com.alan.clients.ui.click.clover.setting.impl;

import com.alan.clients.ui.click.clover.setting.api.SettingComp;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.value.Value;

import java.awt.*;

public class BooleanSettingComponent extends SettingComp implements InstanceAccess {
    public BooleanSettingComponent(Value value) {
        super(value);
    }

    @Override
    public void render() {
        FontManager.getNunito(20).drawString(value.getName(), position.x, position.y, Color.WHITE.hashCode());

        RenderUtil.roundedRectangle(position.x, position.y, 20, 8, 4, ColorUtil.withAlpha(Color.WHITE, 100));
    }
}
