package club.pulsive.impl.module.impl.movement;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.player.Scaffold;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Timer;

import java.util.ArrayList;

@ModuleInfo(name = "AntiVoid", description = "Anti Void module", category = Category.MOVEMENT)

public class AntiVoid extends Module {
    private TimerUtil timer = new TimerUtil();
    public static ArrayList<C03PacketPlayer> packets = new ArrayList<>();
    @Override
    public void onEnable() {
        timer.reset();
        super.onEnable();
    }

    @Override
    public void init() {
        this.setSuffix("Anti-Stall");
        super.init();
    }

    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        switch(event.getEventState()) {
            case SENDING:
                if(event.getPacket() instanceof C03PacketPlayer && Pulsive.INSTANCE.getModuleManager().getModule(Scaffold.class).isToggled()) {
                    C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();
                    if (isInVoid() && !timer.hasElapsed(1500)) {
                        packets.add(packet);
                        event.setCancelled(true);
                        if (timer.hasElapsed(1200)) {
                            packets.clear();
                        }
                    }
                }
                if (!isInVoid() || Pulsive.INSTANCE.getModuleManager().getModule(Flight.class).isToggled()) {
                    for (C03PacketPlayer p : packets) {
                        PacketUtil.sendPacketNoEvent(p);
                    }
                    packets.clear();
                    timer.reset();
                }
                break;
        }
    };
    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
    
    };
    public static boolean isOnGround(double height) {
        if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInVoid() {
        for (int i = 0; i <= 128; i++) {
            if (isOnGround(i)) {
                return false;
            }
        }
        return true;
    }
}
