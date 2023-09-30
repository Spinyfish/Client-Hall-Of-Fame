package me.jinthium.straight.impl.modules.movement.flight;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.game.TeleportEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.modules.movement.Flight;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import me.jinthium.straight.impl.utils.vector.Vector3d;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModeInfo(name = "BlockDrop", parent = Flight.class)
public class BlockDropFly extends ModuleMode<Flight> {

    private final NumberSetting speed = new NumberSetting("Speed", 1, 0.1, 9.5, 0.1);
    private Vector3d position;
    private Vector2f rotation;
    private EntityOtherPlayerMP blinkEntity;

    public BlockDropFly(){
        registerSettings(speed);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(blinkEntity != null)
            deSpawnEntity();

        MovementUtil.stop();
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(position.getX(),
                position.getY(), position.getZ(), rotation.getX(), rotation.getY(), false));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc == null || mc.thePlayer == null) return;
        this.position = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        this.rotation = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        switch (event.getEventState()) {
            case PRE -> {
                deSpawnEntity();
                spawnEntity(position.getX(), position.getY(), position.getZ(), rotation.getX(), rotation.getY());

                RotationUtils.setRotations(event, rotation, 10);
                mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? speed.getValue().floatValue() :
                        mc.gameSettings.keyBindSneak.isKeyDown() ? -speed.getValue().floatValue() : 0;
            }
            case POST -> {
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(position.getX(), position.getY(),
                        position.getZ(), rotation.getX(), rotation.getY(), false));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                        mc.thePlayer.posY, mc.thePlayer.posZ, rotation.getX(), rotation.getY(), false));
            }
        }
    };

    @Callback
    final EventCallback<PlayerMoveUpdateEvent> playerMoveUpdateEventEventCallback = event -> {
        final float speed = this.speed.getValue().floatValue();

        event.setSpeed(speed);
    };

    @Callback
    final EventCallback<TeleportEvent> teleportEventEventCallback = event -> {
        if (!mc.getNetHandler().doneLoadingTerrain)
            return;

        event.setCancelled(true);
        this.position = new Vector3d(event.getPosX(), event.getPosY(), event.getPosZ());
        this.rotation = new Vector2f(event.getYaw(), event.getPitch());
    };

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.SENDING && mc.getNetHandler() != null && mc.getNetHandler().doneLoadingTerrain && event.getPacket() instanceof C03PacketPlayer)
            event.cancel();
    };

    public void deSpawnEntity() {
        if (blinkEntity != null) {
            mc.theWorld.removeEntityFromWorld(blinkEntity.getEntityId());
            KillAura.bots.remove(blinkEntity);
            blinkEntity = null;
        }
    }

    public void spawnEntity(double x, double y, double z, float yaw, float pitch) {
        if (blinkEntity == null) {
            blinkEntity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            blinkEntity.setPositionAndRotation(x, y, z, yaw, pitch);
            blinkEntity.rotationYawHead = yaw;
            blinkEntity.setSprinting(mc.thePlayer.isSprinting());
            blinkEntity.setInvisible(false);
            blinkEntity.setSneaking(mc.thePlayer.isSneaking());
            blinkEntity.inventory = mc.thePlayer.inventory;

            mc.theWorld.addEntityToWorld(blinkEntity.getEntityId(), blinkEntity);
            KillAura.bots.add(blinkEntity);
        }
    }
}
