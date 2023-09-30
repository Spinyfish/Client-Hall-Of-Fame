package me.jinthium.straight.impl.manager;

import me.jinthium.straight.api.notification.Notification;
import me.jinthium.straight.api.notification.NotificationType;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.modules.visual.Hud;
import me.jinthium.straight.impl.modules.visual.PostProcessing;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.render.TaskUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager implements Util, MinecraftInstance {

    private final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public void post(String title, String content, NotificationType notificationType){
        post(new Notification(title, content, notificationType, 1.5f));
    }

    public void post(String title, String content, NotificationType notificationType, float time){
        post(new Notification(title, content, notificationType, time));
    }

    public void post(Notification notification){
        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
        if(Client.INSTANCE.getModuleManager().getModule(Hud.class).isEnabled()) {
            if(hud.getModeSetting("Notification Mode").is("Minecraft"))
                notifications.add(notification);
            else
                TaskUtil.INSTANCE.sendNotification(TaskUtil.MessageType.INFO, notification.getTitle(), notification.getContent());
        }
    }

    public void render(boolean blur){
        if(notifications.isEmpty())
            return;

        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        float y = scaledResolution.getScaledHeight() - 40;
        for (Notification notification : notifications) {
            if(notification == null)
                return;

            if(notification.getSlideAnimation().finished(Direction.BACKWARDS))
                notifications.remove(notification);

            if(notification.getTimerUtil().hasTimeElapsed(notification.getTime()))
                notification.getSlideAnimation().setDirection(Direction.BACKWARDS);

            float x = (scaledResolution.getScaledWidth() - (normalFont19.getStringWidth(notification.getContent()) + 40)
                    * notification.getSlideAnimation().getOutput().floatValue());


            if(Client.INSTANCE.getModuleManager().getModule(PostProcessing.class).isEnabled() && blur){
                Gui.drawRect2(x, y,
                        scaledResolution.getScaledWidth() - 2, 30, Color.BLACK.getRGB());
            }else {
                Gui.drawRect2(x, y,
                        scaledResolution.getScaledWidth() - 2, 30, 0x90000000);
            }

            if(!blur) {
                iconFont20.drawStringWithShadow(notification.getNotificationType().getIcon(), x + 2, y + 2,
                        -1);

                normalFont19.drawStringWithShadow(notification.getTitle(), x + iconFont20.getStringWidth(notification.getNotificationType().getIcon()) + 4, y + 3, -1);
                normalFont19.drawStringWithShadow(notification.getContent(), x + iconFont20.getStringWidth(notification.getNotificationType().getIcon()) + 4, y + 22, -1);
            }

            y -= Client.INSTANCE.getModuleManager().getModule(PostProcessing.class).isEnabled() ? 30 + normalFont19.getHeight() : 23 + normalFont19.getHeight();
        }
    }
}
