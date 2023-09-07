package kevin.module.modules.player.nofalls.other;

import kevin.event.PacketEvent;
import kevin.event.UpdateEvent;
import kevin.module.modules.player.nofalls.NoFallMode;
import kevin.utils.MSTimer;
import kevin.utils.PacketUtils;
import kevin.utils.RandomUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.concurrent.CopyOnWriteArrayList;

public class BuzzNoFall extends NoFallMode {
    public static final BuzzNoFall INSTANCE = new BuzzNoFall(); // As an object
    private static final MSTimer fallTime = new MSTimer();
    private static final CopyOnWriteArrayList<Packet<?>> packets = new CopyOnWriteArrayList<>();

    public BuzzNoFall() {
        super("Buzz");
    }

    @Override
    public final void onEnable(){
        packets.clear();
    }

    @Override
    public final void onUpdate(UpdateEvent event) {
        if(!packets.isEmpty() && mc.thePlayer.onGround && fallTime.hasTimePassed(1000)){
            packets.forEach(PacketUtils.INSTANCE::sendPacketNoEvent);
            packets.clear();
            fallTime.reset();
        }
    }

    @Override
    public final void onDisable() {
        packets.forEach(PacketUtils.INSTANCE::sendPacketNoEvent);
        packets.clear();
    }

    @Override
    public final void onPacket(PacketEvent e) {
        if(e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C0FPacketConfirmTransaction || e.getPacket()
                instanceof C00PacketKeepAlive &&/* MoveUtil.isOnGround(0.42) && */mc.thePlayer.fallDistance > 4){
            if(RandomUtils.INSTANCE.getRandom().nextInt(2) != 0){
                if(e.getPacket() instanceof C03PacketPlayer && mc.thePlayer.ticksExisted % 2 == 0) ((C03PacketPlayer) e.getPacket()).onGround = true;
                packets.add(e.getPacket());
            }
            e.cancelEvent();
            fallTime.reset();
        }
    }
}
