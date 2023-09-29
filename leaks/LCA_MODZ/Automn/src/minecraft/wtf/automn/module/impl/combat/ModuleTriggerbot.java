package wtf.automn.module.impl.combat;


import net.minecraft.util.MathHelper;
import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;
import wtf.automn.module.settings.SettingNumber;
import wtf.automn.utils.math.TimeUtil;

import java.util.Random;

@ModuleInfo(name = "triggerbot", displayName = "Triggerbot", category = Category.COMBAT)
public class ModuleTriggerbot extends Module {
    TimeUtil timeUtil = new TimeUtil();

    public final SettingNumber minCPS = new SettingNumber("minCPS", 10D, 1D, 20D, "MinCPS", this, "set the MinCPS");
    public final SettingNumber maxCPS = new SettingNumber("maxCPS", 18D, 2D, 20D, "MaxCPS", this, "set the MaxCPS");

    @Override
    protected void onDisable() {
    }

    @Override
    protected void onEnable() {

    }

    @EventHandler
    public void onUpdate(final EventPlayerUpdate e) {
        final float CPS = (float) MathHelper.getRandomDoubleInRange(new Random(), minCPS.getValue(), maxCPS.getValue());
        if (this.MC.objectMouseOver.entityHit != null && this.timeUtil.hasReached((long) (1000 / CPS)))
            this.MC.clickMouse();
    }
}
