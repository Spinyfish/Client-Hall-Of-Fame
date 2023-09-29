package dev.elysium.client.ui.elements.targethud.impl;

import dev.elysium.client.ui.elements.targethud.TargetInfo;
import dev.elysium.client.utils.render.ColorUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class Novoline extends TargetInfo {
    public Novoline() {
        super("Novoline",100,40);
    }

    public void draw(float x, float y, EntityLivingBase en) {
        float length = 41+mcfr.getStringWidth(en.getName());
        if(length < 90)
            length = 90;
        width = length;
        int color = ColorUtil.getRainbow(12,0.7F,1F,0);
        Gui.drawRect(x,y,x+width,y+36,0xEE262324);

        if(en instanceof EntityPlayer) {
            mc.getTextureManager().bindTexture(((AbstractClientPlayer)en).getLocationSkin());
            GlStateManager.color(1,1,1);
            Gui.drawScaledCustomSizeModalRect((int)x+2,(int)y+2,8,8,8,8,32,32,64,64);
            GlStateManager.resetColor();
        }

        mcfr.drawStringWithShadow(en.getName(),x+38,y+4,-1);

        float health = (length - 41) / en.getMaxHealth();
        health *= en.getHealth();
        this.smoothvalue1 += (health - smoothvalue1)/5;
        Gui.drawRect(x+38,y+18,x+38+(length-41),y+29,0x60000000);
        Gui.drawRect(x+38,y+18,x+38+smoothvalue1,y+29,color);
        mcfr.drawCenteredString(Math.round(en.getHealth()*1000/en.getMaxHealth())/10F+"%",x+34+(length - 34)/2,y+20,-1);
    }
}
