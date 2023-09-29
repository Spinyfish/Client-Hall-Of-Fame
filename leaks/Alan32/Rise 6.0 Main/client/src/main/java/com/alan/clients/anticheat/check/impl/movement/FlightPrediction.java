package com.alan.clients.anticheat.check.impl.movement;

import com.alan.clients.anticheat.check.Check;
import com.alan.clients.anticheat.check.api.CheckInfo;
import com.alan.clients.anticheat.check.impl.combat.VelocityCancel;
import com.alan.clients.anticheat.data.PlayerData;
import com.alan.clients.anticheat.util.PacketUtil;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.Vec3;
import util.time.StopWatch;
import util.type.EvictingList;

import java.util.ArrayList;

@CheckInfo(name = "Flight", type = "Prediction", description = "Detects flight")
public final class FlightPrediction extends Check {

    private EvictingList<Vec3> possibleGroundPositions = new EvictingList<>(144);
    private ArrayList<Double> possibleJumpMotions = new ArrayList<>();

    // Check settings
    private double MINIMUM_OFFSET_TO_FLAG = 1E-5;
    private int MAX_AIR_TICKS_BRUTEFORCE = 60;
    private int BLOCKS_PREDICTED = 10;
    private int MAX_JUMP_BOOST_MODIFIER = 5;

    private int previousAirTick;

    private StopWatch timeSinceAirTickReset = new StopWatch();
    private int invalidAirTicks, ticksSinceVelocity;
    private boolean velocity, receivedVelocity;


    public FlightPrediction(final PlayerData data) {
        super(data);

        // Using minecraft code fill the arraylist with possible jump motions
        for (int jumpModifier = 0; jumpModifier < MAX_JUMP_BOOST_MODIFIER; jumpModifier++) {
            double jumpDelta = MoveUtil.JUMP_HEIGHT;

            if (jumpModifier != 0) {
                jumpDelta = jumpDelta + (jumpModifier + 1) * 0.1F;
            }

            possibleJumpMotions.add(jumpDelta);
        }

        // Used for when the player falls off a block not jumping
        possibleJumpMotions.add(0D);
    }

    @Override
    public void handle(final Packet<?> packet) {
        // Checking if the checks entity matches one of an S14 or S18, we include teleports because if the player moves to fast we receive a teleport not a move
        if (((PacketUtil.isRelMove(packet) && ((S14PacketEntity) packet).entityId == data.getPlayer().getEntityId()) ||
                (packet instanceof S18PacketEntityTeleport && ((S18PacketEntityTeleport) packet).entityId == data.getPlayer().getEntityId()))) {

            EntityOtherPlayerMP entity = data.getPlayer();

            if (!velocity && data.getTicksSinceLastVelocity() <= 20) {
                velocity = true;
                ticksSinceVelocity = 0;
                receivedVelocity = false;

                // Reset buffer so that every check for velocity is consistent
                resetBuffer();
            }

            // Stops joining from falsing
            if (entity.ticksExisted <= 20 * 5) {
                resetBuffer();
            }

            ticksSinceVelocity++;

            // This prevents WatchDog bots from creating false positives
            if (entity.isInvisible()) return;

            /*
             * Saving possible ground positions,
             * this is used to prevent false positives because the server may not send the clients ground position sometimes
             * this fixes the problem because we look through all blocks within the players radius and use them as possible ground positions
             * this may still false if the player lags a lot, this could be fixed by a dynamic radius depending on how long you lag
             */
            if (data.isLastIsRoughlyGround() || data.isRoughlyGround() || data.isOnGround() || data.isLastOnGround()) {
                possibleGroundPositions.removeIf(position -> Math.abs(data.getX() - position.xCoord) + Math.abs(data.getZ() - position.zCoord) > 10);
            }

            for (double offset = 0; offset >= -0.5; offset -= 0.5) {
                for (int x = -3; x <= 3; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -3; z <= 3; z++) {
                            /*
                             * Adding all block heights which the player could have started on to a list,
                             * we check if the y ground position is already in the list to prevent brute forcing the players
                             * position twice, this would take longer to process and not improve the accuracy of the check
                             */
                            Block block = PlayerUtil.block(entity.posX + x, entity.posY + y, entity.posZ + z);

                            if (block.getMaterial() == Blocks.air.getMaterial()) continue;

                            double groundX = Math.floor(entity.posX + x);
                            double groundY = Math.floor(entity.posY + y) + block.getBlockBoundsMaxY() + offset;
                            double groundZ = Math.floor(entity.posZ + z);

                            Vec3 position = new Vec3(groundX, groundY, groundZ);

                            if (possibleGroundPositions.stream().noneMatch(vec3 -> vec3.yCoord == groundY)) {
                                possibleGroundPositions.add(position);
                            }
                        }
                    }
                }
            }

            // Returning because there isn't enough data to run the check
            if (possibleGroundPositions.size() <= 2) {
                return;
            }

            // Returning because it's unnecessary and could cause false positives
            if (data.getY() < data.getGroundY() - BLOCKS_PREDICTED) return;

            {
                double closest = Double.MAX_VALUE;
                double minimumPosY = Double.MAX_VALUE;
                int airTicks = Integer.MAX_VALUE;
                double groundY = Double.MAX_VALUE;

                // Looping through possibilities
                for (Vec3 possibleGround : possibleGroundPositions) {
                    for (double jumpMotion : possibleJumpMotions) {

                        double predictedPosition = possibleGround.yCoord;

                        for (int possibleAirTicks = 0; possibleAirTicks < MAX_AIR_TICKS_BRUTEFORCE; possibleAirTicks++) {

                            predictedPosition += MoveUtil.predictedMotion(jumpMotion, possibleAirTicks);
                            double offset = Math.abs(predictedPosition - data.getY());

                            if (offset < closest) {
                                closest = offset;
                                airTicks = possibleAirTicks;
                                groundY = possibleGround.yCoord;
                            }

                            if (predictedPosition < minimumPosY) minimumPosY = predictedPosition;

                        }
                    }
                }

                // Only run if the position is > the smallest predicted posY, if it's below this the check doesn't try to detect them
                if (data.getY() < minimumPosY) return;

                if (timeSinceAirTickReset.finished(5 * 50) && data.isRoughlyGround()) {
                    timeSinceAirTickReset.reset();
                    invalidAirTicks = 0;
                }

                // If velocity processing is on we need to switch to it
                if (velocity) {

                    // Ik this isn't a good way of doing it cope
                    if (closest > 0.001 && closest != 0.015485600668696975 && closest != 0.01374998688697815
                            && closest != 0.002786007340908725 && closest != 0.0013359791121487774
                            && closest != 0.0013735326446351337 && closest != 0.005455650520339361 &&
                            closest != 0.026450877005913753 && closest != 0.006179987693194278 && closest != 0.0029422088919233147 &&
                            closest != 0.0031678198715923145) {
                        receivedVelocity = true;
                    }

                    /*
                     * Disabling velocity processing if we're near the ground, and if our buffer is low
                     * this is because if we don't know the trajectory the player is taking the check will false positive
                     * we want to make sure they're 100% on the ground before allowing the check to flag again
                     */
                    if (data.isRoughlyGround() && data.isOnGround() && closest <= MINIMUM_OFFSET_TO_FLAG && ticksSinceVelocity >= 30) {
                        velocity = false;
                        resetBuffer();
                    }

                    // Flagging for velocity if it hasn't been received after x amount of ticks
                    if (ticksSinceVelocity == 25) {

                        VelocityCancel velocityCancel = (VelocityCancel) data.getCheck(VelocityCancel.class);

                        if (velocityCancel == null) return;

                        if (receivedVelocity) {
                            velocityCancel.decreaseBuffer();
                        } else {
                            velocityCancel.increaseBuffer();
                            if (velocityCancel.getBuffer() > 1) velocityCancel.fail();
                        }

                        receivedVelocity = true;
                    }

                } else {
                    /*
                     * Prevents the client from hovering on the same offGroundTick, and forcing them to go down
                     * this could be exploited to make a YPort but the speed check will flag that
                     */
                    if (airTicks <= previousAirTick && !data.isRoughlyGround() && airTicks > 2 && previousAirTick > 2) {
                        invalidAirTicks++;

                        // We use a base of 0.01 because that's how much we increase our buffer by each tick
                        if (invalidAirTicks > 1) {
                            invalidAirTicks = 0;

                            increaseBufferBy(0.01 + 0.1);
                        }
                    }

                    if (closest < MINIMUM_OFFSET_TO_FLAG) {
                        decreaseBufferBy(1 / 10f);
                    }

                    increaseBufferBy(closest);

                    if (getBuffer() > 0.3) {
                        fail();
                        ChatUtil.display("Offset: " + closest);

                        resetBuffer();
                    }
                }

                /*
                 * We use this to check if our current air tick is < our previous one,
                 * this is impossible and we want to flag for that
                 */
                previousAirTick = airTicks;
            }
        }
    }
}