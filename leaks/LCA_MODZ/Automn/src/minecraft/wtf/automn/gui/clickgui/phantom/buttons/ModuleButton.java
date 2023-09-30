package wtf.automn.gui.clickgui.phantom.buttons;

import lombok.Getter;
import net.minecraft.client.gui.Gui;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.gui.clickgui.phantom.buttons.valuecomps.Checkbox;
import wtf.automn.gui.clickgui.phantom.buttons.valuecomps.Component;
import wtf.automn.gui.clickgui.phantom.buttons.valuecomps.Slider;
import wtf.automn.module.Module;
import wtf.automn.module.impl.visual.ModuleBlur;
import wtf.automn.module.settings.Setting;
import wtf.automn.module.settings.SettingBoolean;
import wtf.automn.module.settings.SettingNumber;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleButton {

    @Getter
    private float x, y, width, height;
    private final Module module;

    private GlyphPageFontRenderer fr = ClientFont.font(20, "Arial", true);

    public boolean extended, hovered;

    public List<Component> componentList = new ArrayList<>();

    public ModuleButton(float x, float y, float width, float height, Module module) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.module = module;

        float settingX = x + width + 2;
        for (Setting setting : module.settings()) {
            if (setting instanceof SettingNumber) {
                componentList.add(new Slider(settingX, y, width, height, setting));
            } else if (setting instanceof SettingBoolean) {
                componentList.add(new Checkbox(settingX, y, width, height, setting));
            }
        }
        setPosition(x, y);
    }

    public void drawButton(int mouseX, int mouseY, float partialTicks) {
        hovered = isButtonHovered(mouseX, mouseY);
        Gui.drawRect(x, y, x + width, y + height, (new Color(12, 12, 12, 140)).getRGB());
        ModuleBlur.drawBlurred(() -> Gui.drawRect(x, y, x + width, y + height, (new Color(12, 12, 12, 220)).getRGB()), false);
        fr.drawString(module.display(), x + width / 2f - fr.getStringWidth(module.display()) / 2f, y + height / 2f - fr.getFontHeight() / 2f, (hovered ? new Color(35, 60, 153) : module.enabled() ? new Color(89, 155, 255) : Color.white).getRGB());
        //module.enabled() ? hovered ? new Color(35, 60, 153) : new Color(89, 155, 255) : hovered ? new Color(35, 60, 153) : Color.white
        if (extended) {
            componentList.forEach(component -> component.drawButton(mouseX, mouseY, partialTicks));
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered) {
            if (mouseButton == 0) {
                module.toggle();
            } else if (mouseButton == 1) {
                extended = !extended;
                return true;
            }
        } else {
            if (extended) {
                componentList.forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
            }
        }
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (extended) {
            componentList.forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    private boolean isButtonHovered(int mouseX, int mouseY) {
        return RenderUtils.isInside(mouseX, mouseY, x, y, width, height);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        float yOffset = y;
        for (Component component : componentList) {
            component.x(x + width + 2);
            component.y(yOffset);
            yOffset += component.height();
        }
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
