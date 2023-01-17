package com.alan.clients.ui.menu.impl.intro;

import com.alan.clients.Client;
import com.alan.clients.ui.menu.impl.main.LoginMenu;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import util.time.StopWatch;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class PrereleaseDisclaimer extends GuiScreen {
    private final Animation fadeAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 5000);
    private final StopWatch stopwatch = new StopWatch();

    @Override
    public void initGui() {
        fadeAnimation.reset();
        stopwatch.reset();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.rectangle(0, 0, mc.displayWidth, mc.displayHeight, Color.BLACK);
        fadeAnimation.run(stopwatch.getElapsedTime() > 15000 ? 0 : 255);

        ScaledResolution sr = new ScaledResolution(mc);
        FontManager.getProductSansRegular(24).drawCenteredString("Note: This is private prerelease software", sr.getScaledWidth() / 2D,
                sr.getScaledHeight() / 2D - 70, ColorUtil.withAlpha(Color.WHITE, (int) fadeAnimation.getValue()).getRGB());
        FontManager.getProductSansRegular(24).drawCenteredString("Features, interfaces and sequences are not final and can be expected to change at any time", sr.getScaledWidth() / 2D,
                sr.getScaledHeight() / 2D - 50, ColorUtil.withAlpha(Color.WHITE, (int) fadeAnimation.getValue()).getRGB());
        FontManager.getProductSansRegular(24).drawCenteredString("Sharing screenshots, videos, or other information about this software is a violation of the Rise End", sr.getScaledWidth() / 2D,
                sr.getScaledHeight() / 2D - 10, ColorUtil.withAlpha(Color.WHITE, (int) fadeAnimation.getValue()).getRGB());
        FontManager.getProductSansRegular(24).drawCenteredString("User License Agreement (EULA), and will result in loss of access to prerelease builds of Rise, ", sr.getScaledWidth() / 2D,
                sr.getScaledHeight() / 2D + 10, ColorUtil.withAlpha(Color.WHITE, (int) fadeAnimation.getValue()).getRGB());
        FontManager.getProductSansRegular(24).drawCenteredString("potential loss of access to Rise and other Vantage products, and/or legal action", sr.getScaledWidth() / 2D,
                sr.getScaledHeight() / 2D + 30, ColorUtil.withAlpha(Color.WHITE, (int) fadeAnimation.getValue()).getRGB());

        FontManager.getProductSansRegular(16).drawCenteredString("© Rise Client 2022. All Rights Reserved", sr.getScaledWidth() / 2D,
                sr.getScaledHeight() / 2D + 70, ColorUtil.withAlpha(Color.WHITE, (int) fadeAnimation.getValue() / 2).getRGB());

        //ColorUtil.withAlpha(Color.WHITE, (int) fadeAnimation.getValue()).getRGB()

        if (stopwatch.finished(20000) || Client.DEVELOPMENT_SWITCH) {
            mc.displayGuiScreen(new LoginMenu());
        }
    }
}
