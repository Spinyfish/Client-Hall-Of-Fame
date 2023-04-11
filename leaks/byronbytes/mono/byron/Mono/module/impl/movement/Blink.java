package byron.Mono.module.impl.movement;

import byron.Mono.event.impl.EventPacket;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vector3d;

import java.util.ArrayList;
import java.util.List;

@ModuleInterface(name = "Blink", description = "Pretends lagging to other players", category = Category.Movement)
public class Blink extends Module {


    private final List<Packet> packets = new ArrayList<Packet>();
    private final List<Vector3d> positions = new ArrayList<Vector3d>();

    private BlockPos startPos;

    private boolean doCancel;


    @Override
    public void onEnable() {
        if (mc.theWorld == null)
            return;
        doCancel = true;
        startPos = mc.thePlayer.getPosition();
        final EntityOtherPlayerMP clone = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        clone.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        clone.rotationYawHead = mc.thePlayer.rotationYawHead;
        mc.theWorld.addEntityToWorld(-13371337, clone);
    }

    @Override
    public void onDisable() {
        if (mc.theWorld == null)
            return;
        doCancel = false;
        packets.forEach(mc.thePlayer.sendQueue::addToSendQueue);
        mc.theWorld.removeEntityFromWorld(-13371337);
    }


    @Subscribe
    public void onPacket(EventPacket event) {
         event.setCancelled(doCancel);
        if (moved())
            positions.add(new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));

        if (doCancel) {
            if (event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition)
                packets.add(event.getPacket());
        }
    }

    private boolean moved() {
        return mc.thePlayer.prevPosX != mc.thePlayer.posX || mc.thePlayer.prevPosY != mc.thePlayer.posY || mc.thePlayer.prevPosZ != mc.thePlayer.posZ;
    }
    }


