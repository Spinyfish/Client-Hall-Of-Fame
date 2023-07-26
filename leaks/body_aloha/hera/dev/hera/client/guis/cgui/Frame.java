+package dev.hera.client.guis.cgui;

import dev.hera.client.Client;
import dev.hera.client.mods.Category;
import dev.hera.client.mods.Mod;
import dev.hera.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.Render;

public class Frame {

    // what is a final - xxxfrenchdev2009xxx 

    public Category category;
    public String name;
    public int x, y;

    // xd
    
    public boolean opened = false;

    public Frame(Category category, int x, int y){
        this.category = category;
        this.x = x;
        this.y = y;
        this.name = category.name.toLowerCase();
    }

    public int calculateHeight(FontRenderer fr){
        int height = 0;
        for(Mod m : Client.getInstance().getModManager().getModsBy(category)){
            height += 18;
        }
        return height;
    }

    public void render(){
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        RenderUtils.drawRoundedRect(x, y, x + 120, y + (opened ? 22 : 20), 4, 0xff262626);
        fr.drawString(name, x + 120 / 2 - fr.getStringWidth(name) / 2, y + 20 / 2 - fr.FONT_HEIGHT / 2, -1);



        if(!opened)
            return;

        int y = this.y + 20;

        /* draw bottom */
        int height = calculateHeight(fr);
        RenderUtils.drawRoundedRect(x, y + height - 18 + 2, x + 120, y + height + 2, 4, 0xff262626);

        for(Mod m : Client.getInstance().getModManager().getModsBy(category)){
            Gui.drawRect(x, y, x + 120, y + 18, m.toggled ? 0xFF419AE8 : 0xff262626);
            fr.drawString(m.name, x + 2, y + 18 / 2 - fr.FONT_HEIGHT / 2, -1);
            y += 18;
        }

    }

    public void onClick(int mouseX, int mouseY, int mouseButton){
        if(!opened)
            return;
        int y = this.y + 20;
        for(Mod m : Client.getInstance().getModManager().getModsBy(category)){
            if(mouseY >= y && mouseY <= y + 18){
                if(mouseButton == 0)
                    m.toggle();
            }
            y += 18;
        }
    }


}