/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 04.09.22, 20:43
 */

/**
 * @project Myth
 * @author CodeMan
 * @at 21.08.22, 13:49
 */
package dev.myth.features.player;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@Feature.Info(
        name = "Blink",
        description = "Makes it look like you are lagging",
        category = Feature.Category.PLAYER
)
public class BlinkFeature extends Feature {

    public final NumberSetting maxTicks = new NumberSetting("Ticks", 0, 0, 100, 1).addValueAlias(0, "Unlimited");
    public final BooleanSetting debug = new BooleanSetting("Debug", false);

    private final ArrayList<Packet<?>> packets = new ArrayList<>();
    private int ticks;
    private Vec3 lastPos;

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if(getPlayer() == null) {
            toggle();
            return;
        }
        if (event.getState() == EventState.SENDING) {
            if(event.getPacket().getClass().getSimpleName().startsWith("S")) return;
            if(event.getPacket().getClass().getSimpleName().startsWith("C00") || event.getPacket().getClass().getSimpleName().startsWith("C01")) return;

            packets.add(event.getPacket());
            event.setCancelled(true);
        } else {
//            if(event.getPacket() instanceof S08PacketPlayerPosLook) {
//                packets.clear();
//            } else if (event.getPacket() instanceof S18PacketEntityTeleport) {
//                S18PacketEntityTeleport packet = event.getPacket();
//                if(packet.getEntityId() == getPlayer().getEntityId()) {
//                    packets.clear();
//                }
//            }
        }
    };

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() != EventState.PRE || maxTicks.getValue() == 0) return;

        if (ticks > maxTicks.getValue() + MathUtil.getRandomValue(0, 5)) {
            int amount = packets.size();
            while (!packets.isEmpty()) {
                sendPacketUnlogged(packets.remove(0));
            }
            if(debug.getValue()) doLog("Blinked " + amount + " packets");
            ticks = 0;
            lastPos = getPlayer().getPositionVector();
        }

        ticks++;
    };

    @Override
    public void onDisable() {
        super.onDisable();

        if (getPlayer() == null) return;

        int amount = packets.size();
        while (!packets.isEmpty()) {
            sendPacketUnlogged(packets.remove(0));
        }
        if(debug.getValue()) doLog("Blinked " + amount + " packets");

        GL11.glTranslated(-MC.getRenderManager().renderPosX, -MC.getRenderManager().renderPosY, -MC.getRenderManager().renderPosZ);
        GL11.glTranslated(MC.getRenderManager().renderPosX, MC.getRenderManager().renderPosY, MC.getRenderManager().renderPosZ);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if(getPlayer() == null) {
            toggle();
            return;
        }
        lastPos = getPlayer().getPositionVector();
        packets.clear();
    }
}
