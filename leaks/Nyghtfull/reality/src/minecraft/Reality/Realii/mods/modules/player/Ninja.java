package Reality.Realii.mods.modules.player;

import net.minecraft.item.ItemBow;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.math.MathUtil;

public class Ninja extends Module {
    public Ninja() {
        super("Ninja", ModuleType.Misc);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.keyBindSneak.pressed = false;
        mc.gameSettings.keyBindUseItem.pressed = false;

    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        mc.gameSettings.keyBindSneak.pressed = true;
        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) {
            mc.gameSettings.keyBindUseItem.pressed = true;
            e.setPitch(190);
            mc.thePlayer.setSpeed(MathUtil.getBaseMovementSpeed());
        }

    }
}
