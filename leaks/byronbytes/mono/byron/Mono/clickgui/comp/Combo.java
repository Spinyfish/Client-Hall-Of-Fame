package byron.Mono.clickgui.comp;

import byron.Mono.Mono;
import byron.Mono.clickgui.Clickgui;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.font.FontUtil;
import byron.Mono.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

import org.lwjgl.opengl.GL11;

public class Combo extends Comp {

    public Combo(double x, double y, Clickgui parent, Module module, Setting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isInside(mouseX, mouseY, parent.posX + x - 70, parent.posY + y, parent.posX + x, parent.posY + y + 10) && mouseButton == 0) {
            int max = setting.getOptions().size();
            if (parent.modeIndex + 1 >= max) {
                parent.modeIndex = 0;
            } else {
                parent.modeIndex++;
            }
            setting.setValString(setting.getOptions().get(parent.modeIndex));
        }
    }
    
    public static final void drawSmoothRect(float left, float top, float right, float bottom, int color) {
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        Gui.drawRect(left, top, right, bottom, color);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Gui.drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
        Gui.drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
        Gui.drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
        Gui.drawRect(left * 2.0f, bottom * 2.0f - 1.0f, right * 2.0f, bottom * 2.0f, color);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }
    

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        drawSmoothRect((float) parent.posX + x - 71, parent.posY + y - 1, parent.posX + x + 20, parent.posY + y + 11,setting.getValBoolean() ? new Color(40,10,40).getRGB() : new Color(20,20,20).getRGB());
        FontUtil.normal.drawString(setting.getName() + ":§7 " + setting.getValString(), (int)(parent.posX + x - 69), (int)(parent.posY + y + 1), new Color(200,200,200).getRGB());
    }


    private void drawSmoothRect(double d, double e, double f, double g, int color) {
    	// its actually incorrect but ill fix it later bc im lazy
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        Gui.drawRect(d, e, f, g, color);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Gui.drawRect(d * 2.0f - 1.0f, e * 2.0f, f * 2.0f, g * 2.0f - 1.0f, color);
        Gui.drawRect(d * 2.0f, e * 2.0f - 1.0f, f * 2.0f, g * 2.0f, color);
        Gui.drawRect(d * 2.0f, e * 2.0f, f * 2.0f + 1.0f, g * 2.0f - 1.0f, color);
        Gui.drawRect(d * 2.0f, e * 2.0f - 1.0f, f * 2.0f, g * 2.0f, color);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
		
	}

}
