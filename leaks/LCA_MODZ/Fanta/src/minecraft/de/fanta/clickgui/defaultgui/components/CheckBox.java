package de.fanta.clickgui.defaultgui.components;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.setting.Setting;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;

public class CheckBox {
    public float x;
    public float y;
    private CategoryPanel panel;
    private Setting setting;
    int ani = 0;

    public CheckBox(Setting setting, CategoryPanel panel, float x, float y) {
        this.setting = setting;
        this.panel = panel;
        this.x = x;
        this.y = y;
    }

    public void drawCheckBox(float mouseX, float mouseY) {
        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        boolean hovering = mouseX >= x + xOff && mouseY >= y + yOff && mouseX <= x + xOff + 9 && mouseY <= y + yOff + 9;

        GL11.glPushMatrix();

        int color = hovering ? Colors.getColor(150, 150, 150, 255) : Colors.getColor(110, 110, 110, 255);

        if(((de.fanta.setting.settings.CheckBox) setting.getSetting()).state) {
            ani += 5;
            color = Colors.getColor(200, 50, 0, 255 + ani);
        }else{
            color = Colors.getColor(200, 50, 0, 255 + ani);
            ani -= 5;
        }

        if (ani >= 255) {
            ani = 255;
        }

        if (ani < 0) {
            ani = 0;
            color = hovering ? Colors.getColor(150, 150, 150, 255) : Colors.getColor(110, 110, 110, 255);
        }

        GL11.glColor4f(0.0F,0.0F,0.0f,0.0F);
        GL11.glPopMatrix();


        Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(setting.getName(), (int) (x + xOff + 12), (int) (y + yOff - 2), -1);

        RenderUtil.rectangle(x + xOff, y + yOff, x + xOff + 9, y + yOff + 9, color);
    }

    public void checkBoxClicked(float mouseX, float mouseY, int mouseButton) {
        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        boolean hovering = mouseX >= x + xOff && mouseY >= y + yOff && mouseX <= x + xOff + 9 && mouseY <= y + yOff + 9;

        if(hovering)
            ((de.fanta.setting.settings.CheckBox) setting.getSetting()).state = !((de.fanta.setting.settings.CheckBox) setting.getSetting()).state;
    }
}
