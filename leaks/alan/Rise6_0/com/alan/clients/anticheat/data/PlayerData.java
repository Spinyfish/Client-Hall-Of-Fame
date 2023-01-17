package com.alan.clients.anticheat.data;

import com.alan.clients.Client;
import com.alan.clients.anticheat.check.Check;
import com.alan.clients.anticheat.check.manager.CheckManager;
import com.alan.clients.anticheat.util.PacketUtil;
import com.alan.clients.util.player.PlayerUtil;
import util.time.StopWatch;
import lombok.Getter;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.MathHelper;

import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.util.MathHelper.wrapAngleTo180_double;

@Getter
public final class PlayerData {

    private final EntityOtherPlayerMP player;

    private final List<Check> checks;

    private int serverPosX, serverPosY, serverPosZ;

    private int ticksSinceLastTeleport,
            ticksSinceLastVelocity;

    private double x, y, z,
            lastX, lastY, lastZ,
            deltaX, deltaY, deltaZ,
            lastDeltaX, lastDeltaY, lastDeltaZ,
            deltaXZ, lastDeltaXZ;

    private double groundX, groundY, groundZ,
            lastGroundX, lastGroundY, lastGroundZ;

    private double yaw, pitch, lastYaw, lastPitch;

    private boolean onGround, lastOnGround, movingForward;
    private int groundTicks;
    private boolean isRoughlyGround, lastIsRoughlyGround;
    private StopWatch timeSinceLastMove = new StopWatch();
    private int ping = 20 * 1; // The ping we assume the player has in ticks
    private int maxPingSpike = 1; // Maximum ping spike in the last 30 seconds in ticks

    private int ticksSinceAttack;
    private int ticksSinceFall;
    private EntityPlayer lastAttackEntity;

    public PlayerData(final EntityOtherPlayerMP player) {
        this.player = player;

        this.serverPosX = player.serverPosX;
        this.serverPosY = player.serverPosY;
        this.serverPosZ = player.serverPosZ;

        this.checks = CheckManager.loadChecks(this);
    }

    public void handle(final Packet<?> packet) {
        if (this.player.ticksExisted <= 80) {
            this.timeSinceLastMove.reset();
            return;
        }

        if (Client.INSTANCE.getBotManager().contains(this.player) || !this.player.moved || this.player.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
            return;
        }

        if (PacketUtil.isRelMove(packet)) {
            final S14PacketEntity wrapper = ((S14PacketEntity) packet);

            if (wrapper.entityId == this.player.getEntityId()) {
                if (this.player.hurtTime != 0 && this.ticksSinceLastVelocity > 9 && this.ticksSinceFall > 40) {
                    this.ticksSinceLastVelocity = 0;
                }

                this.serverPosX += wrapper.posX;
                this.serverPosY += wrapper.posY;
                this.serverPosZ += wrapper.posZ;

                this.lastX = this.x;
                this.lastY = this.y;
                this.lastZ = this.z;

                this.x = (double) this.serverPosX / 32.0D;
                this.y = (double) this.serverPosY / 32.0D;
                this.z = (double) this.serverPosZ / 32.0D;

                this.lastYaw = yaw;
                this.lastPitch = pitch;

                if (packet instanceof S14PacketEntity.S17PacketEntityLookMove) {
                    this.yaw = wrapper.yaw;
                    this.pitch = wrapper.pitch;
                }

                this.lastDeltaX = deltaX;
                this.lastDeltaY = deltaY;
                this.lastDeltaZ = deltaZ;
                this.deltaY = this.y - this.lastY;
                this.deltaZ = this.z - this.lastZ;
                this.deltaX = this.x - this.lastX;

                this.lastDeltaXZ = this.deltaXZ;
                this.deltaXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);

                double expectedSpeed = (this.timeSinceLastMove.getElapsedTime() / 50f) * 0.2;
                if (this.timeSinceLastMove.getElapsedTime() / 50 > maxPingSpike && (this.deltaXZ > expectedSpeed || this.lastDeltaXZ > expectedSpeed)) {
                    this.maxPingSpike = (int) (this.timeSinceLastMove.getElapsedTime() / 50);
                }

                this.timeSinceLastMove.reset();

                this.lastOnGround = this.onGround;
                this.onGround = !(PlayerUtil.block(this.x - 0.5, this.y - 0.43, this.z - 0.5) instanceof BlockAir) ||
                        !(PlayerUtil.block(this.x + 0.5, this.y - 0.43, this.z - 0.5) instanceof BlockAir) ||
                        !(PlayerUtil.block(this.x + 0.5, this.y - 0.43, this.z + 0.5) instanceof BlockAir) ||
                        !(PlayerUtil.block(this.x - 0.5, this.y - 0.43, this.z + 0.5) instanceof BlockAir);

                this.lastIsRoughlyGround = this.isRoughlyGround;
                this.isRoughlyGround = !(PlayerUtil.block(this.x - 0.5, this.y - 0.99, this.z - 0.5) instanceof BlockAir) ||
                        !(PlayerUtil.block(this.x + 0.5, this.y - 0.99, this.z - 0.5) instanceof BlockAir) ||
                        !(PlayerUtil.block(this.x + 0.5, this.y - 0.99, this.z + 0.5) instanceof BlockAir) ||
                        !(PlayerUtil.block(this.x - 0.5, this.y - 0.99, this.z + 0.5) instanceof BlockAir);

                if (this.onGround) {
                    this.lastGroundX = this.groundX;
                    this.lastGroundY = this.groundY;
                    this.lastGroundZ = this.groundZ;
                    this.groundX = this.x;
                    this.groundY = this.y;
                    this.groundZ = this.z;
                    this.groundTicks++;
                } else {
                    this.groundTicks = 0;
                }

                if (groundY - y > 2.5) ticksSinceFall = 0;

                double movementYaw = (wrapAngleTo180_double(Math.toDegrees(Math.atan2((lastX - x), (lastZ - z)))) + 180) * -1;
                double yaw = wrapAngleTo180_double(getYaw());
                double difference = Math.abs(yaw - movementYaw);
                movingForward = (difference < 70 || difference > (360 - 70));
            }
        } else if (packet instanceof S18PacketEntityTeleport) {
            final S18PacketEntityTeleport wrapper = ((S18PacketEntityTeleport) packet);

            if (wrapper.getEntityId() == this.player.getEntityId()) {
                this.serverPosX = wrapper.getX();
                this.serverPosY = wrapper.getY();
                this.serverPosZ = wrapper.getZ();

                this.lastX = this.x;
                this.lastY = this.y;
                this.lastZ = this.z;

                this.x = (double) this.serverPosX / 32.0D;
                this.y = (double) this.serverPosY / 32.0D;
                this.z = (double) this.serverPosZ / 32.0D;

                this.ticksSinceLastTeleport = 0;

                this.lastDeltaXZ = this.deltaXZ;
                this.deltaXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);

                double expectedSpeed = (this.timeSinceLastMove.getElapsedTime() / 50f) * 0.2;
                if (this.timeSinceLastMove.getElapsedTime() / 50 > maxPingSpike && (this.deltaXZ > expectedSpeed || this.lastDeltaXZ > expectedSpeed)) {
                    this.maxPingSpike = (int) (this.timeSinceLastMove.getElapsedTime() / 50);
                }
            }
        } else if (player.isSwingInProgress) {
            List<EntityPlayer> playerEntities = Minecraft.getMinecraft().theWorld.playerEntities.stream()
                    .filter(entityPlayer -> entityPlayer.getDistanceSqToEntity(player) < 6 * 6)
                    .collect(Collectors.toList());

            // Stop false flags
            if (playerEntities.size() != 2) return;

            lastAttackEntity = playerEntities.get(0);
            ticksSinceAttack = 0;
        }

        this.checks.forEach(check -> check.handle(packet));
    }

    public void incrementTick() {
        this.ticksSinceLastVelocity++;
        this.ticksSinceLastTeleport++;
        this.ticksSinceAttack++;
        this.ticksSinceFall++;
    }

    public Check getCheck(Class<?> clazz) {
        for (Check check: this.checks) {
            if (check.getClass() == clazz) return check;
        }

        return null;
    }
}
