package wtf.automn.gui.clickgui.phantom.buttons.valuecomps;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.module.settings.Setting;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;

public class Component {
    @Getter @Setter
    protected float x, y, width, height;
    protected final Setting setting;

    protected GlyphPageFontRenderer fr = ClientFont.font(20, "Arial", true);

    public boolean hovered;

    public Component(float x, float y, float width, float height, Setting setting) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setting = setting;
    }

    public void drawButton(int mouseX, int mouseY, float partialTicks) {
        hovered = isButtonHovered(mouseX, mouseY);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    private boolean isButtonHovered(int mouseX, int mouseY) {
        return RenderUtils.isInside(mouseX, mouseY, x, y, width, height);
    }
}
