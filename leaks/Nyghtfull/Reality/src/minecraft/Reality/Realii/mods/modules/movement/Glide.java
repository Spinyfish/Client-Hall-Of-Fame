package Reality.Realii.mods.modules.movement;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.PlayerUtils;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.MathUtil;

public class Glide extends Module{

	public Glide(){
		super("Glide", ModuleType.Movement);
	}

    @Override
    public void onEnable() {
        super.onEnable();

    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
    	mc.thePlayer.motionY = 0.0;
        if (mc.gameSettings.keyBindJump.isPressed()) {
            mc.thePlayer.motionY = 5.000;
        } else if (mc.gameSettings.keyBindSneak.isPressed()) {
            mc.thePlayer.motionY = -5.000;
        }
    }

    @EventHandler
    public void onMove(EventMove e) {
        if (PlayerUtils.isMoving()) {
        mc.thePlayer.motionY = 0.0;
            mc.thePlayer.setMoveSpeed(e, 2);
        } else {
            mc.thePlayer.setMoveSpeed(e, 0);
        }
    }
    
   
    
    

}
