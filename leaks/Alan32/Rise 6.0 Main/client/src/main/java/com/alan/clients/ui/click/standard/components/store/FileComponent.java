package com.alan.clients.ui.click.standard.components.store;


import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import lombok.Getter;
import store.File;
import util.time.StopWatch;

import java.awt.*;

@Getter
public class FileComponent implements InstanceAccess {

    public File file;
    public Vector2f scale = getStandardClickGUI().getModuleDefaultScale();
    public Vector2d position;
    public StopWatch stopwatch = new StopWatch();
    public Animation hoverAnimation = new Animation(Easing.LINEAR, 50);

    public FileComponent(final File file) {
        this.file = file;
    }

    public void draw(final Vector2d position, final int mouseX, final int mouseY, final float partialTicks) {
        this.position = position;

        if (position == null || position.y + scale.y < getStandardClickGUI().position.y || position.y > getStandardClickGUI().position.y + getStandardClickGUI().scale.y)
            return;


        final RiseClickGUI clickGUI = this.getStandardClickGUI();

        RenderUtil.dropShadow(3, (float) position.x, (float) position.y, scale.x, scale.y, 42, 1);
        RenderUtil.roundedRectangle(position.x, position.y, scale.x, scale.y, 6, clickGUI.sidebarColor);
        final Color fontColor = new Color(clickGUI.fontColor.getRed(), clickGUI.fontColor.getGreen(), clickGUI.fontColor.getBlue(), 200);

        // Hover animation
        final boolean overModule = GUIUtil.mouseOver(position.x, position.y, scale.x, this.scale.y, mouseX, mouseY);

        hoverAnimation.run(overModule ? 30 : 0);

        RenderUtil.roundedRectangle(position.x, position.y, scale.x, scale.y, 6,
                new Color(0, 0, 0, (int) hoverAnimation.getValue()));

        FontManager.getNunito(20).drawString(this.getFile().getName(), (float) position.x + 6f, (float) position.y + 8, fontColor.hashCode());
        FontManager.getNunito(15).drawString(this.file.getDescription(), (float) position.x + 6f, (float) position.y + 25, ColorUtil.withAlpha(fontColor, 100).hashCode());

        /* Allows for settings to be drawn */
        scale = new Vector2f(232, 68);

        stopwatch.reset();
    }

    public void key(final char typedChar, final int keyCode) {

    }

    public void click(final int mouseX, final int mouseY, final int mouseButton) {

    }

    public void bloom() {

    }

    public void released() {
    }
}