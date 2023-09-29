package de.fanta.clickgui.defaultgui.components;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.gui.font.ClientFont;
import de.fanta.utils.Colors;
import de.fanta.utils.FileUtil;
import de.fanta.utils.RenderUtil;

import java.awt.Font;
import java.io.File;

public class FontButton {
    private String font;

    private float x, y;
    private boolean selected;
    private FontBox box;

    public FontButton(FontBox box, String font, float x, float y) {
        this.x = x;
        this.y = y;
        this.box = box;
        this.font = font;
    }

    public void drawButton(float mouseX, float mouseY) {
        float xOff = box.dragX;
        float yOff = box.dragY + box.scroll;

        Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(font, x + xOff + 1, y + yOff + 1.5F, selected ? Colors.getColor(242, 62, 22, 255) : -1);

        RenderUtil.rectangle(x + xOff + 171, y + yOff + 1.5F, x + xOff + 196, y + yOff + 13, Colors.getColor(25, 25, 25, 255));
        Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("Load", x + xOff + 171.5F, y + yOff + 1.5F, -1);
    }

    public void buttonClicked(float mouseX, float mouseY, int mouseButton) {
        float xOff = box.dragX;
        float yOff = box.dragY + box.scroll;

        boolean hovering = mouseX >= x + xOff + 170 && mouseY >= y + yOff + 1.5F && mouseX <= x + xOff + 195 && mouseY <= y + yOff + 13;

        if(hovering) {
            selected = true;
            Minecraft.getMinecraft().fontRendererObj = ClientFont.font(17, font, false);
        }
    }

    public void buttonReleased(float mouseX, float mouseY, int state) {
        selected = false;
    }
}
