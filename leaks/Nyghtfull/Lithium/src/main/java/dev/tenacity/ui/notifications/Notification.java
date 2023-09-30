package dev.tenacity.ui.notifications;

import dev.tenacity.Tenacity;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.impl.render.PostProcessing;
import dev.tenacity.utils.Utils;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.time.TimerUtil;
import dev.tenacity.utils.tuples.Pair;
import lombok.Getter;
import net.minecraft.client.gui.Gui;

import java.awt.*;

@Getter
public class Notification implements Utils {

    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TimerUtil timerUtil;
    private final Animation animation;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long) (time * 1000);
        timerUtil = new TimerUtil();
        this.notificationType = type;
        animation = new DecelerateAnimation(250, 1);
    }


    public void drawDefault(float x, float y, float width, float height, float alpha, boolean onlyTitle) {

        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);


        //Icon
       // String icon = getNotificationType().getIcon();
     //   FontUtil.iconFont35.drawString(getNotificationType().getIcon(), x + 5, (y + FontUtil.iconFont35.getMiddleOfBox(height) + 1), notificationColor);

        if (onlyTitle) {
            lithiumBoldFont22.drawString(getTitle(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()),
                    y + lithiumBoldFont22.getMiddleOfBox(height), textColor);
        } else {
            lithiumBoldFont22.drawString(getTitle(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()), y + 4, textColor);
            lithiumFont18.drawString(getDescription(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()), y + 7 + lithiumBoldFont22.getHeight(), textColor);
        }

    }

    public void blurDefault(float x, float y, float width, float height, float alpha, boolean glow) {
        int i = 0;

        int index = (int) (i * 20);
        Pair<Color, Color> colors = HUDMod.getClientColors();

        Color textcolor = ColorUtil.interpolateColorsBackAndForth(5, index, colors.getFirst(), colors.getSecond(), false);
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, textcolor.brighter().brighter(), glow ? .65f : 0), alpha);
        RoundedUtil.drawRound(x + 20, y, width, height, 4, color);
    }


    public void drawExhi(float x, float y, float width, float height) {
        boolean lowerAlpha = Tenacity.INSTANCE.getModuleCollection().getModule(PostProcessing.class).isEnabled();
        Gui.drawRect2(x, y, width, height, new Color(0.1F, 0.1F, 0.1F, lowerAlpha ? 0.4F : .75f).getRGB());
        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);
        Gui.drawRect2(x + (width * percentage), y + height - 1, width - (width * percentage), 1, getNotificationType().getColor().getRGB());
        FontUtil.iconFont40.drawString(getNotificationType().getIcon(), x + 3, (y + FontUtil.iconFont40.getMiddleOfBox(height) + 1), getNotificationType().getColor());

        CustomFont tahomaFont18 = tahomaFont.size(18);
        tahomaFont18.drawString(getTitle(), x + 7 + FontUtil.iconFont40.getStringWidth(getNotificationType().getIcon()), y + 4, Color.WHITE);
        tahomaFont.size(14).drawString(getDescription(), x + 7 + FontUtil.iconFont40.getStringWidth(getNotificationType().getIcon()), y + 8.5f + tahomaFont18.getHeight(), Color.WHITE);
    }

    public void blurExhi(float x, float y, float width, float height) {
        Gui.drawRect2(x, y, width, height, Color.BLACK.getRGB());
        RenderUtil.resetColor();
    }

    public void drawSuicideX(float x, float y, float width, float height, float animation) {
        float heightVal = height * animation <= 6 ? 0 : height * animation;
        float yVal = (y + height) - heightVal;

        RoundedUtil.drawRound(x, y, width, height, 4, new Color(255, 255, 255, 15));

        lithiumBoldFont22.drawCenteredString(title, x + width / 2f,
                yVal + lithiumBoldFont22.getMiddleOfBox(heightVal), ColorUtil.applyOpacity(Color.WHITE, animation - .5f));

    }

    public void blurSuicideX(float x, float y, float width, float height, float animation) {
        float heightVal = height * animation <= 6 ? 0 : height * animation;
        float yVal = (y + height) - heightVal;
        RoundedUtil.drawRound(x, yVal, width, heightVal, 4, Color.BLACK);
    }

}
