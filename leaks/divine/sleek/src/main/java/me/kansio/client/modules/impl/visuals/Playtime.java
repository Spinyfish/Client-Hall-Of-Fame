package me.kansio.client.modules.impl.visuals;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.RenderOverlayEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.network.handshake.client.C00Handshake;

@ModuleData(
        name = "Playtime",
        category = ModuleCategory.VISUALS,
        description = "Shows how long you've been on for"
)
public class Playtime extends Module {

    public long currentTime;

    @Override
    public void onEnable() {
        currentTime = System.currentTimeMillis();
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C00Handshake) {
            currentTime = System.currentTimeMillis();
        }
    }

    @Subscribe
    public void onRender(RenderOverlayEvent event) {
        if (mc.currentScreen != null)
            return;

        long estimatedTime = System.currentTimeMillis() - currentTime;

        //totally not skidded from stackoverflow
        int seconds = (int) (estimatedTime / 1000) % 60 ;
        int minutes = (int) ((estimatedTime / (1000*60)) % 60);
        int hours   = (int) ((estimatedTime / (1000*60*60)) % 24);

        //draw on hud
        mc.fontRendererObj.drawStringWithShadow(hours + "h " + minutes + "m, " + seconds + "s", RenderUtils.getResolution().getScaledWidth() / 2 - 20, 20, -1);
    }
}
