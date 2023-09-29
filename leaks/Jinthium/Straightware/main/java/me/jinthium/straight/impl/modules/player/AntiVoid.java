package me.jinthium.straight.impl.modules.player;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.TeleportEvent;
import me.jinthium.straight.impl.event.movement.WorldEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.movement.Flight;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AntiVoid extends Module {
    private final NumberSetting distance = new NumberSetting("Distance", 5, 0, 10, 1);

    private final ConcurrentLinkedQueue<C03PacketPlayer> packets = new ConcurrentLinkedQueue<>();
    private Vec3 position;

    private Scaffold scaffold;
    private Flight fly;

    public AntiVoid(){
        super("AntiVoid", Category.MISC);
        this.addSettings(distance);
    }

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if(!(event.getPacketState() == PacketEvent.PacketState.SENDING))
            return;

        if (scaffold == null) {
            scaffold = Client.INSTANCE.getModuleManager().getModule(Scaffold.class);
        }

        if (fly == null) {
            fly = Client.INSTANCE.getModuleManager().getModule(Flight.class);
        }

        if (scaffold.isEnabled() || fly.isEnabled()) {
            return;
        }

        final Packet<?> p = event.getPacket();

        if (p instanceof C03PacketPlayer wrapper) {

            if (!PlayerUtil.isBlockUnder()) {
                packets.add(wrapper);
                event.setCancelled(true);

                if (position != null && mc.thePlayer.fallDistance > distance.getValue().floatValue()) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(position.xCoord, position.yCoord + 0.1, position.zCoord, false));
                }
            } else {
                if (mc.thePlayer.onGround) {
                    position = new Vec3(wrapper.x, wrapper.y, wrapper.z);
                }

                if (!packets.isEmpty()) {
                    packets.forEach(PacketUtil::sendPacketNoEvent);
                    packets.clear();
                }
            }
        }
    };


    @Callback
    final EventCallback<TeleportEvent> teleportEventEventCallback = event -> {
        if (packets.size() > 1) {
            packets.clear();
        }
    };

    @Callback
    final EventCallback<WorldEvent> worldEventEventCallback = event -> {
        packets.clear();
    };
}
