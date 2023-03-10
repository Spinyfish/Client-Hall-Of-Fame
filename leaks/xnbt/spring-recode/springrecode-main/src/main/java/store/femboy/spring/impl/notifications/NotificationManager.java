package store.femboy.spring.impl.notifications;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import store.femboy.spring.impl.util.FontUtil;
import store.femboy.spring.impl.util.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

public class NotificationManager {
    private static final RenderUtil utilRender = new RenderUtil();
    private static final int delay = 3500;

    public static void drawScreen(ScaledResolution sr) {
        if (!Notification.getNotifications().isEmpty()) {
            ArrayList<Notification> notifications = new ArrayList<>(Notification.getNotifications());
            for (Notification n : notifications) {
                if (n.isNoSet()) {
                    animateIncoming(n, sr);
                }
                if (n.getX() <= sr.getScaledWidth() - n.getLength()) {
                    n.setNoSet(false);
                }
                Notification.updateY(sr);


                Gui.drawRect((int) n.getX(), (int) n.getY(), (int) n.getX2(), (int) n.getY2(), new Color(10, 10, 10, 225).getRGB());
                FontUtil.icons45.drawString(n.getPrefix(), n.getX() + 5, n.getY() + n.getHeight() / 2f - FontUtil.icons45.getHeight() / 2f + 1.5f, n.getColor().getRGB());
                FontUtil.nunitobold22.drawString(String.valueOf(n.getType()), n.getX() + 5 + FontUtil.icons45.getStringWidth(n.getPrefix()), n.getY() + n.getHeight() / 2f - FontUtil.nunitobold22.getHeight() / 2f - 5, -1);
                FontUtil.nunito21.drawString(n.getMessage(), n.getX() + 5 + FontUtil.icons45.getStringWidth(n.getPrefix()), n.getY() + n.getHeight() / 2f - FontUtil.nunito21.getHeight() / 2f + n.getHeight() / 4.25f, -1);

                if (n.getUtilTimer().hasTimeElapsed(delay)) {
                    animateRetreat(n, sr);
                } else {
                    Gui.drawRect((int) n.getX(), (int) (n.getY() - 1), (int) (n.getX2() - n.getLength() * getLengthPercentage(n)), (int) n.getY(), n.getColor().getRGB());
                }

            }
        }
    }

    public static void animateRetreat(Notification n, ScaledResolution sr) {
        n.getAnimation3().update();
        n.getAnimation3().animate(n.getLength() + 1, 0.1, false);
        n.setX((float) ((sr.getScaledWidth() - n.getLength()) + n.getAnimation3().getValue()));
        if (n.getX() > sr.getScaledWidth()) {
            n.getUtilTimer().reset();
            n.removeNotification();
        }
    }

    public static void animateIncoming(Notification n, ScaledResolution sr) {
        n.getAnimation().update();
        n.getAnimation().animate(n.getLength(), 0.2f, false);
        n.setX((float) Math.max(sr.getScaledWidth() - n.getAnimation().getValue(), sr.getScaledWidth() - n.getLength()));
    }

    public static float getLengthPercentage(Notification n) {
        return (float) n.getUtilTimer().getTime() / delay;
    }
}
