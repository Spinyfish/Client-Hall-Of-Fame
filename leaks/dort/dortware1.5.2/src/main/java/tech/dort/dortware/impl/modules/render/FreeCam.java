package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.BlockCollisionEvent;
import tech.dort.dortware.impl.events.MovementEvent;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.utils.movement.MotionUtils;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

/**
 * @author Auth
 */

public class FreeCam extends Module {

    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 10, SliderUnit.BPT);
    private final BooleanValue flag = new BooleanValue("Flag Check", this, true);
    public double startX, startY, startZ;
    private EntityOtherPlayerMP otherPlayerMP;

    public FreeCam(ModuleData moduleData) {
        super(moduleData);
        register(speed, flag);
    }

    @Subscribe
    public void onMove(MovementEvent event) {
        final MovementInput movementInput = mc.thePlayer.movementInput;
        MotionUtils.setMotion(event, this.speed.getCastedValue().floatValue());
        float newSpeed = this.speed.getCastedValue().floatValue() * 0.5F;
        event.setMotionY(movementInput.jump ? newSpeed : movementInput.sneak ? -newSpeed : 0);
        mc.thePlayer.motionY = 0;
    }

    @Subscribe
    public void onCollide(BlockCollisionEvent event) {
        event.setAxisAlignedBB(null);
        mc.thePlayer.noClip = true;
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (flag.getValue()) {
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                this.toggle();
            }

            if (event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C0BPacketEntityAction || event.getPacket() instanceof C0APacketAnimation || event.getPacket() instanceof C02PacketUseEntity) {
                event.setCancelled(true);
            }
            return;
        }

        if (event.getPacket() instanceof S08PacketPlayerPosLook || event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C0BPacketEntityAction || event.getPacket() instanceof C0APacketAnimation || event.getPacket() instanceof C02PacketUseEntity) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {
        otherPlayerMP = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(mc.thePlayer.getUniqueID(), mc.thePlayer.getDisplayName().getUnformattedText()));
        otherPlayerMP.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, 0, 0);
        otherPlayerMP.inventoryContainer = mc.thePlayer.inventoryContainer;
        otherPlayerMP.inventory = mc.thePlayer.inventory;
        mc.theWorld.addEntityToWorld(otherPlayerMP.getEntityId(), otherPlayerMP);
        startX = mc.thePlayer.posX;
        startY = mc.thePlayer.posY;
        startZ = mc.thePlayer.posZ;
        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.theWorld.removeEntityFromWorld(otherPlayerMP.getEntityId());
        mc.thePlayer.setPosition(startX, startY, startZ);
        MotionUtils.setMotion(0);
        mc.thePlayer.noClip = false;
    }
}