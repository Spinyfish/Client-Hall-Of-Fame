/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10TO1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.PlayerPosition;
import de.gerrygames.viarewind.utils.PacketUtil;
import de.gerrygames.viarewind.utils.Tickable;

public class WorldBorder
extends StoredObject
implements Tickable {
    private double x;
    private double z;
    private double oldDiameter;
    private double newDiameter;
    private long lerpTime;
    private long lerpStartTime;
    private int portalTeleportBoundary;
    private int warningTime;
    private int warningBlocks;
    private boolean init = false;
    private final int VIEW_DISTANCE = 16;

    public WorldBorder(UserConnection user) {
        super(user);
    }

    @Override
    public void tick() {
        if (!this.isInit()) {
            return;
        }
        this.sendPackets();
    }

    private void sendPackets() {
        PlayerPosition position = this.getUser().get(PlayerPosition.class);
        double radius = this.getSize() / 2.0;
        for (Side side : Side.values()) {
            double d;
            double center;
            double pos;
            if (side.modX != 0) {
                pos = position.getPosZ();
                center = this.z;
                d = Math.abs(this.x + radius * (double)side.modX - position.getPosX());
            } else {
                center = this.x;
                pos = position.getPosX();
                d = Math.abs(this.z + radius * (double)side.modZ - position.getPosZ());
            }
            if (d >= 16.0) continue;
            double r = Math.sqrt(256.0 - d * d);
            double minH = Math.ceil(pos - r);
            double maxH = Math.floor(pos + r);
            double minV = Math.ceil(position.getPosY() - r);
            double maxV = Math.floor(position.getPosY() + r);
            if (minH < center - radius) {
                minH = Math.ceil(center - radius);
            }
            if (maxH > center + radius) {
                maxH = Math.floor(center + radius);
            }
            if (minV < 0.0) {
                minV = 0.0;
            }
            double centerH = (minH + maxH) / 2.0;
            double centerV = (minV + maxV) / 2.0;
            int a = (int)Math.floor((maxH - minH) * (maxV - minV) * 0.5);
            double b = 2.5;
            PacketWrapper particles = PacketWrapper.create(42, null, this.getUser());
            particles.write(Type.STRING, "fireworksSpark");
            particles.write(Type.FLOAT, Float.valueOf((float)(side.modX != 0 ? this.x + radius * (double)side.modX : centerH)));
            particles.write(Type.FLOAT, Float.valueOf((float)centerV));
            particles.write(Type.FLOAT, Float.valueOf((float)(side.modX == 0 ? this.z + radius * (double)side.modZ : centerH)));
            particles.write(Type.FLOAT, Float.valueOf((float)(side.modX != 0 ? 0.0 : (maxH - minH) / b)));
            particles.write(Type.FLOAT, Float.valueOf((float)((maxV - minV) / b)));
            particles.write(Type.FLOAT, Float.valueOf((float)(side.modX == 0 ? 0.0 : (maxH - minH) / b)));
            particles.write(Type.FLOAT, Float.valueOf(0.0f));
            particles.write(Type.INT, a);
            PacketUtil.sendPacket(particles, Protocol1_7_6_10TO1_8.class, true, true);
        }
    }

    private boolean isInit() {
        return this.init;
    }

    public void init(double x, double z, double oldDiameter, double newDiameter, long lerpTime, int portalTeleportBoundary, int warningTime, int warningBlocks) {
        this.x = x;
        this.z = z;
        this.oldDiameter = oldDiameter;
        this.newDiameter = newDiameter;
        this.lerpTime = lerpTime;
        this.portalTeleportBoundary = portalTeleportBoundary;
        this.warningTime = warningTime;
        this.warningBlocks = warningBlocks;
        this.init = true;
    }

    public double getX() {
        return this.x;
    }

    public double getZ() {
        return this.z;
    }

    public void setCenter(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public double getOldDiameter() {
        return this.oldDiameter;
    }

    public double getNewDiameter() {
        return this.newDiameter;
    }

    public long getLerpTime() {
        return this.lerpTime;
    }

    public void lerpSize(double oldDiameter, double newDiameter, long lerpTime) {
        this.oldDiameter = oldDiameter;
        this.newDiameter = newDiameter;
        this.lerpTime = lerpTime;
        this.lerpStartTime = System.currentTimeMillis();
    }

    public void setSize(double size) {
        this.oldDiameter = size;
        this.newDiameter = size;
        this.lerpTime = 0L;
    }

    public double getSize() {
        if (this.lerpTime == 0L) {
            return this.newDiameter;
        }
        long time = System.currentTimeMillis() - this.lerpStartTime;
        double percent = (double)time / (double)this.lerpTime;
        if (percent > 1.0) {
            percent = 1.0;
        } else if (percent < 0.0) {
            percent = 0.0;
        }
        return this.oldDiameter + (this.newDiameter - this.oldDiameter) * percent;
    }

    public int getPortalTeleportBoundary() {
        return this.portalTeleportBoundary;
    }

    public void setPortalTeleportBoundary(int portalTeleportBoundary) {
        this.portalTeleportBoundary = portalTeleportBoundary;
    }

    public int getWarningTime() {
        return this.warningTime;
    }

    public void setWarningTime(int warningTime) {
        this.warningTime = warningTime;
    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }

    public void setWarningBlocks(int warningBlocks) {
        this.warningBlocks = warningBlocks;
    }

    private static enum Side {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);

        private int modX;
        private int modZ;

        private Side(int modX, int modZ) {
            this.modX = modX;
            this.modZ = modZ;
        }
    }
}

