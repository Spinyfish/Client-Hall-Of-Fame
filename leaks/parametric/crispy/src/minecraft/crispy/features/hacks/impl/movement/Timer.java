package crispy.features.hacks.impl.movement;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.rotation.LookUtils;
import net.minecraft.src.MathUtils;
import net.superblaubeere27.valuesystem.NumberValue;

@HackInfo(name = "Timer", category = Category.MOVEMENT)
public class Timer extends Hack {
    NumberValue<Float> timer = new NumberValue<Float>("Timer", 1F, 0.1F, 5F);

    @Override
    public void onDisable() {
        net.minecraft.util.Timer.timerSpeed = 1f;
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {

            net.minecraft.util.Timer.timerSpeed = timer.getObject();
        }
    }
}
