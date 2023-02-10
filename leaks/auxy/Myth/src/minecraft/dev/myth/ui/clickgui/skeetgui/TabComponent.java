/**
 * @project Myth
 * @author CodeMan
 * @at 25.09.22, 00:45
 */
package dev.myth.ui.clickgui.skeetgui;

import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.ui.clickgui.ChildComponent;
import dev.myth.ui.clickgui.Component;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class TabComponent extends ChildComponent {

    private double scrolled, totalHeight, scrollSpeed;

    public TabComponent(Component parent, double x, double y, double width, double height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        setWidth(getParent().getWidth() - 32 - 6);
        setHeight(getParent().getHeight() - 3 - 4.5);

        double height = getHeight(), x = getFullX(), y = getFullY();

        if (Mouse.hasWheel() && MouseUtil.isHovered(mouseX, mouseY, x, y, getWidth(), height)) {
            float wheel = Mouse.getDWheel();
            if (wheel < 0) {
                if (scrollSpeed < 0) scrollSpeed = 0;
                scrollSpeed += 5 * RenderUtil.getDeltaTime();
            } else if (wheel > 0) {
                if (scrollSpeed > 0) scrollSpeed = 0;
                scrollSpeed -= 5 * RenderUtil.getDeltaTime();
            }
            scrollSpeed -= scrollSpeed / 10 * RenderUtil.getDeltaTime();
            scrolled += scrollSpeed;
        }
        if (scrolled < 0) {
            scrolled = 0;
        }
        if (totalHeight < height) {
            scrollSpeed = 0;
            scrolled = 0;
        } else {
            if (scrolled >= totalHeight - height) {
                scrolled = totalHeight - height;
                scrollSpeed = 0;
            }
        }

        if (totalHeight > height) {
            float scrollerHeight = (float) ((height / totalHeight) * height);
            float scrollerY = (float) ((scrolled / (totalHeight - height)) * (height - scrollerHeight));

            Gui.drawRect(x + getWidth() - 3, y, x + getWidth(), y + height, SkeetGui.INSTANCE.getColor(0x0C0C0C));
            Gui.drawRect(x + getWidth() - 2.5, y + scrollerY + 0.5, x + getWidth() - 0.5, y + scrollerY + scrollerHeight - 0.5, SkeetGui.INSTANCE.getColor(0x202020));
        }

        double targetedWidth = 110;
        int comps = 0;
        double width = getWidth() - 5;
        for (int i = 1; i < getChildren().size() + 1; i++) {
            if (width / i >= 110) {
                targetedWidth = width / i;
                comps = i;
            }
        }
        double[] yOffsets = new double[comps];
        Arrays.fill(yOffsets, -scrolled + 5);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(getFullX(), getFullY() + 1, getWidth(), getHeight() - 1);
        for (Component component : getChildren()) {
            component.setWidth(targetedWidth - 5);
            int shortest = 0;
            for (int i = 0; i < yOffsets.length; i++) {
                if (yOffsets[i] < yOffsets[shortest]) {
                    shortest = i;
                }
            }
            component.setX(5 + (targetedWidth - 0.5) * shortest);
            component.setY(yOffsets[shortest]);
            yOffsets[shortest] += component.getHeight() + 5;
            component.drawComponent(mouseX, mouseY);
        }
        totalHeight = 0;
        for (double off : yOffsets) {
            if(off > totalHeight) totalHeight = off;
        }
        totalHeight += scrolled;
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for(Component component : getChildren()) {
            if(component.mouseClicked(mouseX, mouseY, mouseButton)) return true;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        for(Component component : getChildren()) {
            if(!component.isVisible()) continue;
            if(component.keyTyped(typedChar, keyCode)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY) {
        for (Component component : getChildren()) {
            if (!component.isVisible()) continue;
            if(component.mouseReleased(mouseX, mouseY)) return true;
        }
        return false;
    }

    @Override
    public void guiClosed() {
        for (Component component : getChildren()) {
            if (!component.isVisible()) continue;
            component.guiClosed();
        }
        super.guiClosed();
    }
}
