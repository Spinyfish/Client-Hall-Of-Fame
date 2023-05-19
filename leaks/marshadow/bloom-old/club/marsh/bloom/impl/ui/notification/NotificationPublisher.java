package club.marsh.bloom.impl.ui.notification;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.Render2DEvent;
import club.marsh.bloom.impl.utils.render.BlurUtil;
import club.marsh.bloom.impl.utils.render.FontRenderer;

import club.marsh.bloom.impl.utils.render.RenderUtil;
import club.marsh.bloom.impl.utils.render.Translate;
import com.google.common.eventbus.Subscribe;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;

public class NotificationPublisher {

    public NotificationPublisher() {
        Bloom.INSTANCE.eventBus.register(this);
    }

    public ConcurrentSet<Notification> notifications = new ConcurrentSet<>();
    public void publish(String name, String desc, long time, NotificationType type) {
        notifications.add(new Notification(name,desc,time,type));
    }
    @Subscribe
    public void onRender2D(Render2DEvent e) {
        try {
            FontRenderer fr = Bloom.INSTANCE.fontManager.defaultFont;
            int notisize = 0;
            long time = System.currentTimeMillis();
            for (Notification notification : notifications) {
                notisize++;
                if (Math.abs(time - notification.publishedtime) < notification.time) {
                    float translationfactor = 50;
                    notification.translate.x = (int) (ScaledResolution.getScaledWidth() - 15 - (fr.getWidth(notification.desc)));
                    notification.translate.y = ScaledResolution.getScaledHeight() - 30;
                    notification.translate.y -= notisize * 25;
                    //RenderUtil.drawRoundedRect((float) notification.translate.x, (float) (notification.translate.y - 10), (float) ScaledResolution.getScaledWidth(), (float) (notification.translate.y + 10), 10, new Color(50, 50, 50, 255));
                    Bloom.INSTANCE.bloomHandler.bloom((int) notification.translate.x, (int) (notification.translate.y - 10),(int) Math.abs(notification.translate.x-ScaledResolution.getScaledWidth()),20,10,new Color(0,0,0,150));
                    fr.drawString(notification.name, (int) (notification.translate.x), (int) (notification.translate.y - 8), new Color(255, 255, 255, 150).getRGB());
                    fr.drawString(notification.desc, (int) (notification.translate.x), (int) (notification.translate.y), new Color(255, 255, 255, 150).getRGB());
                    //timebar
                    Gui.drawRect( ScaledResolution.getScaledWidth() - 5, (int) (notification.translate.y - 10), ScaledResolution.getScaledWidth(), (int) (notification.translate.y + 10), notification.type.color.getRGB());
                    double timeThing = (double) Math.abs(time - notification.publishedtime) / (double) (notification.time);
                    //System.out.println(timeThing);
                    Gui.drawRect((int) ((int) (notification.translate.x) + (timeThing*Math.abs(notification.translate.x - (ScaledResolution.getScaledWidth()-5)))), (int) (notification.translate.y + 15), ScaledResolution.getScaledWidth(), (int) (notification.translate.y + 10), notification.type.color.getRGB());
                } else {
                    notifications.remove(notification);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
