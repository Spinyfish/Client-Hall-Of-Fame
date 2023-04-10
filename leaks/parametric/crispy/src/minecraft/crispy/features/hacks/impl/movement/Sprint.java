package crispy.features.hacks.impl.movement;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.client.Minecraft;
import net.superblaubeere27.valuesystem.BooleanValue;

@HackInfo(name = "Sprint", category = Category.MOVEMENT)
public class Sprint extends Hack {
    BooleanValue Sprint = new BooleanValue("Omni", true);

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            EventUpdate eventUpdate = (EventUpdate) e;
            if(Sprint.getObject()) {
                mc().thePlayer.setSprinting(mc().gameSettings.keyBindForward.pressed || mc().gameSettings.keyBindBack.pressed
                        || mc().gameSettings.keyBindRight.pressed || mc().gameSettings.keyBindLeft.pressed);
            } else {
                mc.gameSettings.keyBindSprint.pressed = true;
            }
        }
    }
    @Override
    public void onDisable() {

        if (Minecraft.theWorld != null) {
            mc().thePlayer.setSprinting(false);
        }
        super.onDisable();
    }
}
