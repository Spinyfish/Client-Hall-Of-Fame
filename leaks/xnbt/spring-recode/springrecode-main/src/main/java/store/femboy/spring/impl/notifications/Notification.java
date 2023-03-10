package store.femboy.spring.impl.notifications;

import com.sun.istack.internal.NotNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import ru.hogoshi.Animation;
import store.femboy.spring.impl.util.FontUtil;
import store.femboy.spring.impl.util.TimeUtil;

import java.awt.*;
import java.util.ArrayList;

public class Notification {
    private static final ArrayList<Notification> notifications = new ArrayList<>();
    private final TimeUtil utilTimer = new TimeUtil();
    private final float offset = 5;
    private final Animation animation = new Animation();
    private final Animation animation2 = new Animation();
    private final Animation animation3 = new Animation();
    public boolean didPlaySound;
    private NotificationType type;
    private String message;
    private ScaledResolution sr;
    private float x, y, x2, y2, height = 30;
    private boolean noSet;
    private float length;
    private float minXSub = 200;

    public Notification(NotificationType type, @NotNull String message, @NotNull boolean didPlaySound) {
        this.type = type;
        this.message = message;
        this.sr = new ScaledResolution(Minecraft.getMinecraft());
        this.length = (float) (5 + 5 + FontUtil.icons45.getStringWidth(getPrefix(type)) + FontUtil.nunito21.getStringWidth(message) + 5);
        if (sr.getScaledWidth() - length > sr.getScaledWidth() - minXSub) {
            length = minXSub;
        }
        this.x = sr.getScaledWidth();
        this.x2 = sr.getScaledWidth();
        this.y = sr.getScaledHeight() - 75 - ((Notification.getNotifications().size()) * (height + offset));
        this.y2 = 100 + height;
        this.noSet = true;
        this.didPlaySound = didPlaySound;
    }

    public static void updateY(ScaledResolution sr) {
        ArrayList<Notification> arrayList = new ArrayList<>(Notification.notifications);

        int i = 0;
        for (Notification n : arrayList) {
            n.animateY(n, sr, i);
            i++;
        }
    }

    public static ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public static void addNotification(Notification n) {
        notifications.add(n);
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Animation getAnimation() {
        return animation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Animation getAnimation2() {
        return animation2;
    }

    public TimeUtil getUtilTimer() {
        return utilTimer;
    }

    public float getMinXSub() {
        return minXSub;
    }

    public void setMinXSub(float minXSub) {
        this.minXSub = minXSub;
    }

    public Animation getAnimation3() {
        return animation3;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isNoSet() {
        return noSet;
    }

    public void setNoSet(boolean noSet) {
        this.noSet = noSet;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getPrefix() { //use font Icons to display the right icon
        switch (this.type) {
            case SUCCESS: {
                return "H ";
            }
            case ERROR: {
                return "I ";
            }
            case WARNING: {
                return "J ";
            }
            case INFO: {
                return "K ";
            }
        }
        return null;
    }

    public String getPrefix(NotificationType notificationType) { //use font Icons to display the right icon
        switch (notificationType) {
            case SUCCESS: {
                return "H";
            }
            case ERROR: {
                return "I";
            }
            case WARNING: {
                return "J";
            }
            case INFO: {
                return "K";
            }
        }
        return null;
    }

    public void removeNotification() {
        notifications.remove(this);
    }

    public void removeNotificationC(@NotNull Notification notification) {
        notifications.remove(notification);
    }

    public void animateY(Notification n, ScaledResolution sr, int i) {
        n.animation2.update();
        n.animation2.animate((i * (n.height + n.offset)), 0.1, false);
        n.setY((float) (sr.getScaledHeight() - 75 - n.getAnimation2().getValue()));

        if (n.animation2.isDone()) {
            n.getAnimation2().setStart(System.currentTimeMillis());
            n.getAnimation2().setValue(0);
        }
    }

    public float getX2() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return sr.getScaledWidth();
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY2() {
        return y + height;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public Color getColor() {
        switch (this.type) {
            case SUCCESS: {
                return new Color(63, 255, 66);
            }
            case ERROR: {
                return new Color(165, 0, 0);
            }
            case WARNING: {
                return new Color(255, 194, 63);
            }
            case INFO: {
                return new Color(63, 204, 255);
            }
        }
        return null;
    }
}
