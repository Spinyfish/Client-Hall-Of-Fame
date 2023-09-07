package kevin.hackChecks;

import kevin.hackChecks.checks.combat.*;
import kevin.hackChecks.checks.move.*;
import kevin.hud.element.elements.ConnectNotificationType;
import kevin.hud.element.elements.Notification;
import kevin.main.KevinClient;
import kevin.module.modules.misc.HackDetector;
import kevin.utils.ChatUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

public class CheckManager {
    private static final Class<?>[] checksClz = {
            AutoBlockCheck.class,
            KillAuraCheck.class,

            FlightCheck.class,
            NoSlowCheck.class
//            SpeedCheck.class
    };
    private final LinkedList<Check> checks = new LinkedList<>();
    private double totalVL = 0;
    private short addedTicks = 0;
    public CheckManager(EntityOtherPlayerMP target) {
        for (Class<?> clz : checksClz) {
            try {
                checks.add((Check) clz.getConstructor(EntityOtherPlayerMP.class).newInstance(target));
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                KevinClient.hud.addNotification(new Notification("Failed to initialize %s check for %s", "HackDetector", ConnectNotificationType.Error));
            }
        }
    }

    public void livingUpdate() {
        for (Check check : checks) {
            try {
                check.onLivingUpdate();
                if (check.wasFailed()) {
                    if (HackDetector.shouldAlert()) ChatUtils.INSTANCE.message(String.format("§l§7[§l§9HackDetector§l§7]§r §4%s§7 maybe using §c%s§7 hack§8: §7%s", check.handlePlayer.getName(), check.name, check.description()));
                    totalVL += check.getPoint();
                    if (HackDetector.catchPlayer(check.handlePlayer.getName(), check.reportName(), totalVL)) {
                        totalVL = -5;
                    }
                    addedTicks = 40;
                    check.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // reduce 0.1 per second
        if (--addedTicks <= 0) totalVL -= totalVL > 0 ? 0.005 : 0;
    }

    public void positionUpdate(double x, double y, double z) {
        for (Check check : checks) {
            try {
                check.positionUpdate(x, y, z);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
