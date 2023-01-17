package com.alan.clients.module.impl.combat;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.player.PingSpoofComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.newevent.impl.render.Render3DEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.NumberValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.List;

@Rise
@ModuleInfo(name = "Legit Reach", description = "module.combat.legitreach.description", category = Category.COMBAT)
public final class LegitReach extends Module {

    public NumberValue maxPingSpoof = new NumberValue("Max Ping Spoof", this, 1000, 50, 10000, 1);
    public BooleanValue preventRepeatedAttacks = new BooleanValue("Prevent repeated attacks", this, true);
    public BooleanValue delayVelocity = new BooleanValue("Delay Velocity", this, true);
    public BooleanValue delayBlockUpdates = new BooleanValue("Delay Block Updates", this, true);

    private Vec3 realTargetPosition = new Vec3(0, 0, 0);
    public Entity targetEntity;
    public boolean isActive, editedPackets;

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        // Getting targets and selecting the nearest one
        List<EntityLivingBase> targets = Client.INSTANCE.getTargetManager().getTargets(9);

        if (targets.isEmpty()) {
            isActive = true;
            targetEntity = null;
            return;
        }

        targetEntity = targets.get(0);

        if (targetEntity == null) {
            return;
        }

        PingSpoofComponent.setSpoofing(maxPingSpoof.getValue().intValue(), true, true, delayVelocity.getValue(), delayBlockUpdates.getValue(), true);

        if (isActive) {
            realTargetPosition = new Vec3(targetEntity.posX, targetEntity.posY, targetEntity.posZ);
            isActive = false;
        }

        while ((targetEntity.getDistanceToEntity(mc.thePlayer) > 3 ||
                realTargetPosition.distanceTo(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) > 6) &&
                !PingSpoofComponent.packets.isEmpty()) {

            PacketUtil.TimedPacket packet = PingSpoofComponent.packets.poll();

            if (packet == null) break;

            PacketUtil.receiveNoEvent(packet.getPacket());
        }
    };

    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        final Packet<?> packet = event.getPacket();

        if (targetEntity == null) {
            return;
        }

        if (packet instanceof S14PacketEntity) {
            S14PacketEntity s14PacketEntity = ((S14PacketEntity) packet);

            if (s14PacketEntity.entityId == targetEntity.getEntityId()) {
                realTargetPosition.xCoord += s14PacketEntity.getPosX() / 32D;
                realTargetPosition.yCoord += s14PacketEntity.getPosY() / 32D;
                realTargetPosition.zCoord += s14PacketEntity.getPosZ() / 32D;
            }
        } else if (packet instanceof S18PacketEntityTeleport) {
            S18PacketEntityTeleport s18PacketEntityTeleport = ((S18PacketEntityTeleport) packet);

            if (s18PacketEntityTeleport.getEntityId() == targetEntity.getEntityId()) {
                realTargetPosition = new Vec3(s18PacketEntityTeleport.getX() / 32D, s18PacketEntityTeleport.getY() / 32D, s18PacketEntityTeleport.getZ() / 32D);
            }
        }
    };


    @EventLink()
    public final Listener<Render3DEvent> onRender3D = event -> {

        if (targetEntity == null) {
            return;
        }

        if (realTargetPosition.squareDistanceTo(new Vec3(targetEntity.posX, targetEntity.posY, targetEntity.posZ)) > 0.1) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GL11.glDepthMask(false);

            double expand = 0.14;

            RenderUtil.drawBoundingBox(mc.thePlayer.getEntityBoundingBox().offset(-mc.thePlayer.posX, -mc.thePlayer.posY, -mc.thePlayer.posZ).
                    offset(realTargetPosition.xCoord, realTargetPosition.yCoord, realTargetPosition.zCoord).expand(expand, expand, expand));

            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GL11.glDepthMask(true);
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
            GlStateManager.resetColor();
        }
    };


    @EventLink()
    public final Listener<AttackEvent> onAttack = event -> {
        if (preventRepeatedAttacks.getValue()) editedPackets = true;
    };
}
