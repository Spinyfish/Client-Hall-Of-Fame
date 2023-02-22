package com.alan.clients.ui.click.clover.button.impl;

import com.alan.clients.module.Module;
import com.alan.clients.ui.click.clover.button.api.Button;
import com.alan.clients.ui.click.clover.setting.api.SettingComp;
import com.alan.clients.ui.click.clover.setting.impl.BooleanSettingComponent;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.impl.BooleanValue;

import java.awt.*;
import java.util.ArrayList;

public class ModuleButton extends Button implements InstanceAccess {
    public Module module;
    public ArrayList<SettingComp> settings = new ArrayList<>();

    public ModuleButton(Vector2d position, Vector2d scale, Runnable click, Module module) {
        super(position, scale, click);
        this.module = module;

        module.getAllValues().forEach(value -> {
            if (value instanceof BooleanValue) {
                settings.add(new BooleanSettingComponent(value));
            }
        });
    }

    @Override
    public void render() {
        super.render();

        RenderUtil.roundedRectangle(position.x, position.y, scale.x, scale.y, 8, ColorUtil.withAlpha(Color.WHITE, 10));
        RenderUtil.roundedOutlineRectangle(position.x, position.y, scale.x, scale.y, 8, 0.5, ColorUtil.withAlpha(Color.WHITE, 10));

        double fontWidth = FontManager.getNunito(24).width(module.getModuleInfo().name());
        FontManager.getNunito(24).drawString(module.getModuleInfo().name(), position.x + scale.x - fontWidth - getCloverClickGUI().padding, position.y + getCloverClickGUI().padding, Color.WHITE.hashCode());

        double radius = 1.5;
        double distance = 2 + radius;
        Vector2d moreOptionsPosition = position.offset(getCloverClickGUI().padding, scale.y - getCloverClickGUI().padding - 6);
        for (double y = moreOptionsPosition.y; y < moreOptionsPosition.y + distance * 3; y += distance) {
            RenderUtil.roundedRectangle(moreOptionsPosition.x, y, radius, radius, radius / 2, Color.WHITE);
        }

        String description = module.getModuleInfo().description();
        double width = FontManager.getNunito(15).width(description);
        FontManager.getNunito(15).drawString(description, position.x + scale.x - width - getCloverClickGUI().padding, position.y + 33, getCloverClickGUI().deSelected.hashCode());

        Vector2d buttonScale = new Vector2d(70, 18);
        Vector2d enabledButton = position.offset(scale.x - getCloverClickGUI().padding - buttonScale.x, scale.y - getCloverClickGUI().padding - buttonScale.y + 5);
        if (module.isEnabled()) {
            RenderUtil.drawRoundedGradientRect(enabledButton.x, enabledButton.y, buttonScale.x, buttonScale.y, buttonScale.y / 2f, new Color(255, 0, 242), new Color(27, 83, 224), false);
            NORMAL_POST_BLOOM_RUNNABLES.add(() -> RenderUtil.drawRoundedGradientRect(enabledButton.x, enabledButton.y, buttonScale.x, buttonScale.y, buttonScale.y / 2f, new Color(255, 0, 242), new Color(27, 83, 224), false));
        } else {
            RenderUtil.roundedOutlineRectangle(enabledButton.x, enabledButton.y, buttonScale.x, buttonScale.y, buttonScale.y / 2f, 0.5f, getCloverClickGUI().accentLines);
        }

        FontManager.getNunito(14).drawCenteredString(module.isEnabled() ? "Disable" : "Enable", enabledButton.x + buttonScale.x / 2, enabledButton.y + buttonScale.y / 2 - FontManager.getNunito(14).height() / 2 + 2.5, Color.WHITE.hashCode());
    }
}
