package dev.echo.module.impl.player;

import dev.echo.Echo;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.impl.movement.Speed;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.player.AlwaysUtil;
import dev.echo.utils.server.PacketUtils;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

public class AntiVoid extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Watchdog", "BlocksMC");
    private final NumberSetting fallDist = new NumberSetting("Fall Distance", 3, 20, 1, 0.5);
    private final TimerUtil timer = new TimerUtil();
    private boolean reset;
    private double lastGroundY;

    private final List<Packet> packets = new ArrayList<>();

    public AntiVoid() {
        super("AntiVoid", Category.PLAYER, "saves you from the void");
        this.addSettings(mode, fallDist);
    }

    @Link
    public Listener<PacketSendEvent> onPacketSend = event -> {
        if (!AlwaysUtil.isPlayerInGame()) {
            return;
        }
        setSuffix(mode.getMode());
        if (mode.is("Watchdog") && !Echo.INSTANCE.getModuleCollection().getModule(Speed.class).isEnabled()) {
            if (event.getPacket() instanceof C03PacketPlayer) {
                if (!isBlockUnder()) {
                    if (mc.thePlayer.fallDistance < fallDist.getValue()) {
                        event.setCancelled(true);
                        packets.add(event.getPacket());
                    } else {
                        if (!packets.isEmpty()) {
                            for (Packet packet : packets) {
                                final C03PacketPlayer c03 = (C03PacketPlayer) packet;
                                c03.setY(lastGroundY);
                                PacketUtils.sendPacketNoEvent(packet);
                            }
                            packets.clear();
                        }
                    }
                } else {
                    lastGroundY = mc.thePlayer.posY;
                    if (!packets.isEmpty()) {
                        packets.forEach(PacketUtils::sendPacketNoEvent);
                        packets.clear();
                    }
                }
            }
        }
        if (mode.is("BlocksMC")) {
            if (!isBlockUnder()) {
                if (mc.thePlayer.fallDistance > fallDist.getValue()) {
                    mc.thePlayer.fallDistance = 0;
                    mc.thePlayer.motionY = 3;
                }
            }
        }
    };

    private boolean isBlockUnder() {
        if (!AlwaysUtil.isPlayerInGame()) {
            return false;
        }
        if (mc.thePlayer.posY < 0) {
            return false;
        }
        for (int offset = 0; offset < (int) mc.thePlayer.posY + 2; offset += 2) {
            AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
