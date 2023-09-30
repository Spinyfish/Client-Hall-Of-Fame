package club.pulsive.api.ui.config;

import club.pulsive.api.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public class CustomButton extends GuiButton {

    public CustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {

        this.hovered = isHovered(xPosition, yPosition, xPosition+width, yPosition+height, mouseX, mouseY);

        Color color = this.hovered ? new Color(66,68,80, 220) : new Color(76,78,90, 220);

        Gui.drawRect(xPosition, yPosition - 1, xPosition+width, yPosition+height + 1, 0xFF121212);
        Fonts.fontforflashy.drawCenteredStringWithShadow(this.displayString, xPosition+width/2
        , yPosition+height/2-mc.fontRendererObj.FONT_HEIGHT/2, -1);


    }

    public boolean isHovered(double x, double y, double width, double height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < width && mouseY < height;
    }
}
