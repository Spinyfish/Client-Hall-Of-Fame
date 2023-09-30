package me.r.touchgrass.module.modules.render;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import net.minecraft.client.Minecraft;
import me.r.touchgrass.events.EventPreMotion;

@Info(name = "NoBob", description = "Removes the bobbing animation", category = Category.Render)
public class NoBob extends Module {

    public NoBob() {}

    @EventTarget
    public void onRender(EventPreMotion e) {
        Minecraft.getMinecraft().thePlayer.distanceWalkedModified = 0f;
    }
}
