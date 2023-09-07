package kevin.hackChecks;

import kevin.utils.ChatUtils;
import kevin.utils.MinecraftInstance;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class Check extends MinecraftInstance {
    public static boolean debug = false;
    protected EntityOtherPlayerMP handlePlayer = null;
    protected String name = "NONE";
    protected boolean enabled = true;
    protected double violationLevel = 0;
    protected double checkViolationLevel = 20;
    protected double vlStep = 5;

    public Check(EntityOtherPlayerMP playerMP) {
        handlePlayer = playerMP;
    }

    public void positionUpdate(double x, double y, double z) {}

    public void onLivingUpdate() {}

    public boolean isEnabled() {
        return enabled;
    }

    public void flag(String verbose, double vl) {
        violationLevel += vl;
        if (debug) ChatUtils.INSTANCE.message(String.format("§l§7[§l§9HackDetector§l§7]§r §c%s§3 failed§2 %s §r§7(x§4%s§7) %s", handlePlayer.getName(), name, (int) violationLevel, verbose));
    }

    public void reward() {
        reward(0.1);
    }

    public void reward(double rewardVL) {
        violationLevel = Math.max(0, violationLevel - rewardVL);
    }

    public void shrinkVL(double t) {
        violationLevel *= t;
    }

    public int getCalculationVL() {
        return (int) (violationLevel / vlStep);
    }

    public double getVL() {
        return violationLevel;
    }

    public boolean wasFailed() {
        return violationLevel > checkViolationLevel;
    }

    public String description() {
        return "cheating";
    }

    public void reset() {
        violationLevel = 0;
    }

    public double getPoint() {
        return 5.0;
    }

    public String reportName() {
        return name;
    }
}
