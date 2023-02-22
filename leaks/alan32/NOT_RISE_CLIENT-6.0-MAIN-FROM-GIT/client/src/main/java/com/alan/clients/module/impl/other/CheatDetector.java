package com.alan.clients.module.impl.other;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.client.entity.EntityOtherPlayerMP;

@Rise
@ModuleInfo(name = "module.other.cheatdetector.name", description = "module.other.cheatdetector.description", category = Category.OTHER)
public final class CheatDetector extends Module {

    public ModeValue alertType = new ModeValue("Alert Type", this)
            .add(new SubMode("ClientSide"))
            .add(new SubMode("ServerSide"))
            .setDefault("ClientSide");

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        Client.INSTANCE.getCheatDetector().incrementTick();
    };

    @Override
    protected void onDisable() {
        Client.INSTANCE.getCheatDetector().playerMap.clear();
    }

    @Override
    protected void onEnable() {
        Client.INSTANCE.getCheatDetector().playerMap.clear();
        mc.theWorld.playerEntities.forEach(entityPlayer -> {
            if (entityPlayer != mc.thePlayer) {
                Client.INSTANCE.getCheatDetector().getRegistrationListener().handleSpawn((EntityOtherPlayerMP) entityPlayer);
            }
        });
    }

    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        Client.INSTANCE.getCheatDetector().playerMap.values().forEach(playerData -> playerData.handle(event.getPacket()));
    };
}
