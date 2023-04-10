package crispy.features.hacks.impl.combat;

import crispy.features.event.Event;
import crispy.features.event.impl.render.EventRenderGui;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;

@HackInfo(name = "OldHitting", category = Category.COMBAT)
public class OldHitting extends Hack {
    @Override
    public void onEvent(Event e) {
        if (e instanceof EventRenderGui) {

        }
    }
}
