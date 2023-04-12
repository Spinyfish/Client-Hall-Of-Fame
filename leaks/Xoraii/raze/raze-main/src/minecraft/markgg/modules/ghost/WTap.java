package markgg.modules.ghost;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.settings.NumberSetting;
import net.minecraft.entity.Entity;

public class WTap extends Module {

	public NumberSetting dist = new NumberSetting("Distance", this, 3.5, 1, 6, 0.5);
	
	public WTap() {
		super("WTap", "Taps W to deal more knockback", 0, Category.GHOST);
	}
	
	public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            for (Object o : mc.theWorld.loadedEntityList) {
                if (!(o instanceof Entity)) {
                    continue;
                }
                Entity entity = (Entity)o;
                if (entity == mc.thePlayer) {
                    continue;
                }
                if (mc.thePlayer.getDistanceToEntity(entity) > dist.getValue() || !mc.thePlayer.isSprinting()) {
                    continue;
                }
                mc.thePlayer.setSprinting(false);
            }
        }
    }
	
}
