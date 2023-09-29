package me.r.touchgrass.module.modules.player;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import me.r.touchgrass.events.EventUpdate;
import me.r.touchgrass.utils.ReflectionUtil;

@Info(name = "FastPlace", description = "Removes the right-click delay", category = Category.Player, keybind = Keyboard.KEY_U)
public class FastPlace extends Module {

    public FastPlace() {}

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(this.isEnabled()) {
            try {
                ReflectionUtil.delayTimer.setInt(Minecraft.getMinecraft(), 0);
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
