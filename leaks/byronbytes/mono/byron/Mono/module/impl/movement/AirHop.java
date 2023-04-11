package byron.Mono.module.impl.movement;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.TimeUtil;
import com.google.common.eventbus.Subscribe;

@ModuleInterface(name = "AirHop", description = "Automatically jumps in the air.", category = Category.Movement)
public class AirHop extends Module
{
    @Override
    public void onEnable ()
    {
        super.onEnable();
        mc.thePlayer.jump();

    }

    @Subscribe
    public void onUpdate (EventUpdate e)
    {
        mc.gameSettings.keyBindJump.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);

        if (mc.thePlayer.fallDistance > 1)
        {
            mc.thePlayer.onGround = true;
            mc.thePlayer.fallDistance = 0;

        }

        else
        {
            mc.thePlayer.onGround = false;

        }

    }

    @Override
    public void onDisable ()
    {
        super.onDisable();
        mc.thePlayer.onGround = false;
        mc.gameSettings.keyBindJump.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);

    }

}
