package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.border.WorldBorder;

public class S44PacketWorldBorder implements Packet
{
    private S44PacketWorldBorder.Action action;
    private int size;
    private double centerX;
    private double centerZ;
    private double targetSize;
    private double diameter;
    private long timeUntilTarget;
    private int warningTime;
    private int warningDistance;

    public S44PacketWorldBorder() {}

    public S44PacketWorldBorder(WorldBorder border, S44PacketWorldBorder.Action actionIn)
    {
        this.action = actionIn;
        this.centerX = border.getCenterX();
        this.centerZ = border.getCenterZ();
        this.diameter = border.getDiameter();
        this.targetSize = border.getTargetSize();
        this.timeUntilTarget = border.getTimeUntilTarget();
        this.size = border.getSize();
        this.warningDistance = border.getWarningDistance();
        this.warningTime = border.getWarningTime();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.action = (S44PacketWorldBorder.Action)buf.readEnumValue(S44PacketWorldBorder.Action.class);

        switch (S44PacketWorldBorder.SwitchAction.field_179947_a[this.action.ordinal()])
        {
            case 1:
                this.targetSize = buf.readDouble();
                break;

            case 2:
                this.diameter = buf.readDouble();
                this.targetSize = buf.readDouble();
                this.timeUntilTarget = buf.readVarLong();
                break;

            case 3:
                this.centerX = buf.readDouble();
                this.centerZ = buf.readDouble();
                break;

            case 4:
                this.warningDistance = buf.readVarIntFromBuffer();
                break;

            case 5:
                this.warningTime = buf.readVarIntFromBuffer();
                break;

            case 6:
                this.centerX = buf.readDouble();
                this.centerZ = buf.readDouble();
                this.diameter = buf.readDouble();
                this.targetSize = buf.readDouble();
                this.timeUntilTarget = buf.readVarLong();
                this.size = buf.readVarIntFromBuffer();
                this.warningDistance = buf.readVarIntFromBuffer();
                this.warningTime = buf.readVarIntFromBuffer();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.action);

        switch (S44PacketWorldBorder.SwitchAction.field_179947_a[this.action.ordinal()])
        {
            case 1:
                buf.writeDouble(this.targetSize);
                break;

            case 2:
                buf.writeDouble(this.diameter);
                buf.writeDouble(this.targetSize);
                buf.writeVarLong(this.timeUntilTarget);
                break;

            case 3:
                buf.writeDouble(this.centerX);
                buf.writeDouble(this.centerZ);
                break;

            case 4:
                buf.writeVarIntToBuffer(this.warningDistance);
                break;

            case 5:
                buf.writeVarIntToBuffer(this.warningTime);
                break;

            case 6:
                buf.writeDouble(this.centerX);
                buf.writeDouble(this.centerZ);
                buf.writeDouble(this.diameter);
                buf.writeDouble(this.targetSize);
                buf.writeVarLong(this.timeUntilTarget);
                buf.writeVarIntToBuffer(this.size);
                buf.writeVarIntToBuffer(this.warningDistance);
                buf.writeVarIntToBuffer(this.warningTime);
        }
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleWorldBorder(this);
    }

    public void func_179788_a(WorldBorder border)
    {
        switch (S44PacketWorldBorder.SwitchAction.field_179947_a[this.action.ordinal()])
        {
            case 1:
                border.setTransition(this.targetSize);
                break;

            case 2:
                border.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
                break;

            case 3:
                border.setCenter(this.centerX, this.centerZ);
                break;

            case 4:
                border.setWarningDistance(this.warningDistance);
                break;

            case 5:
                border.setWarningTime(this.warningTime);
                break;

            case 6:
                border.setCenter(this.centerX, this.centerZ);

                if (this.timeUntilTarget > 0L)
                {
                    border.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
                }
                else
                {
                    border.setTransition(this.targetSize);
                }

                border.setSize(this.size);
                border.setWarningDistance(this.warningDistance);
                border.setWarningTime(this.warningTime);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    public static enum Action
    {
        SET_SIZE("SET_SIZE", 0),
        LERP_SIZE("LERP_SIZE", 1),
        SET_CENTER("SET_CENTER", 2),
        INITIALIZE("INITIALIZE", 3),
        SET_WARNING_TIME("SET_WARNING_TIME", 4),
        SET_WARNING_BLOCKS("SET_WARNING_BLOCKS", 5);

        private Action(String p_i45961_1_, int p_i45961_2_) {}
    }

    static final class SwitchAction
    {
        static final int[] field_179947_a = new int[S44PacketWorldBorder.Action.values().length];

        static
        {
            try
            {
                field_179947_a[S44PacketWorldBorder.Action.SET_SIZE.ordinal()] = 1;
            }
            catch (NoSuchFieldError var6)
            {
                ;
            }

            try
            {
                field_179947_a[S44PacketWorldBorder.Action.LERP_SIZE.ordinal()] = 2;
            }
            catch (NoSuchFieldError var5)
            {
                ;
            }

            try
            {
                field_179947_a[S44PacketWorldBorder.Action.SET_CENTER.ordinal()] = 3;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                field_179947_a[S44PacketWorldBorder.Action.SET_WARNING_BLOCKS.ordinal()] = 4;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                field_179947_a[S44PacketWorldBorder.Action.SET_WARNING_TIME.ordinal()] = 5;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                field_179947_a[S44PacketWorldBorder.Action.INITIALIZE.ordinal()] = 6;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
