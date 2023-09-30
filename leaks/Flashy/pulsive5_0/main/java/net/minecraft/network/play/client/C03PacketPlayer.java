package net.minecraft.network.play.client;

import lombok.Setter;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

@Setter
public class C03PacketPlayer implements Packet<INetHandlerPlayServer>
{
    public double x;
    public double y;
    public double z;
    protected float yaw;
    protected float pitch;
    protected boolean onGround;
    protected boolean moving;
    public boolean rotating;

    public C03PacketPlayer()
    {
    }

    public C03PacketPlayer(boolean isOnGround)
    {
        this.onGround = isOnGround;
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processPlayer(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.onGround = buf.readUnsignedByte() != 0;
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.onGround ? 1 : 0);
    }

    public double getPositionX()
    {
        return this.x;
    }

    public double getPositionY()
    {
        return this.y;
    }

    public double getPositionZ()
    {
        return this.z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setPositionX(double x) {
        this.x = x;
    }

    public void setPositionY(double y) {
        this.y = y;
    }

    public void setPositionZ(double z) {
        this.z = z;
    }

    public float getYaw()
    {
        return this.yaw;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isOnGround()
    {
        return this.onGround;
    }

    public boolean isMoving()
    {
        return this.moving;
    }

    public boolean getRotating()
    {
        return this.rotating;
    }

    public void setMoving(boolean isMoving)
    {
        this.moving = isMoving;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public static class C04PacketPlayerPosition extends C03PacketPlayer
    {
        public C04PacketPlayerPosition()
        {
            this.moving = true;
        }

        public C04PacketPlayerPosition(double posX, double posY, double posZ, boolean isOnGround)
        {
            this.x = posX;
            this.y = posY;
            this.z = posZ;
            this.onGround = isOnGround;
            this.moving = true;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            this.x = buf.readDouble();
            this.y = buf.readDouble();
            this.z = buf.readDouble();
            super.readPacketData(buf);
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            buf.writeDouble(this.x);
            buf.writeDouble(this.y);
            buf.writeDouble(this.z);
            super.writePacketData(buf);
        }
    }

    public static class C05PacketPlayerLook extends C03PacketPlayer
    {
        public C05PacketPlayerLook()
        {
            this.rotating = true;
        }

        public C05PacketPlayerLook(float playerYaw, float playerPitch, boolean isOnGround)
        {
            this.yaw = playerYaw;
            this.pitch = playerPitch;
            this.onGround = isOnGround;
            this.rotating = true;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            this.yaw = buf.readFloat();
            this.pitch = buf.readFloat();
            super.readPacketData(buf);
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            buf.writeFloat(this.yaw);
            buf.writeFloat(this.pitch);
            super.writePacketData(buf);
        }
    }

    public static class C06PacketPlayerPosLook extends C03PacketPlayer
    {
        public C06PacketPlayerPosLook()
        {
            this.moving = true;
            this.rotating = true;
        }

        public C06PacketPlayerPosLook(double playerX, double playerY, double playerZ, float playerYaw, float playerPitch, boolean playerIsOnGround)
        {
            this.x = playerX;
            this.y = playerY;
            this.z = playerZ;
            this.yaw = playerYaw;
            this.pitch = playerPitch;
            this.onGround = playerIsOnGround;
            this.rotating = true;
            this.moving = true;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            this.x = buf.readDouble();
            this.y = buf.readDouble();
            this.z = buf.readDouble();
            this.yaw = buf.readFloat();
            this.pitch = buf.readFloat();
            super.readPacketData(buf);
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            buf.writeDouble(this.x);
            buf.writeDouble(this.y);
            buf.writeDouble(this.z);
            buf.writeFloat(this.yaw);
            buf.writeFloat(this.pitch);
            super.writePacketData(buf);
        }
    }
}
