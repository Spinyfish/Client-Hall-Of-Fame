package crispy.features.hacks.impl.combat;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;

@HackInfo(name = "WTap", category = Category.COMBAT)
public class WTap extends Hack {
    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            if (event.isPre() && mc.thePlayer.isSprinting() && mc.thePlayer.swingProgress > 0.6) {
                mc.thePlayer.setSprinting(false);
            }
        }
    }
}
