package dev.elysium.client.ui.elements.targethud.impl;

import dev.elysium.client.Elysium;
import dev.elysium.client.ui.elements.targethud.TargetInfo;
import dev.elysium.client.ui.font.FontManager;
import dev.elysium.client.ui.font.TTFFontRenderer;
import dev.elysium.client.utils.render.ColorUtil;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Exhibition extends TargetInfo {
    public Exhibition() {
        super("Exhibition", 125, 45);
    }

    public void draw(float x, float y, EntityLivingBase en) {
        Gui.drawRect(x, y, x+width, y+height, 0xFF0a0a0a);
        double increment = .5;
        Gui.drawRect(x+increment, y+increment, x+width-increment, y+height-increment, 0xFF3c3c3c);
        increment = 1;
        Gui.drawRect(x+increment, y+increment, x+width-increment, y+height-increment, 0xFF222222);
        increment = 2.5;
        Gui.drawRect(x+increment, y+increment, x+width-increment, y+height-increment, 0xFF3c3c3c);
        increment = 3;
        Gui.drawRect(x+increment, y+increment, x+width-increment, y+height-increment, 0xFF161616);

        //Render Target Box
        Gui.drawRect(x+increment+1.5, y+increment+1.5, x-increment+43.5, y-increment+43.5, 0xFF0a0a0a);
        increment = 3.5;
        Gui.drawRect(x+increment+1.5, y+increment+1.5, x-increment+43.5, y-increment+43.5, 0xFF303030);
        increment = 4;
        Gui.drawRect(x+increment+1.5, y+increment+1.5, x-increment+43.5, y-increment+43.5, 0xFF111111);

        GlStateManager.color(1, 1, 1, 1);
        GuiInventory.drawEntityOnScreen((int) (x+23),(int) (y + 38),16,en.rotationYaw,0, (EntityLivingBase) en);


        float hue = 1000 / 360;
        hue *= en.getHealth() * 8;
        hue *= 0.001f;
        int color = Color.HSBtoRGB(hue,1, 1);

        //Render Healthbar
        Gui.drawRect(x+42, y+14.5, x+104, y+18.5, 0xFF000000);
        Gui.drawRect(x+42.5, y+15, x+103.5, y+18, color);
        Gui.drawRect(x+42.5, y+15, x+103.5, y+18, 0xC0000000);
        Gui.drawRect(x+42.5, y+15, x+42.5+(61/en.getMaxHealth())*en.getHealth(), y+18, color);

        //Per 6
        double permove = 6.1;

        for(int index = 0; index+1 < 10; index++) {
            Gui.drawRect(x+42.5 + permove + index * permove, y+14.5, x+43 + permove+ index * permove, y+18.5, 0xFF000000);
        }

        int realdex = 0;

        Elysium.getInstance().fm.getFont("SFM 16").drawStringWithShadow(en.getName(), x+42, (float) (y+5.5), -1);

        GlStateManager.scale(0.5,0.5,1);
        mcfr.drawString("HP: " + Math.round(en.getHealth()) + " | Dist: " + Math.round(mc.thePlayer.getDistanceToEntity(en)), 2*(x+42.9F), 2*(y+20), -1, false);
        GlStateManager.scale(2,2,1);

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        for(int index = 0; index <= 4; index++) {
            if(index < 4 && en.getCurrentArmor(3-index) != null) {
                RenderUtils.drawItemStack(en.getCurrentArmor(3-index), (int) (x + 44 + 16*realdex), (int) (y + 28), 1);
                mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, en.getCurrentArmor(3-index), (int) (x + 43 + 16*realdex), (int) (y + 25));
                realdex++;
            } else if(index == 4 && en.getHeldItem() != null) {
                RenderUtils.drawItemStack(en.getHeldItem() , (int) (x + 44 + 16*realdex), (int) (y + 28), 1);
                mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, en.getHeldItem(), (int) (x + 41 + 16*realdex), (int) (y + 25));
            }
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
}
