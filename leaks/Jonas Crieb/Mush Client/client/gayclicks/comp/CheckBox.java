package client.gayclicks.comp;


import client.gayclicks.Clickgui;
import client.gayclicks.setting.Setting;
import client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class CheckBox extends Comp {

    public CheckBox(double x, double y, Clickgui parent, Module module, Setting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        Gui.drawRect(parent.posX + x - 70, parent.posY + y, parent.posX + x + 10 - 70, parent.posY + y + 10,setting.getValBoolean() ? new Color(230,10,230).getRGB() : new Color(30,30,30).getRGB());
        Minecraft.getMinecraft().fontRendererObj.drawString(setting.getName(), (int)(parent.posX + x - 55), (int)(parent.posY + y + 1), new Color(200,200,200).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isInside(mouseX, mouseY, parent.posX + x - 70, parent.posY + y, parent.posX + x + 10 - 70, parent.posY + y + 10) && mouseButton == 0) {
            setting.setValBoolean(!setting.getValBoolean());
        }
    }

}
