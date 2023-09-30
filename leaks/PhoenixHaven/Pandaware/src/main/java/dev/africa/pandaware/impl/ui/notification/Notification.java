package dev.africa.pandaware.impl.ui.notification;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.font.renderer.TTFFontRenderer;
import dev.africa.pandaware.impl.module.render.HUDModule;
import dev.africa.pandaware.manager.notification.NotificationManager;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.render.RenderUtils;
import dev.africa.pandaware.utils.render.animator.Animator;
import dev.africa.pandaware.utils.render.animator.Easing;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

@Getter
public class Notification {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final double animationStart;
    private final double currentTime;
    private final double duration;
    private final String text, title;
    private final Type type;
    private final Animator xAnimator;
    private final Animator yAnimator;

    private boolean shouldAnimateBack;
    private double animationValue;
    private double xAnimation;
    private boolean animated;
    private final Color typeColor;

    private double rectPosition;
    private double maxWidth;

    public Notification(Type type, String text, double duration) {
        this(type, StringUtils.capitalize(type.name().toLowerCase()), text, duration);
    }

    public Notification(Type type, String title, String text, double duration) {
        this.type = type;
        this.text = text;
        this.title = title;

        this.animationStart = 0.062f;
        this.animationValue = animationStart * RenderUtils.fpsMultiplier();
        this.currentTime = System.currentTimeMillis();
        this.duration = (duration * 1000);
        this.xAnimation = 0;
        this.typeColor = type.getColor();
        this.animated = false;
        this.shouldAnimateBack = false;
        this.xAnimator = new Animator();
        this.yAnimator = new Animator().setValue(0);
    }

    public void render(ScaledResolution scaledResolution, int yPosition, NotificationManager notificationManager) {
        //BAR TIME
        double time = ApacheMath.abs(currentTime - System.currentTimeMillis());

        //CURRENT FONT
        TTFFontRenderer font = Fonts.getInstance().getTahomaNormal();
//        TTFFontRenderer font2 = Fonts.getInstance().getTahomaSmall();

        //CATEGORY NAME

        String categoryText = title + " (" + (MathUtils.roundToDecimal(MathHelper.clamp_double((duration - time) / 1000.0, 0, duration / 1000.0), 1)) + "s)";

        //idk i forgot
        int spacing = 1;
        //TEXT LENGTH
        double textLength = ApacheMath.max(ApacheMath.max(font.getStringWidth(text) + 25, font.getStringWidth(title) + 18), 0);

        this.maxWidth = textLength;

        double width = textLength + spacing;

        //ANIMATION SPEED
        animationValue = animationStart * RenderUtils.fpsMultiplier();

        HUDModule hudModule = Client.getInstance().getModuleManager().getByClass(HUDModule.class);

        boolean shouldCenter = hudModule.getNotificationsCenter().getValue() && mc.currentScreen == null;

        //RENDERING POSITION
        double rectX = shouldCenter ? ((1 / xAnimation) * (scaledResolution.getScaledWidth() / 2D - width / 2D)) : scaledResolution.getScaledWidth() - (width) * xAnimation,
                rectY = shouldCenter ? scaledResolution.getScaledHeight() / 2D + 20 - yPosition : scaledResolution.getScaledHeight() + yPosition - 25,
                reduction = MathHelper.clamp_double(1 - (time / duration), 0, 1);

        Easing easingMode = Easing.Elastic.QUINTIC_OUT;
        xAnimator.setEase(easingMode)
                .setMin(0)
                .setMax(1)
                .setSpeed(2f);

        if (!animated) {
            xAnimator.setReversed(false);
            xAnimator.update();
            xAnimation = xAnimator.getValue();

            //SETS ANIMATED BOOLEAN
            if (xAnimation >= 1) {
                animated = true;
            }
        } else {
            //SETS BOOLEAN FOR BACKWARDS ANIMATION
            if (reduction <= 0.01) {
                shouldAnimateBack = true;
            }
        }

        //SETS BACKWARDS ANIMATION
        if (shouldAnimateBack) {
            xAnimator.setEase(Easing.QUINTIC_IN);
            xAnimator.setMin(-0.02f).setSpeed(1.7f);

            xAnimator.setReversed(true);
            xAnimator.update();
            xAnimation = xAnimator.getValue();
//            xAnimation = MathHelper.clamp_double(xAnimation -= animationValue, 0, 1);
        }

        //REMOVES NOTIFICATION AFTER THE TIME HAS PASSED
        if (shouldAnimateBack && xAnimation <= 0.02f && animated) {
            notificationManager.getItems().remove(this);
            xAnimator.reset();
        }

        //GETS ICON AND SETS COLOR
        String icon = getIcon();

        this.rectPosition = rectX;

        //RENDERS BACKGROUND

        int rounding = mc.theWorld == null ? 0 : 3;
        int alpha = 200;

        int height = 25;

//        StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_MASK);
//        RenderUtils.drawRoundedRect(this.rectPosition, rectY - yPosition, textLength + spacing,
//                32, rounding, (new Color(0, 0, 0, alpha)));
//        StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_DRAW);
//
//        RenderUtils.drawRoundedRect(this.rectPosition, rectY - yPosition, textLength + spacing,
//                32, rounding, new Color(10, 10, 10, alpha));
//
        double bar = ((width) * (1 - reduction));
//        //RENDERS STAY TIME BAR
//        StencilUtils.stencilStage(StencilUtils.StencilStage.DISABLE);

        RenderUtils.drawRect(this.rectPosition, rectY, this.rectPosition + width, rectY + height - 1, new Color(16, 14, 8, alpha).getRGB());
        RenderUtils.drawRect(this.rectPosition + width, rectY + height - 1, rectPosition, rectY + height, typeColor.darker().darker().getRGB());
        RenderUtils.drawRect(this.rectPosition + width, rectY + height - 1, rectPosition + bar - 1, rectY + height, typeColor.darker().getRGB());
        RenderUtils.drawRect(this.rectPosition + width, rectY + height - 1, rectPosition + bar, rectY + height, typeColor.getRGB());

        //RENDERS ICON
        RenderUtils.drawImage(new ResourceLocation(icon), (int) this.rectPosition + 2, (int) (rectY) + 3, 18, 18);

        font.drawString(title/*StringUtils.capitalize(this.type.name().toLowerCase())*/,
                (float) this.rectPosition + spacing + 20, (float) (rectY) + 2, -1);

        //RENDER TEXTS
        font.drawString(text, (float) this.rectPosition + spacing + 20, (float) (rectY) + height - 4 - font.FONT_HEIGHT, -1);
    }

    //GETS ICON FROM PICKED CATEGORY
    private String getIcon() {
        String path = Client.getInstance().getManifest().getClientName().toLowerCase() + "/icons/goodnotifs/", icon = "";

        switch (type) {
            case WARNING:
                icon = path + "warning.png";
                break;
            case INFO:
                icon = path + "info.png";
                break;
            case NOTIFY:
                icon = path + "notify.png";
                break;
            case SUCCESS:
                icon = path + "okay.png";
                break;
        }

        return icon;
    }

    //CATEGORY ENUMS
    public enum Type {
        SUCCESS(new Color(65, 252, 65)),
        INFO(new Color(127, 174, 210)),
        NOTIFY(new Color(255, 255, 94)),
        WARNING(new Color(226, 87, 76));

        private final Color color;

        Type(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return this.color;
        }
    }
}
