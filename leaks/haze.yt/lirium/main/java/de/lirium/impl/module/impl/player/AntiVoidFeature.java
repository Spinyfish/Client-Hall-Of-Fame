package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import god.buddy.aot.BCompiler;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

@ModuleFeature.Info(name = "Anti Void", description = "Prevents you from falling into the void", category = ModuleFeature.Category.PLAYER)
public class AntiVoidFeature extends ModuleFeature {

    @Value(name = "Mode")
    private final ComboBox<String> mode = new ComboBox<>("NCP", new String[]{"Intave", "Sparky"});

    @Value(name = "Sparky - Mode")
    private final ComboBox<String> sparkyMode = new ComboBox<>("Drag Down", new String[] {"Motion"}, new Dependency<>(mode, "Sparky"));



    private final List<Packet> packets = new ArrayList<>();

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        setSuffix(mode.getValue());
        doUpdateEvent();
    };

    @EventHandler
    public final Listener<PacketEvent> packetEventListener = this::doPacketEvent;

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doPacketEvent(PacketEvent e) {
        switch (mode.getValue()) {
            case "Intave":
                boolean isValid = false;
                for (int y = -2; y > 0; y--) {
                    final BlockPos pos = new BlockPos(getX(), y, getZ());
                    if (!getWorld().isAirBlock(pos))
                        isValid = true;
                }

                if (e.packet instanceof CPacketPlayer) {
                    if (!getPlayer().onGround) {
                        packets.add(e.packet);
                        e.setCancelled(true);
                    }

                    if (getPlayer().fallDistance > 5) {
                        sendPacketUnlogged(new CPacketPlayer.Position(getX() - 1, getY() + 2, getZ(), false));
                    }
                }
                if (e.packet instanceof SPacketConfirmTransaction) {
                    if (!getPlayer().onGround && !isValid) {
                        packets.add(e.packet);
                        e.setCancelled(true);
                    } else {
                        if (!packets.isEmpty()) {
                            packets.forEach(packet -> packet.processPacket(mc.getConnection()));
                            packets.clear();
                        }
                    }
                }
                break;
        }
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doUpdateEvent() {
        boolean isVoid = true;
        for (int y = (int) getPlayer().posY; y > 0; y--) {
            final BlockPos pos = new BlockPos(getX(), y, getZ());
            if (!getWorld().isAirBlock(pos))
                isVoid = false;
        }

        if (isVoid) {
            switch (mode.getValue()) {
                case "Sparky":
                    switch (sparkyMode.getValue()) {
                        case "Motion":
                            if (getPlayer().fallDistance > 3) {
                                getPlayer().motionY = -(0.1 * 0.98);
                                if (!isMoving())
                                    setSpeed(0.1);
                            }
                            break;
                        case "Drag Down":
                            if(getPlayer().fallDistance >= 3) {
                                getPlayer().motionY -= 15;
                            }
                            break;
                    }
                    break;
                case "NCP":
                    if (getPlayer().fallDistance > 3) {
                        sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY() + 0.42, getZ(), true));
                    }
                    break;
            }
        }
    }
}