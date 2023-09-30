package dev.echo.module.impl.movement;

import dev.echo.Echo;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.StrafeEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.impl.combat.ExtraKB;
import dev.echo.module.impl.player.NoSlow;
import dev.echo.module.settings.impl.BooleanSetting;

public class Sprint extends Module {

    private final BooleanSetting omniSprint = new BooleanSetting("Omni Sprint", false);

    public Sprint() {
        super("Sprint", Category.MOVEMENT, "Sprints automatically");
        this.addSettings(omniSprint);
    }

    @Link
    public Listener<StrafeEvent> moveUpdateEventListener = e -> {
        if ((Echo.INSTANCE.getModuleCollection().get(Scaffold.class).isEnabled() && (!Scaffold.sprint.isEnabled() || Scaffold.isDownwards())) || (Echo.INSTANCE.getModuleCollection().get(ExtraKB.class).isEnabled() && (ExtraKB.attacked == true))) {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
            return;
        }
        if (omniSprint.isEnabled()) {
            mc.thePlayer.setSprinting(true);
        } else {
            if (mc.thePlayer.isUsingItem()) {
                if (mc.thePlayer.moveForward > 0 && (Echo.INSTANCE.isEnabled(NoSlow.class) || !mc.thePlayer.isUsingItem()) && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
                    mc.thePlayer.setSprinting(true);
                }
            } else {
                mc.gameSettings.keyBindSprint.pressed = true;
            }
        }
    };

    @Override
    public void onDisable() {
        mc.thePlayer.setSprinting(false);
        mc.gameSettings.keyBindSprint.pressed = false;
        super.onDisable();
    }

}
