package me.r.touchgrass.ui.mainmenu;

import me.r.touchgrass.font.FontHelper;
import me.r.touchgrass.font.FontUtil;
import me.r.touchgrass.utils.ParticleGenerator;
import me.r.touchgrass.utils.Utils;
import me.r.touchgrass.touchgrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.awt.*;

/**
 * Created by r on 26/02/2021
 */
public class MainMenu extends GuiScreen {

    static String splashText;

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static void drawMenu(int mouseX, int mouseY) {
        if(touchgrass.getClient().panic) {
            return;
        }

        // side menu rects (buttons)

        //drawRect(40, 0, 140, Utils.getScaledRes().getScaledHeight(), 0x60000000);
        //drawRect(40, 0, 41, Utils.getScaledRes().getScaledHeight(), 0x60000000);
        drawRect(139, 0, 140, Utils.getScaledRes().getScaledHeight(), 0x60000000);

        // right hand strings
        String mname = String.format("Logged in as §7%s", Minecraft.getMinecraft().getSession().getUsername());
        String devs = String.format("Developed by §7%s §fand §7%s", touchgrass.devs[0], touchgrass.devs[1]);

        FontHelper.comfortaa_r.drawStringWithShadowMainMenu(devs, Utils.getScaledRes().getScaledWidth() - FontHelper.comfortaa_r.getStringWidth(devs) - 4, 16, new Color(19,109,21));
        FontHelper.comfortaa_r.drawStringWithShadowMainMenu(mname, Utils.getScaledRes().getScaledWidth() - FontHelper.comfortaa_r.getStringWidth(mname) - 4, 28, new Color(19,109,21));
        // first start

        if(touchgrass.getClient().firstStart) {
            FontUtil.drawTotalCenteredStringWithShadowSFL2("Welcome to", Utils.getScaledRes().getScaledWidth() / 2 - 22, Utils.getScaledRes().getScaledHeight() / 2 - (FontHelper.sf_l2.getHeight() / 2) - 35, new Color(207, 238, 255));
        }

        FontHelper.sf_l_mm.drawString("TouchGrass", Utils.getScaledRes().getScaledWidth() / 2 - 43, Utils.getScaledRes().getScaledHeight() / 2 - 36, new Color(51, 50, 50));
        FontHelper.sf_l_mm.drawString("TouchGrass", Utils.getScaledRes().getScaledWidth() / 2 - 45, Utils.getScaledRes().getScaledHeight() / 2 - 37, new Color(19,109,21));

        FontUtil.drawTotalCenteredStringWithShadowComfortaa("Go Touch Some Grass!", Utils.getScaledRes().getScaledWidth() / 2 + (FontHelper.sf_l_mm.getStringWidth("touchgrass") / 2) - 46, Utils.getScaledRes().getScaledHeight() / 2 + 33, Color.WHITE);
    }

}