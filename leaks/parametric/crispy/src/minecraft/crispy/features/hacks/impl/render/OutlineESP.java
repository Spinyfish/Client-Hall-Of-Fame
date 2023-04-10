package crispy.features.hacks.impl.render;

import crispy.features.event.Event;
import crispy.features.event.impl.render.Event3D;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;

@HackInfo(name = "JelloESP", category = Category.RENDER)
public class OutlineESP extends Hack {
    @Override
    public void onEvent(Event e) {
        if(e instanceof Event3D) {

        }
    }
}
