package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

@ModuleData(
        name = "Auto Settings",
        category = ModuleCategory.PLAYER,
        description = "Attempts to set up good settings for the client"
)
public class AutoSettings extends Module {

    private boolean isUsingC0F;
    private long currTime = System.currentTimeMillis();
    private long lastC0FDelay, C0FDelay;

    private BooleanValue debugC0F = new BooleanValue("Test C0F", this, true);

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {

        }
    }

}
