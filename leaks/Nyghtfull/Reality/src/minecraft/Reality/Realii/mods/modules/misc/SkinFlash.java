
package Reality.Realii.mods.modules.misc;

import net.minecraft.entity.player.EnumPlayerModelParts;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventTick;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
public class SkinFlash
extends Module {
    public SkinFlash(){
        super("SkinFlash", ModuleType.Misc);
    }
    @Override
    public void onDisable() {
        EnumPlayerModelParts[] parts;
        if (this.mc.thePlayer != null && (parts = EnumPlayerModelParts.values()) != null) {
            EnumPlayerModelParts[] arrayOfEnumPlayerModelParts1 = parts;
            int j = arrayOfEnumPlayerModelParts1.length;
            int i = 0;
            while (i < j) {
                EnumPlayerModelParts part = arrayOfEnumPlayerModelParts1[i];
                this.mc.gameSettings.setModelPartEnabled(part, true);
                ++i;
            }
        }
    }

    @EventHandler
    private void onTick(EventTick e) {
        EnumPlayerModelParts[] parts;
        if (this.mc.thePlayer != null && (parts = EnumPlayerModelParts.values()) != null) {
            EnumPlayerModelParts[] arrayOfEnumPlayerModelParts1 = parts;
            int j = arrayOfEnumPlayerModelParts1.length;
            int i = 0;
            while (i < j) {
                EnumPlayerModelParts part = arrayOfEnumPlayerModelParts1[i];
                boolean newState = this.isEnabled() ? random.nextBoolean() : true;
                this.mc.gameSettings.setModelPartEnabled(part, newState);
                ++i;
            }
        }
    }
}

