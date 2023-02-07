package org.crystalpvp.anticheat.packet;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.checks.ICheck;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.update.RotationUpdate;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import lombok.AllArgsConstructor;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;

import org.bukkit.entity.Player;
import pt.foxspigot.jar.handler.MovementHandler;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@AllArgsConstructor
public class CustomMovementHandler implements MovementHandler {

    private final CrystalAC plugin;
    
    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packet) {
        if (player.getAllowFlight()) {
            return;
        }

        if (player.isInsideVehicle()) {
            return;
        }

        if (to.getY() < 2.0) {
            return;
        }

        if (!player.getWorld().isChunkLoaded(to.getBlockX() >> 4, to.getBlockZ() >> 4)) {
            return;
        }

        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        if (playerData == null) {
            return;
        }

        playerData.setWasOnPiston(playerData.isOnPiston());
        playerData.setWasOnGround(playerData.isOnGround());
        playerData.setWasInLiquid(playerData.isInLiquid());
        playerData.setWasUnderBlock(playerData.isUnderBlock());
        playerData.setWasInWeb(playerData.isInWeb());
        playerData.setOnGround(BlockUtil.isOnGround(to, 0) || BlockUtil.isOnGround(to, 1));

        if (!playerData.isOnGround()) {
            positions:
            for (BlockPosition position : playerData.getFakeBlocks()) {
                int x = position.getX();
                int z = position.getZ();

                int blockX = to.getBlock().getX();
                int blockZ = to.getBlock().getZ();

                for (int xOffset = -1; xOffset <= 1; xOffset++) {
                    for (int zOffset = -1; zOffset <= 1; zOffset++) {
                        if (x == blockX + xOffset && z == blockZ + zOffset) {
                            int y = position.getY();
                            int pY = to.getBlock().getY();

                            if (pY - y <= 1 && pY > y) {
                                playerData.setOnGround(true);
                            }

                            if (playerData.isOnGround()) {
                                break positions;
                            }
                        }
                    }
                }
            }
        }

        if (playerData.isOnGround()) {
            playerData.setLastGroundY(to.getY());
        }

        playerData.setInLiquid(BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1) || BlockUtil.isOnLiquid(to, -1));
        playerData.setInWeb(BlockUtil.isOnWeb(to, 0));
        playerData.setOnIce(BlockUtil.isOnIce(to, 1) || BlockUtil.isOnIce(to, 2));

        if (playerData.isOnIce()) {
            playerData.setMovementsSinceIce(0);
        } else {
            playerData.setMovementsSinceIce(playerData.getMovementsSinceIce() + 1);
        }

        playerData.setOnStairs(BlockUtil.isOnStairs(to, 0) || BlockUtil.isOnStairs(to, 1));
        playerData.setOnCarpet(BlockUtil.isOnCarpet(to, 0) || BlockUtil.isOnCarpet(to, 1));
        playerData.setOnPiston(BlockUtil.isOnPiston(to, 0) || BlockUtil.isOnPiston(to, 1) || BlockUtil.isOnPiston(to, 2));
        playerData.setUnderBlock(BlockUtil.isOnGround(to, -2));

        if (playerData.isUnderBlock()) {
            playerData.setMovementsSinceUnderBlock(0);
        } else {
            playerData.setMovementsSinceUnderBlock(playerData.getMovementsSinceUnderBlock() + 1);
        }

        if (to.getY() != from.getY() && playerData.getVelocityV() > 0) {
            playerData.setVelocityV(playerData.getVelocityV() - 1);
        }

        if (Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ()) > 0.0 && playerData.getVelocityH() > 0) {
            playerData.setVelocityH(playerData.getVelocityH() - 1);
        }

        for (final Class<? extends ICheck> checkClass : PlayerData.CHECKS) {
            if (!CrystalAC.getInstance().getDisabledChecks().contains(checkClass.getSimpleName().toUpperCase())) {
                final ICheck check = playerData.getCheck(checkClass);
                if (check != null && check.getType() == PositionUpdate.class) {
                    check.handleCheck(player, new PositionUpdate(player, to, from, packet));
                }
            }
        }

        if (playerData.getVelocityY() > 0.0 && to.getY() > from.getY()) {
            playerData.setVelocityY(0.0);
        }


        double xz = Math.hypot(from.getX() - to.getX(), from.getZ() - to.getZ());
        if (xz < 0) {
            xz *= -1;
        }
        playerData.setLastDistanceXZ(xz);




        double y = to.getY() - from.getY();
        playerData.setLastDistanceY(y);


        double xDiff = from.getX() - to.getX();
        if (xDiff < 0) {
            playerData.setXDirection("WEST");
        } else if (xDiff > 0) {
            playerData.setXDirection("EAST");
        } else {
            playerData.setXDirection("NONE");
        }

        double zDiff = from.getX() - to.getX();
        if (zDiff < 0) {
            playerData.setZDirection("NORTH");
        } else if (zDiff > 0) {
            playerData.setZDirection("SOUTH");
        } else {
            playerData.setZDirection("NONE");
        }
    }
    
    public void handleUpdateRotation(final Player player, final Location to, final Location from, final PacketPlayInFlying packet) {
        if (player.getAllowFlight()) {
            return;
        }

        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        if (playerData == null) {
            return;
        }

        for (final Class<? extends ICheck> checkClass : PlayerData.CHECKS) {
            if (!CrystalAC.getInstance().getDisabledChecks().contains(checkClass.getSimpleName().toUpperCase())) {
                final ICheck check = playerData.getCheck(checkClass);

                if (check != null && check.getType() == RotationUpdate.class) {
                    check.handleCheck(player, new RotationUpdate(player, to, from, packet));
                }
            }
        }


        float diffYaw = MathUtil.getDistanceBetweenAngles(to.getYaw(), from.getYaw());
        playerData.setLastDiffYaw(diffYaw);

        float diffPitch = MathUtil.getDistanceBetweenAngles(to.getPitch(), from.getPitch());
        playerData.setLastDiffYaw(diffPitch);

        double newPitch = to.getPitch();

        playerData.setWasFacing(playerData.getFacing());
        if (newPitch > 0 && newPitch <= 45) {
            playerData.setFacing("SOUTH");
        } else if (newPitch > 45 && newPitch <= 135) {
            playerData.setFacing("WEST");
        } else if (newPitch > 135 && newPitch <= -135) {
            playerData.setFacing("NORTH");
        } else {
            playerData.setFacing("EAST");
        }
    }

}
