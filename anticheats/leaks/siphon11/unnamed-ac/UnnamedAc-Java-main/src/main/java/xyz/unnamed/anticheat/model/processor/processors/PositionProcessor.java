package xyz.unnamed.anticheat.model.processor.processors;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.model.packet.UPacket;
import xyz.unnamed.anticheat.model.packet.impl.v_1_8_8.inbound.PacketInFlying;
import xyz.unnamed.anticheat.model.processor.UProcessor;
import xyz.unnamed.anticheat.utilities.math.MathUtil;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@Getter
@Setter
public class PositionProcessor implements UProcessor {

    // ------------------------------------------------

    //Position
    private double x, y, z, lastX, lastY, lastZ, deltaX, deltaY, deltaZ, deltaXZ , lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;

    private float friction, prevFriction, prevPrevFriction;

    // ------------------------------------------------

    //Rotation
    private float yaw, pitch, lastYaw, lastPitch, deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch, joltYaw, joltPitch, lastJoltYaw, lastJoltPitch;

    private boolean positionUpdate, rotationUpdate;

    // ------------------------------------------------





    @Override
    public void update(UPacket packet) {
        if (!(packet instanceof PacketInFlying))
            return;

        PacketInFlying flying = (PacketInFlying) packet;

        this.positionUpdate = flying.isPos();
        this.rotationUpdate = flying.isRot();

        if (this.positionUpdate) {
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;

            this.x = flying.getX();
            this.y = flying.getY();
            this.z = flying.getZ();

            this.prevPrevFriction = this.prevFriction;
            this.prevFriction = this.friction;

            this.lastDeltaX = this.deltaX;
            this.lastDeltaY = this.deltaY;
            this.lastDeltaZ = this.deltaZ;

            this.deltaX = this.x - this.lastX;
            this.deltaY = this.y - this.lastY;
            this.deltaZ = this.z - this.lastZ;

            this.deltaXZ = MathUtil.hypot(deltaX, deltaZ);
            this.lastDeltaXZ = this.deltaXZ;

        }

        if (this.rotationUpdate) {
            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;

            this.lastJoltPitch = this.joltPitch;
            this.lastJoltYaw = this.joltYaw;

            this.yaw = flying.getYaw();
            this.pitch = flying.getPitch();

            this.lastDeltaYaw = deltaYaw;
            this.lastDeltaPitch = deltaPitch;

            this.deltaYaw = Math.abs(yaw - lastYaw);
            this.deltaPitch = Math.abs(pitch - lastPitch);

            this.joltYaw = Math.abs(deltaYaw - lastDeltaYaw);
            this.joltPitch = Math.abs(deltaPitch - lastDeltaPitch);
        }
    }
}
