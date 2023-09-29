package me.r.touchgrass.module.modules.player;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.events.EventUpdate;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;

/**
 * Created by r on 20/02/2021
 */
@Info(name = "AntiAFK", description = "Prevents you from getting kicked", category = Category.Player)
public class AntiAFK extends Module {

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(mc.thePlayer.onGround && this.isEnabled()) {
            mc.thePlayer.jump();
        }
    }
}
