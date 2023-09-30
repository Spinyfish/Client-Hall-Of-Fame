package dev.elysium.client.mods.impl.combat;

import java.util.List;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.events.EventUpdate;
import dev.elysium.client.utils.Timer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class WTap extends Mod{

    public NumberSetting delay = new NumberSetting("Delay", 100, 2000, 500, 10, this);
    public NumberSetting held = new NumberSetting("Held", 50, 250, 100, 10, this);
    public NumberSetting range = new NumberSetting("Range", 1, 8, 3.5, 0.1, this);
    private Timer delayt = new Timer();
    private Timer holdt = new Timer();

    public static boolean stopSprint = false;
    int step = 0;
    public WTap() {
        super("Wtap", "Holds w for u", Category.COMBAT);

    }
    protected boolean isTargetValid(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (!entity.isEntityAlive()) {
            return false;
        }
        if (!mc.thePlayer.canEntityBeSeen(entity)) {
            return false;
        };
        if (mc.thePlayer.getDistanceToEntity(entity) > range.getValue()) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            //EntityPlayer player = (EntityPlayer) entity;
        }
        return true;
    }
    public void setKeyPressed(KeyBinding bind,boolean pressed) {
        KeyBinding.setKeyBindState(bind.getKeyCode(), pressed);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        try {
            if(mc.theWorld == null || mc.thePlayer == null || mc.currentScreen != null || !mc.thePlayer.isEntityAlive())return;
            Entity ens = null;
            List<Entity> entites = mc.theWorld.getLoadedEntityList();
            for (Entity e1 : entites) {
                if (e1 != mc.thePlayer && this.isTargetValid(e1)) {
                    if (mc.thePlayer.isSwingInProgress) {
                        ens = e1;
                    }
                }
            }
            //System.out.println(ens);

            if (ens != null && mc.thePlayer.isSprinting() && delayt.hasTimeElapsed((long) delay.getValue(), true)) {
                setKeyPressed(mc.gameSettings.keyBindForward,false);
                stopSprint = true;
                holdt.reset();

            }


            if (holdt.hasTimeElapsed((long) held.getValue(), true)) {
                setKeyPressed(mc.gameSettings.keyBindForward,true);
                stopSprint = false;
            }
        } catch (Exception e2) {
            // TODO: handle exception
        }
    }

}
