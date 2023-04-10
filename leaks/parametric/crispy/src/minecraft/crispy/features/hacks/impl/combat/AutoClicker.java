package crispy.features.hacks.impl.combat;

import crispy.Crispy;
import crispy.features.commands.impl.ClickPatternCommand;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.time.TimeHelper;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Mouse;

@HackInfo(name = "AutoClicker", category = Category.COMBAT)
public class AutoClicker extends Hack {
    private int index;
    private final TimeHelper timeHelper = new TimeHelper();

    @Override
    public void onEnable() {
        index = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        index = 0;
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            if (Mouse.isButtonDown(0)) {
                ClickPatternCommand pattern = Crispy.INSTANCE.getCommandManager().getCommand(ClickPatternCommand.class);
                if (index > pattern.delays.size() - 1) {
                    index = 0;
                }
                long delay = pattern.delays.get(index);
                if (timeHelper.hasReached(delay)) {
                    index++;
                    timeHelper.reset();
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindAttack.getKeyCode(), false);
                    mc.clickMouse();
                }
            }
        }
    }
}
