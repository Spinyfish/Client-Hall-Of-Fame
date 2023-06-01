package frapppyz.cutefurry.pics;

import com.google.common.eventbus.EventBus;
import frapppyz.cutefurry.pics.modules.ModManager;
import frapppyz.cutefurry.pics.notifications.Notification;
import frapppyz.cutefurry.pics.util.Logger;

public class Wrapper {
    private static Astra astra = new Astra();
    private static Logger logger = new Logger();
    private static ModManager modManager = new ModManager();
    private static Notification notification = new Notification();

    public static Astra getAstra(){
        return astra;
    }
    public static Logger getLogger(){
        return logger;
    }
    public static ModManager getModManager(){
        return modManager;
    }
    public static Notification getNotifications(){ return notification; }

}
