package me.jinthium.straight.impl.modules.movement;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.Scaffold;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.MultiBoolSetting;
import org.lwjglx.input.Keyboard;

public class Sprint extends Module {

    private final BooleanSetting omni = new BooleanSetting("Omni", false);

    public Sprint(){
        super("Sprint", Keyboard.KEY_V, Category.MOVEMENT);
        addSettings(omni);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setSprinting(false);
        mc.gameSettings.keyBindSprint.pressed = false;
        super.onDisable();
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        Scaffold scaffold = Client.INSTANCE.getModuleManager().getModule(Scaffold.class);
        if (scaffold.isEnabled() && scaffold.noSprintProperty.isEnabled()) {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
            return;
        }

        if (omni.isEnabled()) {
            mc.thePlayer.setSprinting(true);
        } else {
            if (mc.thePlayer.isUsingItem()) {
                if (mc.thePlayer.moveForward > 0 && (Client.INSTANCE.getModuleManager().getModule(NoSlowDown.class).isEnabled() ||
                        !mc.thePlayer.isUsingItem()) && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally
                        && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {

                    mc.thePlayer.setSprinting(true);
                }
            } else {
                mc.gameSettings.keyBindSprint.pressed = true;
            }
        }
    };
}
