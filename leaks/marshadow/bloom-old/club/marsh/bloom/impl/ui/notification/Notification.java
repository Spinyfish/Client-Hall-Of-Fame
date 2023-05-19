package club.marsh.bloom.impl.ui.notification;

import club.marsh.bloom.impl.utils.render.Translate;
import net.minecraft.client.gui.ScaledResolution;

public class Notification {
    public Translate translate = new Translate(ScaledResolution.getScaledWidth()+30,ScaledResolution.getScaledHeight());
    public Translate translate2 = new Translate(0,0);
    String name,desc;
    long time,publishedtime;
    NotificationType type;
    float x = -10, deltax = 0, deltay, y = -100,translationfactor = 20;
    public Notification(String name, String desc, long time,NotificationType type) {
        this.time = time;
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.publishedtime = System.currentTimeMillis();
    }
}
