package wtf.automn.gui.clickgui.phantom;

import lombok.Getter;
import net.minecraft.client.gui.Gui;
import wtf.automn.Automn;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.gui.clickgui.phantom.buttons.ModuleButton;
import wtf.automn.gui.clickgui.phantom.buttons.valuecomps.Component;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Panel {

    @Getter
    private float x, y, width, height;
    private final Category category;

    private GlyphPageFontRenderer fr = ClientFont.font(20, "Arial", true);

    private boolean extended = true, hovered, dragged;
    private float dragX, dragY;

    private List<ModuleButton> moduleButtons = new ArrayList<>();

    public Panel(float x, float y, float width, float height, Category category) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.category = category;
        float yOffset = y + height;
        List<Module> modules = Automn.instance().moduleManager().getModules().stream().filter(module -> module.category() == category).toList();
        for (Module module : modules) {
            ModuleButton moduleButton = new ModuleButton(x, yOffset, width, height, module);
            moduleButtons.add(moduleButton);
            yOffset += moduleButton.height();
        }
    }

    public void drawPanel(int mouseX, int mouseY, float partialTicks) {

        if (dragged) {
            setPosition(mouseX - dragX, mouseY - dragY);
        }

        hovered = isHeaderHovered(mouseX, mouseY);
        RenderUtils.drawRoundedRect2(x, y, width, height, 3, true, true, false, false, (hovered ? new Color(89, 155, 255) : new Color(35, 60, 153)).getRGB());
        fr.drawString(category.name(), x + width / 2f - fr.getStringWidth(category.name()) / 2f, y + height / 2f - fr.getFontHeight() / 2f, Color.white.getRGB());
        if (extended) {
            moduleButtons.forEach(moduleButton -> moduleButton.drawButton(mouseX, mouseY, partialTicks));
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered) {
            if (mouseButton == 0) {
                dragged = true;
                dragX = mouseX - x;
                dragY = mouseY - y;
            } else if (mouseButton == 1) {
                extended = !extended;
            }
        } else {
            if (extended) {
                for (ModuleButton moduleButton : moduleButtons) {
                    if (!moduleButton.mouseClicked(mouseX, mouseY, mouseButton)) {
                        boolean isHovered = false;
                        for (Component component : moduleButton.componentList) {
                            if (component.hovered) {
                                isHovered = true;
                                break;
                            }
                        }
                        moduleButton.extended = isHovered;
                    }
                }
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragged = false;
        if (extended) {
            moduleButtons.forEach(moduleButton -> moduleButton.mouseReleased(mouseX, mouseY, mouseButton));
        }
    }

    private boolean isHeaderHovered(int mouseX, int mouseY) {
        return RenderUtils.isInside(mouseX, mouseY, x, y, width, height);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        float yOffset = y + height;
        for (ModuleButton moduleButton : moduleButtons) {
            moduleButton.setPosition(x, yOffset);
            yOffset += moduleButton.height();
        }
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}

