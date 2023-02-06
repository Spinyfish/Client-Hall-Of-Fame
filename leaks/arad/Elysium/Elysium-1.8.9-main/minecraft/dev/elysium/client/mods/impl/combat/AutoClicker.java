package dev.elysium.client.mods.impl.combat;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventUpdate;
import dev.elysium.client.utils.Timer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class AutoClicker extends Mod {
    public NumberSetting rmbcps = new NumberSetting("RMB CPS", 0,20,12,1,this);
    public NumberSetting lmbcps = new NumberSetting("LMB CPS", 0, 20, 12, 1, this);
    public BooleanSetting trigger = new BooleanSetting("Trigger", false, this);
    public Timer rtimer = new Timer();
    public Timer ltimer = new Timer();
    public long[] timerValue = new long[] {1000, 1000};

    public AutoClicker() {
        super("AutoClicker","Clicks for you", Category.COMBAT);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        ItemStack item = mc.thePlayer.inventory.getCurrentItem();
        if (lmbcps.getValue() > 0 && (mc.gameSettings.keyBindAttack.pressed || mc.objectMouseOver.entityHit != null && Elysium.getInstance().getTargets().contains(mc.objectMouseOver.entityHit) && trigger.isEnabled()) && ltimer.hasTimeElapsed(timerValue[0], true))
        {
            mc.leftClickCounter = 0;
            mc.clickMouse();
            resetValues();
        } else if(rmbcps.getValue() > 0 && mc.gameSettings.keyBindUseItem.pressed && rtimer.hasTimeElapsed(timerValue[1], true) && item != null && !(item.getItem() instanceof ItemSword))
        {
            mc.rightClickMouse();
            resetValues();
        }
    }

    public void resetValues() {
        Float laps = (float) lmbcps.getValue();
        Float raps = (float) rmbcps.getValue();

        timerValue[0] = getRandomValue(laps);
        timerValue[1] = getRandomValue(raps);
    }

    public long getRandomValue(Float aps) {
        long value = 1000;

        if(Math.random() > 0.5){
            value = Math.round((1000-(Math.random()*700)) / aps);
        } else {
            value = Math.round((1000+(Math.random()*700)) / aps);
        }

        return value;
    }
}
