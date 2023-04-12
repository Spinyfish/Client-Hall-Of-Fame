package xyz.unnamed.anticheat.model.packet.nms;

import io.netty.buffer.ByteBuf;
import xyz.unnamed.anticheat.utilities.nms.NMSUtil;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

public interface UWrappedPacketDataSerializer {
    static Class<? extends UWrappedPacketDataSerializer> getVersionSerializer() throws ClassNotFoundException {
        return (Class<? extends UWrappedPacketDataSerializer>) Class.forName("xyz.unnamed.anticheat.model.packet.nms.impl.WrappedPacketDataSerializer_" + NMSUtil.NMS_VERSION_SUFFIX);
    }

    /***
     * Sets the buffer to be serialized to
     * @param buf Buffer to be serialized to
     */
    void setBuffer(ByteBuf buf);

    /***
     * Serializes packet to this object
     * @param packet Packet to serialize
     */
    void serializePacket(Object packet);

    /***
     * Reads a short from the ByteBuf
     * @return Short read from ByteBuf
     */
    short readShort();

    /***
     * Reads a boolean from the ByteBuf
     * @return Boolean read from ByteBuf
     */
    boolean readBoolean();

    /***
     * Reads a byte from the ByteBuf
     * @return Byte read from ByteBuf
     */
    byte readByte();

    /***
     * Reads an unsigned byte from the ByteBuf
     * @return Unsigned byte read from ByteBuf (as a short)
     */
    short readUnsignedByte();

    /***
     * Reads a float from the ByteBuf
     * @return Float read from ByteBuf
     */
    float readFloat();

    /***
     * Reads a double from the ByteBuf
     * @return Double read from ByteBuf
     */
    double readDouble();

    /***
     * Reads a byte array from the ByteBuf
     * @return Byte array read from ByteBuf
     */
    byte[] readByteArray();

    /***
     * Reads a string from the ByteBuf
     * @param maxLength Maximum length of the string
     * @return String read from ByteBuf
     */
    String readString(int maxLength);

    /***
     * Reads an enum from the ByteBuf
     * @param oclass Class of the enum
     */
    <T extends Enum<T>> T readEnum(Class<T> oclass);

    /***
     * Reads a VarInt from the ByteBuf (specified by MC protocol)
     * @return VarInt read from ByteBuf (as an int)
     */
    int readVarInt();
}
