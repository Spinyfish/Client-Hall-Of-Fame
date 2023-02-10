/**
 * @project Myth
 * @author CodeMan
 * @at 24.09.22, 22:08
 */
package dev.myth.ui.clickgui.dropdowngui;

import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.StencilUtil;
import dev.myth.ui.clickgui.Component;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.awt.Color;

public class Panel extends Component {

    @Getter
    private final String name;

    private boolean dragging;
    private double dragX, dragY, scroll, scrollSpeed;

    public Panel(String name, double x, double y, double width, double height) {
        this.name = name;
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);

        dragging = false;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {

        if(dragging) {
            setX(mouseX - dragX);
            setY(mouseY - dragY);
        }

        RenderUtil.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), new Color(20, 20, 20));
        int yOffset = String.valueOf(getName().charAt(0)).equals("V") ? - 2 : (int) (String.valueOf(getName().charAt(0)).equals("D") ? -1.5 : String.valueOf(getName().charAt(0)).equals("E") ? -1 : 0); // hardcode by Auxy because don't want to fix font
        FontLoaders.ICON.drawString(getName().charAt(0) + "", (float) (this.getX() + 3), (float) (this.getY() + getHeight() / 2 - FontLoaders.ICON.getHeight() / 2 - yOffset), -1);
        FontLoaders.BOLD.drawStringWithShadow(getName(), (float) (this.getX() + 20), (float) (this.getY() + 6), -1);

        if(isExtended()) {
            final double fullHeight = getFullHeight() - getHeight();
            final double height = Math.min(fullHeight, 400);
            final float deltaTime = 120F / Minecraft.getDebugFPS();

            if (Mouse.hasWheel() && MouseUtil.isHovered(mouseX, mouseY, getX(), getY() + getHeight(), getWidth(), height)) {
                float wheel = Mouse.getDWheel();
                if (wheel < 0) {
                    if (scrollSpeed < 0) scrollSpeed = 0;

                    scrollSpeed += 5 * deltaTime;
                } else if (wheel > 0) {
                    if (scrollSpeed > 0) scrollSpeed = 0;
                    scrollSpeed -= 5 * deltaTime;
                }
                scrollSpeed -= scrollSpeed / 10 * deltaTime;
                scroll += scrollSpeed;
                if (fullHeight < height) {
                    scrollSpeed = 0;
                    scroll = 0;
                }
            }
            if (scroll < 0) {
                scroll = 0;
            }
            if (fullHeight >= height) {
                if (scroll > fullHeight - height) {
                    scroll = fullHeight - height;
                }
            }

            if(getChildren().isEmpty()) return;
            StencilUtil.initStencilToWrite();
            RenderUtil.drawRect(this.getX(), this.getY() + this.getHeight(), this.getWidth(), height, new Color(20, 20, 20));
            StencilUtil.readStencilBuffer(1);
            Component previous = null;
            for(Component child : this.getChildren()) {
                if(!child.isVisible()) continue;
                if(previous != null) {
                    child.setY(previous.getY() + previous.getFullHeight());
                } else {
                    child.setY(this.getY() + this.getHeight() - scroll);
                }
                child.setX(getX());
                child.setWidth(getWidth());
                child.setHeight(getHeight());
                child.drawComponent(mouseX, mouseY);

                previous = child;
            }
            StencilUtil.uninitStencilBuffer();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(MouseUtil.isHovered(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            if(mouseButton == 1) setExtended(!isExtended());
            if(mouseButton == 0) {
                dragging = true;
                dragX = mouseX - getX();
                dragY = mouseY - getY();
            }
        }
        if(!isExtended()) return false;
        getChildren().forEach(child -> child.mouseClicked(mouseX, mouseY, mouseButton));
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY) {
        dragging = false;
        getChildren().forEach(child -> child.mouseReleased(mouseX, mouseY));
        return false;
    }

    @Override
    public void guiClosed() {
        dragging = false;

        getChildren().forEach(dev.myth.ui.clickgui.Component::guiClosed);
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        getChildren().forEach(child -> child.keyTyped(typedChar, keyCode));
        return false;
    }

    @Override
    public boolean isExtendable() {
        return true;
    }

    @Override
    public double getFullHeight() {
        double height = this.getHeight();
        if(isExtended()) {
            height += getChildren().stream().filter(dev.myth.ui.clickgui.Component::isVisible).mapToDouble(Component::getFullHeight).sum();
        }
        return height;
    }
}
