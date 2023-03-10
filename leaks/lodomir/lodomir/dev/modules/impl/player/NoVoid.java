/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.event.impl.network.EventSendPacket;
import lodomir.dev.event.impl.player.EventCollideBlock;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.player.PlayerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.AxisAlignedBB;

public class NoVoid
extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Flag", "Flag", "Float", "Blink", "Pos", "Jump");
    private final NumberSetting fall = new NumberSetting("Fall Distance", 1.0, 10.0, 5.0, 1.0);
    private final ArrayList<Packet> packets = new ArrayList();

    public NoVoid() {
        super("NoVoid", 0, Category.PLAYER);
        this.addSettings(this.mode, this.fall);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        this.setSuffix(this.mode.getMode());
    }

    @Override
    public void onDisable() {
        for (Packet p : this.packets) {
            if (mc.isSingleplayer()) continue;
            this.sendPacketSilent(p);
        }
        this.packets.clear();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        this.packets.clear();
        super.onEnable();
    }

    @Override
    @Subscribe
    public void onBlockCollide(EventCollideBlock event) {
        switch (this.mode.getMode()) {
            case "Float": {
                if (!(event.getBlock() instanceof BlockAir)) break;
                double x = event.getX();
                double y = event.getY();
                double z = event.getZ();
                if (!(y < NoVoid.mc.thePlayer.posY)) break;
                event.setCollisionBoundingBox(AxisAlignedBB.fromBounds(-20.0, -1.0, -20.0, 20.0, 1.0, 20.0).offset(x, y, z));
            }
        }
    }

    @Override
    @Subscribe
    public void onSendPacket(EventSendPacket event) {
        if (this.mode.isMode("Blink") && !PlayerUtils.isBlockUnder() && (double)NoVoid.mc.thePlayer.fallDistance > this.fall.getValue() && (event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C0APacketAnimation || event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C07PacketPlayerDigging || event.getPacket() instanceof C08PacketPlayerBlockPlacement)) {
            this.packets.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        if (!PlayerUtils.isBlockUnder() && (double)NoVoid.mc.thePlayer.fallDistance > this.fall.getValue()) {
            switch (this.mode.getMode()) {
                case "Flag": 
                case "Blink": {
                    this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition());
                    break;
                }
                case "Pos": {
                    this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(NoVoid.mc.thePlayer.posX, event.getY() + (double)NoVoid.mc.thePlayer.fallDistance, NoVoid.mc.thePlayer.posZ, true));
                    break;
                }
                case "Jump": {
                    if (NoVoid.mc.thePlayer.ticksExisted % 2 != 0) break;
                    NoVoid.mc.thePlayer.jump();
                }
            }
        }
    }
}

