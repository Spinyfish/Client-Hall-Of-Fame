package crispy.features.hacks.impl.render;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;

@HackInfo(name = "BlockHit", category = Category.MISC)
public class BlockHit extends Hack {
    public static BooleanValue germanStyle = new BooleanValue("German Style", false);
    public static BooleanValue booleanValue = new BooleanValue("Go Down On Hit", false);
    public NumberValue<Double> scale = new NumberValue<Double>("Scale", 1D, 0.1D, 1D);
    public ModeValue modes = new ModeValue("Animation", "Slide", "Slide", "SpinnyBoi", "1.7", "Boop", "Slide2");
    public static NumberValue<Integer> handspeed = new NumberValue<Integer>("Hand Speed", 1, 1, 12);

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            setDisplayName(getName() + " \2477" + modes.getMode());
        }
    }
}
