package io.github.raze.modules.collection.player;

import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.collection.network.EventPacketSend;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.utilities.collection.math.TimeUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.concurrent.CopyOnWriteArrayList;

public class Blink extends BaseModule {

    public TimeUtil timer;
    private CopyOnWriteArrayList<Packet> savedPacketList = new CopyOnWriteArrayList<Packet>();

    private boolean isBlink;

    public Blink() {
        super("Blink", "Stops sending packets.", ModuleCategory.PLAYER);
        timer = new TimeUtil();

    }

    @SubscribeEvent
    private void onPacketSend(EventPacketSend event) {
        if(event.getPacket() instanceof C03PacketPlayer && isBlink) {
            savedPacketList.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if(eventMotion.getState() == BaseEvent.State.PRE){
            timer.reset();
            if(timer.elapsed(3000, false)){
                mc.thePlayer.sendQueue.addToSendQueue(
                        new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround)
                );
                timer.reset();
            }
        }
    }

    @Override
    public void onEnable() {
        isBlink = true;
    }

    @Override
    public void onDisable() {
        for(Packet <?> packet : savedPacketList){
            mc.thePlayer.sendQueue.addToSendQueue(packet);
        }
        savedPacketList.clear();
        isBlink = false;
    }

}
