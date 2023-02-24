package me.kansio.client.modules.impl.visuals;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.ModeValue;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@ModuleData(
        name = "Time Changer",
        category = ModuleCategory.VISUALS,
        description = "Changes the world time"
)
public class TimeChanger extends Module {

    private ModeValue time = new ModeValue("Mode", this, "Day", "Noon", "Night", "Mid Night");

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        switch (time.getValue()){
            case "Day":
                mc.theWorld.setWorldTime(1000);
                break;
            case "Noon":
                mc.theWorld.setWorldTime(13200);
                break;
            case "Night":
                mc.theWorld.setWorldTime(13000);
                break;
            case "Mid Night":
                mc.theWorld.setWorldTime(18000);
                break;
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        }
    }

}
