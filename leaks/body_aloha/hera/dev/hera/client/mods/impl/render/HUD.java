package dev.hera.client.mods.impl.render;

import dev.hera.client.Client;
import dev.hera.client.events.impl.EventRenderHUD;
import dev.hera.client.events.types.EventTarget;
import dev.hera.client.mods.Category;
import dev.hera.client.mods.Mod;
import dev.hera.client.mods.ModInfo;
import dev.hera.client.utils.ColorUtils;
import dev.hera.client.utils.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.List;

@ModInfo(
        name= "HUD",
        description = "Cool hud",
        category = Category.RENDER
)
public class HUD extends Mod {

    @EventTarget
    public void onEventRender(EventRenderHUD e){
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRendererObj;
        List<Mod> mods = new java.util.ArrayList<Mod>(Client.getInstance().getModManager().mods);
        mods.sort((m1, m2) -> Integer.compare(fr.getStringWidth(m2.name), fr.getStringWidth(m1.name)));
        String text = "gethera.dev"; // make customizable later
        RenderUtils.drawRoundedRect(5, 9 - 2, 5 + 4 + fr.getStringWidth(text), 9 + fr.FONT_HEIGHT + 2, 5, ColorUtils.setColorOpacity(0xff181818, 220));
        for(int x = 5; x < 5 + 4 + fr.getStringWidth(text); x++){
            Gui.drawRect(x, 7,x + 1, 9, ColorUtils.getRainbow(4, 0.5, 1, x * 20L));
        }
        fr.drawString(text, 5 + 2, 10, -1);
        int posY = 3;
        int index = 0;
        for(Mod m : mods){
            if(!m.toggled || m == this) continue;
            index++;
            Gui.drawRect(sr.getScaledWidth(), posY, sr.getScaledWidth() - fr.getStringWidth(m.name) - 4 - 1, posY + fr.FONT_HEIGHT + 2, new Color(18, 18, 18, 180).getRGB());
            Gui.drawRect(sr.getScaledWidth(), posY, sr.getScaledWidth() - 2, posY + fr.FONT_HEIGHT + 2, ColorUtils.getRainbow(4, 0.4, 1, -(index * 200)));

            fr.drawString(m.name, sr.getScaledWidth() - fr.getStringWidth(m.name) - 2 - 2, posY + 1, -1);
            posY += fr.FONT_HEIGHT + 2;
        }

    }

}