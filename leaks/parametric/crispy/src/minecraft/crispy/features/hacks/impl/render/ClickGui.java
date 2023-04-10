package crispy.features.hacks.impl.render;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.animation.animations.Direction;
import crispy.util.animation.animations.impl.SmoothStepAnimation;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import org.lwjgl.input.Keyboard;

@HackInfo(name = "ClickGui", category = Category.RENDER, key = Keyboard.KEY_RSHIFT)
public class ClickGui extends Hack {
    public static ModeValue modeValue = new ModeValue("Click Mode", "Crispy v2", "Astolfo", "Rise", "Crispy v2");
    public static ModeValue theme = new ModeValue("Theme", "Blue", () -> modeValue.getMode().equalsIgnoreCase("Rise"),"Blue", "Purple", "Red");
    public static ModeValue novoThemes = new ModeValue("Color Mode", "New", () -> modeValue.getMode().equalsIgnoreCase("Crispy v2"),"New", "Old");
    public static BooleanValue dark = new BooleanValue("Dark", true, () -> modeValue.getMode().equalsIgnoreCase("Rise"));
    public static BooleanValue blur = new BooleanValue("Blur", false, () -> modeValue.getMode().equalsIgnoreCase("Crispy v2"));
    @Override
    public void onEnable() {
        toggle();
        switch (modeValue.getMode()) {
            case "Dortware":
            case "Astolfo": {
                mc().displayGuiScreen(Crispy.INSTANCE.getClickGui());
                break;
            }
            case "Rise": {
                mc().displayGuiScreen(Crispy.INSTANCE.getClick());
                break;
            }
            case "Crispy v2": {
                mc.displayGuiScreen(Crispy.INSTANCE.getNovoGui());
                break;
            }
        }
        super.onEnable();
    }

    @Override
    public void onEvent(Event e) {
        //Nothing here don't mind me
    }
}
