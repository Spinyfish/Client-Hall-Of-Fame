/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 16:39
 */

package cc.swift.gui.clickgui.components;

import cc.swift.gui.clickgui.GuiComponent;
import cc.swift.util.ColorUtil;
import cc.swift.util.MouseUtil;
import cc.swift.util.render.RenderUtil;
import cc.swift.util.render.animation.Animation;
import cc.swift.util.render.animation.Easings;
import cc.swift.util.render.font.CFontRenderer;
import cc.swift.util.render.font.FontRenderer;
import cc.swift.value.impl.BooleanValue;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class BooleanValueComponent extends GuiComponent {
    private final BooleanValue booleanValue;

    private final Animation animation = new Animation();

    public BooleanValueComponent(BooleanValue booleanValue, double width, double height) {
        super(0, 0, width, height);
        this.booleanValue = booleanValue;

        this.animation.setValue(booleanValue.getValue() ? 1 : 0);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        this.animation.updateAnimation();

        Gui.drawRect(x, y, x + width, y + height, ColorUtil.LIGHT_GRAY.getRGB());

        // RenderUtil.drawRoundedRect(x + width - 18, y + height / 2 - 2, 12, 4, 4, this.booleanValue.getValue() ? Color.RED.getRGB() : -1, 0, 0);
        // RenderUtil.drawRoundedRect(x + width - 18 + 1.5 + 6 * this.animation.getValue(), y + height / 2 - 1.5, 3, 3, 4, ColorUtil.GRAY.getRGB(), 0, 0);

        RenderUtil.drawRoundedRect(x + width - 12, y + height / 2 - 3, 6, 6, 4, 0, this.booleanValue.getValue() ? Color.RED.getRGB() : -1, 2);

        double size = 4 * this.animation.getValue();
        double invertedSize = 4 - size;
        RenderUtil.drawRoundedRect(x + width - 12 + 1 + invertedSize / 2, y + height / 2 - 3 + 1 + invertedSize / 2, size, size, 4, Color.RED.getRGB(), 0, 0);

        CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 15);
        int y = (int) Math.ceil(this.y + (height - vietnamFont.getHeight()) / 2);
        vietnamFont.drawStringWithShadow(this.booleanValue.getName(), x + 6, y, -1);

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && MouseUtil.isHovered(x, y, width, height, mouseX, mouseY)) {
            this.booleanValue.setValue(!this.booleanValue.getValue());

            this.animation.animate(this.booleanValue.getValue() ? 1 : 0, 100, Easings.NONE);
        }
    }

    @Override
    public boolean isVisible() {
        return this.booleanValue.isVisible();
    }
}
