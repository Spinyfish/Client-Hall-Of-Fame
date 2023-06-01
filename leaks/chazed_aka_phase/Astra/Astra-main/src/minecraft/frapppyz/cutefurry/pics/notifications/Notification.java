package frapppyz.cutefurry.pics.notifications;

import net.minecraft.client.Minecraft;

public class Notification {
    public long finishTime;
    public String title, message, message2;
    public Type type;

    public void postNotification(String title, String message, String msgline2, Type type, long duration){
        finishTime = System.currentTimeMillis() + duration;
        this.message = message;
        this.message2 = msgline2;
        this.title = title;
        this.type = type;
    }

    public void postNotification(String title, String message, Type type, long duration){
        finishTime = System.currentTimeMillis() + duration;
        this.message = message;
        this.message2 = "";
        this.title = title;
        this.type = type;
    }
}
