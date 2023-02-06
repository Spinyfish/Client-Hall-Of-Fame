package dev.elysium.client.ui.elements.targethud.impl;

import dev.elysium.client.ui.elements.targethud.TargetInfo;
import dev.elysium.client.utils.render.ColorUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class Astolfo extends TargetInfo {

    public Astolfo() {
        super("Astolfo",125,45);
    }

    public void draw(float x, float y, EntityLivingBase en) {
        Gui.drawRect(x, y, x + 125, y + 45, 0xaa000000);
        GlStateManager.color(1,1,1);
        GuiInventory.drawEntityOnScreenFree((int) (x+12),(int) (y + 42),20,-50,0, en);
        mcfr.drawStringWithShadow(en.getName(), (int) (x + 25), (int) (y + 4), -1);
        GlStateManager.scale(2,2,1);
        int color = ColorUtil.getRainbow(12,0.7F,1F,0);
        mcfr.drawStringWithShadow((""+(Math.round(en.getHealth()*10)/10F)).replaceAll("\\.",",") +  " ❤", (x + 25) / 2F, (y + 16) / 2F, color);
        GlStateManager.scale(0.5,0.5,1);
        Gui.drawRect(x + 25, y + 36, x + 122, y + 42, color);
        Gui.drawRect(x + 25, y + 36, x + 122, y + 42, 0xcc000000);
        float health = (122 - 25) / en.getMaxHealth();
        health *= en.getHealth();
        this.smoothvalue1 += (health - smoothvalue1)/5;
        Gui.drawRect(x + 25, y + 36, x + 25 + smoothvalue1, y + 42, color);
        Gui.drawRect(x + 25 + smoothvalue1-4, y + 36, x + 25 + smoothvalue1, y + 42, 0x60000000);
        //Gui.drawRect(x + 25, y + 37, x + 123, y + 42, color);
    }
}
