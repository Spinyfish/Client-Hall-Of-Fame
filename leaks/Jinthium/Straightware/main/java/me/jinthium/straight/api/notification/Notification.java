package me.jinthium.straight.api.notification;


import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.impl.SmoothStepAnimation;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import net.minecraft.client.gui.ScaledResolution;

public class Notification implements Util, MinecraftInstance {
    private final Animation slideAnimation;
    private final TimerUtil timerUtil;
    private final String title, content;
    private final NotificationType notificationType;
    private final float time;

    public Notification(String title, String content, NotificationType notificationType, float time){
        this.title = title;
        this.content = content;
        this.notificationType = notificationType;
        this.time = (time * 1000);
        timerUtil = new TimerUtil();
        slideAnimation = new SmoothStepAnimation(200, 1);
    }

    public Notification(String title, String content, NotificationType notificationType){
        this(title, content, notificationType, 3);
    }

    public Animation getSlideAnimation() {
        return slideAnimation;
    }

    public TimerUtil getTimerUtil() {
        return timerUtil;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public float getTime() {
        return time;
    }
}
