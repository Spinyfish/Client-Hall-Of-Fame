package tech.dort.dortware.impl.gui.vaziakclick.element.impl.setting.impl;

import net.minecraft.util.ResourceLocation;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.font.CustomFontRenderer;
import tech.dort.dortware.api.property.Value;
import tech.dort.dortware.impl.gui.vaziakclick.GuiUtils;
import tech.dort.dortware.impl.gui.vaziakclick.element.impl.setting.Setting;

import java.awt.*;

public class ToggleSetting extends Setting {

    protected ResourceLocation checked = new ResourceLocation("dortware/settings/checked.png");
    protected ResourceLocation unchecked = new ResourceLocation("dortware/settings/unchecked.png");
    private final int iconWidth = 13;
    private final int iconHeight = 13;
    private float imagePosX;
    private float imagePosY;

    public ToggleSetting(Value value, int width) {
        this.width = width;
        this.value = value;
        this.height = 16;
    }

    @Override
    public void onDraw(int mouseX, int mouseY, float partialTicks) {
        final CustomFontRenderer font = Client.INSTANCE.getFontManager().getFont("Small1").getRenderer();
        boolean enabled = (boolean) value.getValue();
        float yPadding = ((height /*header height*/ - (font.getHeight(value.getName()))) / 2);
        float xPaddingImage = ((width - SETTING_PADDING) - iconWidth);
        float heightPadding = ((height - (iconHeight))) / 2F;
        font.drawString(value.getName(), posX + SETTING_PADDING /*offset * 2*/, posY + yPadding, -1);
        imagePosX = posX + xPaddingImage;
        imagePosY = posY + heightPadding;
        GuiUtils.glResets();
        GuiUtils.drawImage(enabled ? checked : unchecked, imagePosX, imagePosY, iconWidth, iconHeight, enabled ? -1 : new Color(120, 120, 120).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtils.isHovering(mouseX, mouseY, (int) imagePosX, (int) imagePosY, iconWidth, iconHeight)) {
            boolean enabled = (boolean) value.getValue();
            value.setValueAutoSave(!enabled);
        }
    }
}
