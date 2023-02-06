package dev.elysium.client.ui.elements.targethud.impl;

import dev.elysium.client.ui.elements.targethud.TargetInfo;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class Cross extends TargetInfo {
    public Cross() {
        super("Cross", 12, 12);
    }

    public void draw(float x, float y, EntityLivingBase en) {
        /**
        mcfr.drawSmallStringCentered("[TARGET LOCKED]",x,y-24,-1);
        mcfr.drawSmallStringCentered(en.getName(),x,y+20,-1);
        Gui.drawRect(x-17,y-17,x+17,y+17,0x80800000);
        Gui.drawRect(x-15,y-15,x+15,y+15,0x40FF0000);
         **/
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Gui.icons);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
        GlStateManager.enableAlpha();
        Gui.drawModalRectWithCustomSizedTexture((int) x- 7, (int) y - 7, 0, 0, 16, 16,260,260);
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableBlend();
    }
}
