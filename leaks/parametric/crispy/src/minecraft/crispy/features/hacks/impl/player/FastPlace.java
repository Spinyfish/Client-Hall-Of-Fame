package crispy.features.hacks.impl.player;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.client.Minecraft;

@HackInfo(name = "FastPlace", category = Category.PLAYER)
public class FastPlace extends Hack {
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            Minecraft.getMinecraft().rightClickDelayTimer = 0;
        }
    }
}
