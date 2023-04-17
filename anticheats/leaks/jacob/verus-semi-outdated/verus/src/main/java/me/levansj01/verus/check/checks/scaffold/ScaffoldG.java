package me.levansj01.verus.check.checks.scaffold;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import me.levansj01.verus.compat.api.wrapper.Direction;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.location.PlayerLocation;
import org.bukkit.inventory.ItemStack;

@CheckInfo(
        friendlyName = "Scaffold",
        type = CheckType.SCAFFOLD,
        subType = "G",
        version = CheckVersion.RELEASE,
        schem = true,
        minViolations = -1.0D,
        maxViolations = 10
)
public class ScaffoldG extends Check implements PacketHandler {
    private static final Cuboid CUBOID = new Cuboid();

    public void handle(VPacketPlayInFlying vPacket) {
        this.decreaseVL(0.05D);
    }

    public void handle(VPacketPlayInBlockPlace vPacket) {
        int face = vPacket.getFace();
        if (face != 255 && !this.playerData.isTeleportingV2()) {
            ItemStack itemStack = vPacket.getItemStack();
            if (itemStack != null && itemStack.getType() != MaterialList.AIR) {
                BlockPosition blockPosition = vPacket.getPosition();
                if (blockPosition != null) {
                    PlayerLocation playerLocation = this.playerData.getLocation().clone();
                    playerLocation.setY(playerLocation.getY() + this.player.getEyeHeight());
                    CUBOID.setValues(playerLocation);
                    if (!CUBOID.containsBlock(this.player.getWorld(), blockPosition)) {
                        Direction direction = Direction.values()[vPacket.getFace()];
                        int ordinal = direction.ordinal();
                        if (ordinal == 1) {
                            double x = playerLocation.getX() - (double) blockPosition.getX();
                            if (x < 0.3D) {
                                this.handleViolation(() -> String.format("%s %.2f", direction.name(), x));
                            }
                        } else if (ordinal == 2) {
                            double z = playerLocation.getZ() - (double) blockPosition.getZ();
                            if (z < 0.3D) {
                                this.handleViolation(() -> String.format("%s %.2f", direction.name(), z));
                            }
                        } else if (ordinal == 3) {
                            double x = playerLocation.getX() - (double) blockPosition.getX();
                            if (x > 0.7D) {
                                this.handleViolation(() -> String.format("%s %.2f", direction.name(), x));
                            }
                        } else if (ordinal == 4) {
                            double z = playerLocation.getZ() - (double) blockPosition.getZ();
                            if (z > 0.7D) {
                                this.handleViolation(() -> String.format("%s %.2f", direction.name(), z));
                            }
                        } else if (ordinal == 5) {
                            double y = playerLocation.getY() - (double) blockPosition.getY();
                            if (y > 2.0D) {
                                this.handleViolation(() -> String.format("%s %.2f", direction.name(), y));
                            }
                        }
                    }
                }
            }
        }
    }
}
