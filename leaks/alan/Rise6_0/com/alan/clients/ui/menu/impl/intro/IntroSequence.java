package com.alan.clients.ui.menu.impl.intro;

import com.alan.clients.Client;
import com.alan.clients.ui.menu.impl.main.LoginMenu;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.render.RenderUtil;
import util.time.StopWatch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Hazsi
 * @since 10/12/2022
 */
public class IntroSequence extends GuiScreen {

    private final String username = ""; // ALAN TODO THIS WITH VANTAGE STUFF
    private final String usernameMod = username == "" ? username : ", " + username + ",";

    private final ArrayList<Line> lines = new ArrayList<Line>() {{
        add(new Line("Around the release of Rise 5.3, the team behind Rise began to envision a new project", 10000));
        add(new Line("This project came to be known as Rise 6.", 5000));
        add(new Line("", 500));
        add(new Line("Today", 2000));
        add(new Line("Thousands of development hours from some of the most talented minds in the community", 7500));
        add(new Line("Hundreds of hours of testing and feedback from dedicated beta testers", 6000));
        add(new Line("", 500));
        add(new Line("It's ready", 2000));
        add(new Line("", 500));
        add(new Line("With the highest quality features ever seen in a client", 5000));
        add(new Line("The highest frame rates ever seen in a Minecraft client of any kind", 6000));
        add(new Line("The smoothest and most meticulously polished user experience ever", 6000));
        add(new Line("And so many more powerful and unique features in development for updates in the very near future", 9000));
        add(new Line("We're so incredibly proud to show you the future of Rise.", 6000));
        add(new Line("", 500));
        add(new Line("Welcome" + usernameMod + " to the pioneer of the next generation of Minecraft clients.", 7000));
    }};

    private final StopWatch timeTracker = new StopWatch();
    private int currentLine = 0;
    private boolean started = false;

    private final Animation logoAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 3000);

    @Override
    public void initGui() {
        // TODO FIRST LAUNCH DETECTION
//        if (!Client.FIRST_LAUNCH) {
//            mc.displayGuiScreen(Client.BETA_SWITCH ? new PrereleaseDisclaimer() : new LoginMenu());
//        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        // Init GUI is called erroneously, use this instead.
        if (!started) {
            this.started = true;

            this.timeTracker.reset();
            this.logoAnimation.setValue(255);
            this.logoAnimation.reset();
        }

//        if (currentLine >= lines.size()) {
//            mc.displayGuiScreen(new MainMenu());
//            return;
//        }

//        RenderUtil.rectangle(0, 0, mc.displayWidth, mc.displayHeight, Color.BLACK);
//        FontRenderer fr = FontManager.getProductSansRegular(24);
//        ScaledResolution sr = new ScaledResolution(mc);
//        Line line = lines.get(currentLine);
//
//        fr.drawCenteredString(line.getText(), sr.getScaledWidth() / 2D, sr.getScaledHeight() / 2D, -1);
//
//        if (timeTracker.finished(line.getLength())) {
//            timeTracker.reset();
//            currentLine++;
//        }

        this.logoAnimation.run(0);

        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.color(Color.WHITE);
        RenderUtil.rectangle(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), Color.BLACK);
        RenderUtil.image(new ResourceLocation("rise/images/splash.png"), sr.getScaledWidth() / 2D - 75,
                sr.getScaledHeight() / 2D - 25, 150, 50, new Color(255, 255, 255, (int) this.logoAnimation.getValue()));

        if (this.timeTracker.finished(4000)) {
            mc.displayGuiScreen(Client.BETA_SWITCH ? new PrereleaseDisclaimer() : new LoginMenu());
        }
    }

    @Getter
    @AllArgsConstructor
    private static class Line {
        private final String text;
        private final long length;
    }
}