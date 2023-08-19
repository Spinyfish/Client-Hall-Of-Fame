/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 16:12
 */

package cc.swift.gui.clickgui.components;

import cc.swift.gui.clickgui.GuiComponent;
import cc.swift.util.ColorUtil;
import cc.swift.util.MouseUtil;
import cc.swift.util.render.RenderUtil;
import cc.swift.util.render.StencilUtil;
import cc.swift.util.render.animation.Animation;
import cc.swift.util.render.animation.Easings;
import cc.swift.util.render.font.CFontRenderer;
import cc.swift.util.render.font.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;

public class PanelComponent extends GuiComponent {

    private final String name;
    private final char icon;

    private boolean expanded, dragging;
    private double clickedX, clickedY;

    private final Animation animation = new Animation();

    public PanelComponent(String name, char icon, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.name = name;
        this.icon = icon;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - clickedX;
            y = mouseY - clickedY;
        }

        animation.updateAnimation();

        StencilUtil.init(false);
        RenderUtil.drawRoundedRect(x, y, width, getFullHeight(), 3, -1, 0, 0);
        StencilUtil.read(false);

        Gui.drawRect(x, y, x + width, y + height, ColorUtil.DARK_GRAY.getRGB());

        CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 22);
        int vietnamY = (int) Math.ceil(this.y + (height - vietnamFont.getHeight()) / 2) + 1;
        vietnamFont.drawStringWithShadow(EnumChatFormatting.BOLD + name, x + 6, vietnamY, -1);

        CFontRenderer iconFont = FontRenderer.getFont("icons.ttf", 30);
        int iconY = (int) Math.ceil(this.y + (height - iconFont.getHeight()) / 2);
        iconFont.drawStringWithShadow(String.valueOf(icon), x + width - iconFont.getStringWidth(String.valueOf(icon)) - 6, iconY, -1);

        if (expanded || animation.getValue() > 0)
            super.draw(mouseX, mouseY);

        StencilUtil.end();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isHovered(x, y, width, height, mouseX, mouseY)) {
            if (mouseButton == 1) {
                expanded = !expanded;
                animation.animate(expanded ? 1 : 0, 200, Easings.NONE);
            } else if (mouseButton == 0) {
                dragging = true;
                clickedX = mouseX - x;
                clickedY = mouseY - y;
            }
        }

        if (expanded)
            super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;

        if (expanded)
            super.mouseReleased(mouseX, mouseY);
    }

    @Override
    public double getFullHeight() {
        double height = this.height;
        if (expanded || animation.getValue() > 0) {
            for (GuiComponent component : getChildren()) {
                if (!component.isVisible()) continue;
                height += component.getFullHeight();
            }
        }
        return (height - this.height) * animation.getValue() + this.height;
    }
}
