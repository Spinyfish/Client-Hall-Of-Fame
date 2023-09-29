package dev.elysium.client.ui;

import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.ui.font.TTFFontRenderer;
import dev.elysium.client.utils.api.Hypixel;
import dev.elysium.client.utils.render.ColorUtil;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Comparator;

public class HUD {

    public Minecraft mc = Minecraft.getMinecraft();
    public boolean hasUpdated = false;

    public void draw(double width, double height, FontRenderer fr) {
        long updateDelay = 60000;
        fr.drawStringWithShadow("bps : " + Math.round(mc.thePlayer.getBPS()*100)/100F,3, (float) (height - fr.FONT_HEIGHT - 3), -1);

        ScaledResolution sr = new ScaledResolution(mc);
        TTFFontRenderer cfr = Elysium.getInstance().getFontManager().getFont("Poppins-Medium 18");

        int i = 0;

        Elysium.getInstance().getModManager().getMods().sort(Comparator.comparingInt(m -> fr.getStringWidth(((Mod)m).name)).reversed());
        for(Mod m : Elysium.getInstance().getModManager().getMods()) {
            if(m.toggled) {
                int color = ColorUtil.getRainbow(12,0.7F,1F,i * 300);
                fr.drawStringWithShadow(m.name, sr.getScaledWidth() - 4 - fr.getStringWidth(m.name), 4 + (fr.FONT_HEIGHT + 2) * i++, color);
            }
        }
        if(Hypixel.bansSinceStart == null) {
            Hypixel.getBanStats();
            return;
        }
        if(Elysium.getInstance().getModManager().getModByName("BanStats").toggled) {
            long sessiontime = (System.currentTimeMillis() - Elysium.getInstance().timeLaunched) / 1000;
            String displaytime = "";
            if (sessiontime > 60) {
                String minutes = Double.toString(Math.floor(sessiontime / 60));
                displaytime = minutes.substring(0, minutes.indexOf(".")) + "m " + sessiontime % 60 + "s";
            } else {
                displaytime = sessiontime + "s";
            }
            float boxwidth = fr.getStringWidth(Hypixel.bansSinceStart[0] + " <- Watchdog | Staff -> " + Hypixel.bansSinceStart[1]);

            RenderUtils.drawSkeetoRect(width/2 - boxwidth/3, 5,width/2 + boxwidth/3, 36);
            fr.drawSmallStringCentered("Session Time : " + displaytime, (float) (width / 2), 11, -1);
            fr.drawSmallStringCentered(Hypixel.bansSinceStart[0] + " <- Watchdog | Staff -> " + Hypixel.bansSinceStart[1], (float) (width / 2), 18, -1);
            fr.drawSmallStringCentered("Updating in " + (updateDelay - (System.currentTimeMillis() % updateDelay)) / 1000 + "s", (float) (width / 2), 26, -1);

            if (System.currentTimeMillis() % updateDelay > updateDelay-10) {
                if(hasUpdated)
                    return;
                hasUpdated = true;
                Elysium.getInstance().addChatMessage("Updating Ban Stats...");
                new Thread(new Runnable() {
                    public void run() {
                        int[] bans = Hypixel.getBanStats();
                        Elysium.getInstance().addChatMessage("Watchdog (+" + bans[0] + ")");
                        Elysium.getInstance().addChatMessage("Staff (+" + bans[1] + ")");
                    }
                }).start();
            } else
                hasUpdated = false;
        }
    }
}
