package dev.elysium.client.ui.elements.targethud.impl;

import dev.elysium.client.ui.elements.targethud.TargetInfo;
import dev.elysium.client.ui.font.FontManager;
import dev.elysium.client.ui.font.TTFFontRenderer;
import dev.elysium.client.utils.render.ColorUtil;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class Elysium extends TargetInfo {
    public Elysium() {
        super("Elysium", 150, 42);
    }

    public void draw(float x, float y, EntityLivingBase en) {
        FontManager fm = dev.elysium.client.Elysium.getInstance().getFontManager();
        int rainbow = ColorUtil.getRainbow(1200,0.4F,1,1);
        int realdex = 0;

        RenderUtils.drawARoundedRect(x, y, x+150, y+42, 5, 0xFF121212);

        if(Double.isNaN(smoothvalue1)) smoothvalue1 = 0;
        if(smoothvalue1 > en.getMaxHealth()) smoothvalue1 = en.getMaxHealth();
        if(smoothvalue1 < 0) smoothvalue1 = 0;
        smoothvalue1 += (en.getHealth()-smoothvalue1)/45;

        double yoh = 14.5;
        double hoh = 5;

        RenderUtils.drawARoundedRect(x + 42, y + yoh, x+112, y + yoh + hoh, 10, 0xFF101010);
        RenderUtils.drawARoundedRect(x + 43, y + yoh + 1, x+111, y + yoh + hoh - 1, 10, 0xFF9e1b1b);
        RenderUtils.drawARoundedRect(x + 43, y + yoh + 1, x+43+((68/en.getMaxHealth())*smoothvalue1), y + yoh + hoh - 1, 10, 0xFFc73c3c);

        TTFFontRenderer cfr = fm.getFont("SFM 16");

        cfr.drawString(en.getName(), x+42, y+5, -1);

        RenderUtils.drawARoundedRect(x + 2.5, y+2.5, x+39.5,y+39.5, 5, 0xFF202020);
        GL11.glColor3d(1, 1, 1);
        GuiInventory.drawEntityOnScreenFree((int) (x+21),(int) (y + 36),16,(System.currentTimeMillis()%1800)/5,0, en);

        if(en instanceof EntityPlayer) {
            cfr = fm.getFont("SFL 14");
            String pms = ((EntityPlayer)en).getPing()+"ms";
            cfr.drawString(pms, x+148 - cfr.getStringWidth(pms), y+13.5F, -1);
            pms = Math.round(smoothvalue1/en.getMaxHealth()*1000F)/10F + "%";
            cfr.drawString(pms, x+148 - cfr.getStringWidth(pms), y+5, 0xFFb25f5f);
        }

        float xo = 45;
        float yo = 26;

        for(int index = 0; index <= 4; index++) {
            if(index < 4) {
                RenderUtils.drawRoundedRect(x + xo + 18*index - 3, y + 23, x + xo + 16.5 + 18*index - 3, y+39.5,5, 0xFF202020);

                if(en.getCurrentArmor(3-index) == null) continue;

                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                RenderHelper.enableGUIStandardItemLighting();

                mc.getRenderItem().renderItemAndEffectIntoGUI(en.getCurrentArmor(3-index), (int) (x + xo + 18*index - 2.5), (int) (y + yo - 3));
                mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, en.getCurrentArmor(3-index), (int) (x + xo - 3 + 18*index), (int) (y + yo - 1));

                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
            } else if(index == 4) {
                realdex = 3;
                xo += 17;

                RenderUtils.drawRoundedRect(x + xo + 18*index - 3.5, y + 23, x + xo + 16 + 18*index - 2.5, y+39.5,5, 0xFF202020);

                if(en.getHeldItem() == null) continue;

                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                RenderHelper.enableGUIStandardItemLighting();

                mc.getRenderItem().renderItemAndEffectIntoGUI(en.getHeldItem() , (int) (x + xo + 18*realdex + 15), (int) (y + yo - 2));
                mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, en.getHeldItem(), (int) (x + xo - 3 + 18*realdex + 18), (int) (y + yo - 2));

                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
            }
        }
    }
}
