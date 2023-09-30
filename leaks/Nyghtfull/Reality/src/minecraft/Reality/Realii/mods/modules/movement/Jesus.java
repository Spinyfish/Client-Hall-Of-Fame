/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.movement;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.misc.EventCollideWithBlock;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class Jesus
extends Module {

    public Jesus(){
        super("Jesus", ModuleType.Movement);
    }

    private boolean canJeboos() {
        if (!(this.mc.thePlayer.fallDistance >= 3.0f || this.mc.gameSettings.keyBindJump.isPressed() || BlockHelper.isInLiquid() || this.mc.thePlayer.isSneaking())) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPre(EventPreUpdate e) {
        if (BlockHelper.isInLiquid() && !this.mc.thePlayer.isSneaking() && !this.mc.gameSettings.keyBindJump.isPressed()) {
            this.mc.thePlayer.motionY = 0.05;
            this.mc.thePlayer.onGround = true;
        }
    }

    @EventHandler
    public void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer && this.canJeboos() && BlockHelper.isOnLiquid()) {
            C03PacketPlayer packet = (C03PacketPlayer)e.getPacket();
            packet.y = this.mc.thePlayer.ticksExisted % 2 == 0 ? packet.y + 0.01 : packet.y - 0.01;
        }
    }

    @EventHandler
    public void onBB(EventCollideWithBlock e) {
        if (e.getBlock() instanceof BlockLiquid && this.canJeboos()) {
            e.setBoundingBox(new AxisAlignedBB(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ(), (double)e.getPos().getX() + 1.0, (double)e.getPos().getY() + 1.0, (double)e.getPos().getZ() + 1.0));
        }
    }
}

