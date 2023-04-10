package crispy.util.fbi.check;

import crispy.Crispy;
import crispy.features.hacks.impl.misc.HackerDetector;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import crispy.util.fbi.data.Data;
import crispy.util.fbi.target.TargetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;


public abstract class Check {

    private final String name;
    private final String type;
    private long lastAlert;

    public Check() {
        CheckInfo checkInfo = getCheckInfo();
        this.name = checkInfo.name();
        this.type = checkInfo.type();
    }


    public void alert(Data data) {
        HackerDetector fbiAntiCheat = Crispy.INSTANCE.getHackManager().getHack(HackerDetector.class);
        if (!TargetManager.INSTANCE.getTargets().contains(data.getPlayer())) {
            TargetManager.INSTANCE.getTargets().add(data.getPlayer());
        }
        switch (fbiAntiCheat.mode.getMode()) {
            case "Notification": {
                if (System.currentTimeMillis() - lastAlert > 1000) {
                    NotificationPublisher.queue("[FBI DETECTION]", data.getPlayer().getCommandSenderName() + " \247ahas failed " + name, NotificationType.WARNING, 5000);
                    lastAlert = System.currentTimeMillis();
                }
                break;
            }
            case "Text": {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\247c[\2474FBI\247c] \2478» \247c" + data.getPlayer().getCommandSenderName() + " \247ahas failed \247c" + name + " \2477[\247c" + type + "\2477]"));
                break;
            }
        }


    }

    public CheckInfo getCheckInfo() {
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            return this.getClass().getAnnotation(CheckInfo.class);
        } else {
            System.err.println("CheckInfo was not set from class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    public abstract void run(Data data);
}
