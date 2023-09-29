package v4n1ty.UI;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import v4n1ty.V4n1ty;
import v4n1ty.module.Module;
import v4n1ty.utils.Wrapper;
import v4n1ty.utils.render.ColorUtils;
import v4n1ty.utils.render.RoundedUtils;

public class HUD extends GuiIngame{
    public HUD(Minecraft mcIn) {
        super(mcIn);
    }

    public void renderGameOverlay(float p_175180_1_){
        super.renderGameOverlay(p_175180_1_);

        renderHUD();
        renderArrayList();
    }

    protected Minecraft mc = Minecraft.getMinecraft();

    private void renderHUD() {
        int counter = 0;
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int y = 1;
        float fadeOffset = 0;
        int firstColor = new Color(207, 0, 255).getRGB();
        int secondColor = new Color(123, 0, 255).getRGB();
        int color = ColorUtils.fadeBetween(firstColor, secondColor, counter * 150L);
        int usernameWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(mc.thePlayer.getName());

        String serverIP;
        if (mc.isSingleplayer()) {
            serverIP = "Singleplayer";
        } else {
            serverIP = mc.getCurrentServerData().serverIP;
        }

        int serverIpWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(serverIP);
        int roundedRectWidth = usernameWidth + serverIpWidth + 14;
        fadeOffset -= 3;
        Gui.drawRect((sr.getScaledWidth() / 2 - 635), (sr.getScaledHeight() / 2 - 355), (sr.getScaledWidth() / 2 - 589 + roundedRectWidth), 22.0f, 0x80000000);
        Gui.drawRect((sr.getScaledWidth() / 2 - 635), (sr.getScaledHeight() / 2 - 355), (sr.getScaledWidth() / 2 - 589 + roundedRectWidth), 7.0f, color);
        mc.fontRendererObj.drawStringWithShadow("v4n1ty | " + mc.thePlayer.getName() + " | " + serverIP, 10.0f, 10.0f, -1);
        fadeOffset -= 3;
        counter++;
    }



    private void renderArrayList() {
        V4n1ty.instance.moduleManager.getModules().sort(Comparator.comparingInt(module ->  mc.fontRendererObj.getStringWidth(((Module)module).getName())).reversed());

        int yCount = 0;
        int index = 0;
        long x = 0;

        V4n1ty.instance.moduleManager.getModules().sort(Comparator.comparingInt(m -> -m.getName().length()));


        int counter = 0;
        for(Module m : V4n1ty.instance.moduleManager.getModules()) {
            m.onRender();

            ScaledResolution sr = new ScaledResolution(mc);
            double offset = yCount*(Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 4);

            if(m.isToggled()) {
                int firstColor = new Color(207, 0, 255).getRGB();
                int secondColor = new Color(123, 0, 255).getRGB();
                int color = ColorUtils.fadeBetween(firstColor, secondColor, counter * 150L);
                Gui.drawRect(sr.getScaledWidth() - getFontRenderer().getStringWidth(m.getName()) - 8, offset - 2, sr.getScaledWidth(), 2 + getFontRenderer().FONT_HEIGHT + offset, 0x80000000);
                Gui.drawRect(sr.getScaledWidth() - 2, 1 + offset, sr.getScaledWidth(), getFontRenderer().FONT_HEIGHT + offset, color);
                Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(m.getName(), sr.getScaledWidth() - Minecraft.getMinecraft().fontRendererObj.getStringWidth(m.getName()) - 4 , 1 +  offset , color);
                yCount++;
                index++;
                x++;
                counter++;
            }
        }
    }
}