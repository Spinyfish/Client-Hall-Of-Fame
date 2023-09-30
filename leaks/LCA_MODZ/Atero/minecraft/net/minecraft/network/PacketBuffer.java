package net.minecraft.network;

import io.netty.buffer.*;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public class PacketBuffer extends ByteBuf {
  //das ist so dumm xd
  //weird in eclispe geht ja alles
  private final ByteBuf buf;
  
  public PacketBuffer(ByteBuf paramByteBuf) {
    this.buf = paramByteBuf;
  }
  //darf ich kurtz was testen? XD
  //
  public static int getVarIntSize(int paramInt) {
    for (byte b = 1; b < 5; b++) {
      if ((paramInt & -1 << b * 7) == 0)
        return b; 
    } 
    return 5;
  }
  
  public PacketBuffer writeByteArray(byte[] paramArrayOfbyte) {
    writeVarIntToBuffer(paramArrayOfbyte.length);
    writeBytes(paramArrayOfbyte);
    return this;
  }
  
  public byte[] readByteArray() {
    return readByteArray(readableBytes());
  }
  
  public byte[] readByteArray(int paramInt) {
    int i = readVarIntFromBuffer();
    if (i > paramInt)
      throw new DecoderException("ByteArray with size " + i + " is bigger than allowed " + paramInt); 
    byte[] arrayOfByte = new byte[i];
    readBytes(arrayOfByte);
    return arrayOfByte;
  }
  
  public PacketBuffer writeVarIntArray(int[] paramArrayOfint) {
    writeVarIntToBuffer(paramArrayOfint.length);
    for (int i : paramArrayOfint)
      writeVarIntToBuffer(i); 
    return this;
  }
  
  public int[] readVarIntArray() {
    return readVarIntArray(readableBytes());
  }
  
  public int[] readVarIntArray(int paramInt) {
    int i = readVarIntFromBuffer();
    if (i > paramInt)
      throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + paramInt); 
    int[] arrayOfInt = new int[i];
    for (byte b = 0; b < arrayOfInt.length; b++)
      arrayOfInt[b] = readVarIntFromBuffer(); 
    return arrayOfInt;
  }
  
  public PacketBuffer writeLongArray(long[] paramArrayOflong) {
    writeVarIntToBuffer(paramArrayOflong.length);
    for (long l : paramArrayOflong)
      writeLong(l); 
    return this;
  }
  
  public long[] readLongArray(long[] paramArrayOflong) {
    return readLongArray(paramArrayOflong, readableBytes() / 8);
  }
  
  public long[] readLongArray(long[] paramArrayOflong, int paramInt) {
    int i = readVarIntFromBuffer();
    if (paramArrayOflong == null || paramArrayOflong.length != i) {
      if (i > paramInt)
        throw new DecoderException("LongArray with size " + i + " is bigger than allowed " + paramInt); 
      paramArrayOflong = new long[i];
    } 
    for (byte b = 0; b < paramArrayOflong.length; b++)
      paramArrayOflong[b] = readLong(); 
    return paramArrayOflong;
  }
  
  public BlockPos readBlockPos() {
    return BlockPos.fromLong(readLong());
  }
  
  public PacketBuffer writeBlockPos(BlockPos paramBlockPos) {
    writeLong(paramBlockPos.toLong());
    return this;
  }
  
  public IChatComponent readChatComponent() throws IOException {
    return IChatComponent.Serializer.jsonToComponent(readStringFromBuffer(32767));
  }
  
  public PacketBuffer writeChatComponent(IChatComponent paramIChatComponent) {
    return writeString(IChatComponent.Serializer.componentToJson(paramIChatComponent));
  }
  
  public <T extends Enum<T>> T readEnumValue(Class<T> paramClass) {
    return (T)((Enum[])paramClass.getEnumConstants())[readVarIntFromBuffer()];
  }
  
  public PacketBuffer writeEnumValue(Enum<?> paramEnum) {
    return writeVarIntToBuffer(paramEnum.ordinal());
  }
  
  public int readVarIntFromBuffer() {
    int i = 0;
    byte b = 0;
    while (true) {
      byte b1 = readByte();
      i |= (b1 & Byte.MAX_VALUE) << b++ * 7;
      if (b > 5)
        throw new RuntimeException("VarInt too big"); 
      if ((b1 & 0x80) != 128)
        return i; 
    } 
  }
  
  public long readVarLong() {
    long l = 0L;
    byte b = 0;
    while (true) {
      byte b1 = readByte();
      l |= (b1 & Byte.MAX_VALUE) << b++ * 7;
      if (b > 10)
        throw new RuntimeException("VarLong too big"); 
      if ((b1 & 0x80) != 128)
        return l; 
    } 
  }
  
  public PacketBuffer writeUuid(UUID paramUUID) {
    writeLong(paramUUID.getMostSignificantBits());
    writeLong(paramUUID.getLeastSignificantBits());
    return this;
  }
  
  public UUID readUuid() {
    return new UUID(readLong(), readLong());
  }
  
  public PacketBuffer writeVarIntToBuffer(int paramInt) {
    while ((paramInt & 0xFFFFFF80) != 0) {
      writeByte(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    } 
    writeByte(paramInt);
    return this;
  }
  
  public PacketBuffer writeVarLong(long paramLong) {
    while ((paramLong & 0xFFFFFFFFFFFFFF80L) != 0L) {
      writeByte((int)(paramLong & 0x7FL) | 0x80);
      paramLong >>>= 7L;
    } 
    writeByte((int)paramLong);
    return this;
  }
  
  public PacketBuffer writeNBTTagCompoundToBuffer(NBTTagCompound paramNBTTagCompound) {
    if (paramNBTTagCompound == null) {
      writeByte(0);
    } else {
      try {
        CompressedStreamTools.write(paramNBTTagCompound, (DataOutput)new ByteBufOutputStream(this));
      } catch (IOException iOException) {
        throw new EncoderException(iOException);
      } 
    } 
    return this;
  }
  
  public NBTTagCompound readNBTTagCompoundFromBuffer() throws IOException {
    int i = readerIndex();
    byte b = readByte();
    if (b == 0)
      return null; 
    readerIndex(i);
    try {
      return CompressedStreamTools.read((DataInput)new ByteBufInputStream(this), new NBTSizeTracker(2097152L));
    } catch (IOException iOException) {
      throw new EncoderException(iOException);
    } 
  }
  
  public PacketBuffer writeItemStackToBuffer(ItemStack paramItemStack) {
    if (paramItemStack == null) {
      writeShort(-1);
    } else {
      writeShort(Item.getIdFromItem(paramItemStack.getItem()));
      writeByte(paramItemStack.stackSize);
      writeShort(paramItemStack.getMetadata());
      NBTTagCompound nBTTagCompound = null;
      if (paramItemStack.getItem().isDamageable() || paramItemStack.getItem().getShareTag())
        nBTTagCompound = paramItemStack.getTagCompound(); 
      writeNBTTagCompoundToBuffer(nBTTagCompound);
    } 
    return this;
  }
  
  public ItemStack readItemStackFromBuffer() throws IOException {
    short s1 = readShort();
    if (s1 < 0)
      return null; 
    byte b = readByte();
    short s2 = readShort();
    ItemStack itemStack = new ItemStack(Item.getItemById(s1), b, s2);
    try {
      itemStack.setTagCompound(readNBTTagCompoundFromBuffer());
    } catch (Exception exception) {}
    return itemStack;
  }
  
  public String readStringFromBuffer(int paramInt) {
    int i = readVarIntFromBuffer();
    if (i > paramInt * 4)
      throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + (paramInt * 4) + ")"); 
    if (i < 0)
      throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!"); 
    String str = toString(readerIndex(), i, StandardCharsets.UTF_8);
    readerIndex(readerIndex() + i);
    if (str.length() > paramInt)
      throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + paramInt + ")"); 
    return str;
  }
  
  public PacketBuffer writeString(String paramString) {
    byte[] arrayOfByte = paramString.getBytes(StandardCharsets.UTF_8);
    if (arrayOfByte.length > 32767)
      throw new EncoderException("String too big (was " + arrayOfByte.length + " bytes encoded, max " + paramString + ")"); 
    writeVarIntToBuffer(arrayOfByte.length);
    writeBytes(arrayOfByte);
    return this;
  }
  
  public ResourceLocation func_192575_l() {
    return new ResourceLocation(readStringFromBuffer(32767));
  }
  
  public PacketBuffer func_192572_a(ResourceLocation paramResourceLocation) {
    writeString(paramResourceLocation.toString());
    return this;
  }
  
  public Date func_192573_m() {
    return new Date(readLong());
  }
  
  public PacketBuffer func_192574_a(Date paramDate) {
    writeLong(paramDate.getTime());
    return this;
  }
  
  public int capacity() {
    return this.buf.capacity();
  }
  
  public ByteBuf capacity(int paramInt) {
    return this.buf.capacity(paramInt);
  }
  
  public int maxCapacity() {
    return this.buf.maxCapacity();
  }
  
  public ByteBufAllocator alloc() {
    return this.buf.alloc();
  }
  
  public ByteOrder order() {
    return this.buf.order();
  }
  
  public ByteBuf order(ByteOrder paramByteOrder) {
    return this.buf.order(paramByteOrder);
  }
  
  public ByteBuf unwrap() {
    return this.buf.unwrap();
  }
  
  public boolean isDirect() {
    return this.buf.isDirect();
  }
  
  public boolean isReadOnly() {
    return this.buf.isReadOnly();
  }
  // in eclispe geht alles da habe ich netty ausgetauscht und alles hat workded dann why net auch in inteliji? XD
  public ByteBuf asReadOnly() {
    return this.buf.asReadOnly();
  }
  
  public int readerIndex() {
    return this.buf.readerIndex();
  }
  
  public ByteBuf readerIndex(int paramInt) {
    return this.buf.readerIndex(paramInt);
  }
  
  public int writerIndex() {
    return this.buf.writerIndex();
  }
  
  public ByteBuf writerIndex(int paramInt) {
    return this.buf.writerIndex(paramInt);
  }
  
  public ByteBuf setIndex(int paramInt1, int paramInt2) {
    return this.buf.setIndex(paramInt1, paramInt2);
  }
  
  public int readableBytes() {
    return this.buf.readableBytes();
  }
  
  public int writableBytes() {
    return this.buf.writableBytes();
  }
  
  public int maxWritableBytes() {
    return this.buf.maxWritableBytes();
  }
  
  public boolean isReadable() {
    return this.buf.isReadable();
  }
  
  public boolean isReadable(int paramInt) {
    return this.buf.isReadable(paramInt);
  }
  
  public boolean isWritable() {
    return this.buf.isWritable();
  }
  
  public boolean isWritable(int paramInt) {
    return this.buf.isWritable(paramInt);
  }
  
  public ByteBuf clear() {
    return this.buf.clear();
  }
  
  public ByteBuf markReaderIndex() {
    return this.buf.markReaderIndex();
  }
  
  public ByteBuf resetReaderIndex() {
    return this.buf.resetReaderIndex();
  }
  
  public ByteBuf markWriterIndex() {
    return this.buf.markWriterIndex();
  }
  
  public ByteBuf resetWriterIndex() {
    return this.buf.resetWriterIndex();
  }
  
  public ByteBuf discardReadBytes() {
    return this.buf.discardReadBytes();
  }
  
  public ByteBuf discardSomeReadBytes() {
    return this.buf.discardSomeReadBytes();
  }
  
  public ByteBuf ensureWritable(int paramInt) {
    return this.buf.ensureWritable(paramInt);
  }
  
  public int ensureWritable(int paramInt, boolean paramBoolean) {
    return this.buf.ensureWritable(paramInt, paramBoolean);
  }
  
  public boolean getBoolean(int paramInt) {
    return this.buf.getBoolean(paramInt);
  }
  
  public byte getByte(int paramInt) {
    return this.buf.getByte(paramInt);
  }
  
  public short getUnsignedByte(int paramInt) {
    return this.buf.getUnsignedByte(paramInt);
  }
  
  public short getShort(int paramInt) {
    return this.buf.getShort(paramInt);
  }
  
  public short getShortLE(int paramInt) {
    return this.buf.getShortLE(paramInt);
  }
  
  public int getUnsignedShort(int paramInt) {
    return this.buf.getUnsignedShort(paramInt);
  }
  
  public int getUnsignedShortLE(int paramInt) {
    return this.buf.getUnsignedShortLE(paramInt);
  }
  
  public int getMedium(int paramInt) {
    return this.buf.getMedium(paramInt);
  }
  
  public int getMediumLE(int paramInt) {
    return this.buf.getMediumLE(paramInt);
  }
  
  public int getUnsignedMedium(int paramInt) {
    return this.buf.getUnsignedMedium(paramInt);
  }
  
  public int getUnsignedMediumLE(int paramInt) {
    return this.buf.getUnsignedMediumLE(paramInt);
  }
  
  public int getInt(int paramInt) {
    return this.buf.getInt(paramInt);
  }
  
  public int getIntLE(int paramInt) {
    return this.buf.getIntLE(paramInt);
  }
  
  public long getUnsignedInt(int paramInt) {
    return this.buf.getUnsignedInt(paramInt);
  }
  
  public long getUnsignedIntLE(int paramInt) {
    return this.buf.getUnsignedIntLE(paramInt);
  }
  
  public long getLong(int paramInt) {
    return this.buf.getLong(paramInt);
  }
  
  public long getLongLE(int paramInt) {
    return this.buf.getLongLE(paramInt);
  }
  
  public char getChar(int paramInt) {
    return this.buf.getChar(paramInt);
  }
  
  public float getFloat(int paramInt) {
    return this.buf.getFloat(paramInt);
  }
  
  public double getDouble(int paramInt) {
    return this.buf.getDouble(paramInt);
  }
  
  public ByteBuf getBytes(int paramInt, ByteBuf paramByteBuf) {
    return this.buf.getBytes(paramInt, paramByteBuf);
  }
  
  public ByteBuf getBytes(int paramInt1, ByteBuf paramByteBuf, int paramInt2) {
    return this.buf.getBytes(paramInt1, paramByteBuf, paramInt2);
  }
  
  public ByteBuf getBytes(int paramInt1, ByteBuf paramByteBuf, int paramInt2, int paramInt3) {
    return this.buf.getBytes(paramInt1, paramByteBuf, paramInt2, paramInt3);
  }
  
  public ByteBuf getBytes(int paramInt, byte[] paramArrayOfbyte) {
    return this.buf.getBytes(paramInt, paramArrayOfbyte);
  }
  
  public ByteBuf getBytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) {
    return this.buf.getBytes(paramInt1, paramArrayOfbyte, paramInt2, paramInt3);
  }
  
  public ByteBuf getBytes(int paramInt, ByteBuffer paramByteBuffer) {
    return this.buf.getBytes(paramInt, paramByteBuffer);
  }
  
  public ByteBuf getBytes(int paramInt1, OutputStream paramOutputStream, int paramInt2) throws IOException {
    return this.buf.getBytes(paramInt1, paramOutputStream, paramInt2);
  }
  
  public int getBytes(int paramInt1, GatheringByteChannel paramGatheringByteChannel, int paramInt2) throws IOException {
    return this.buf.getBytes(paramInt1, paramGatheringByteChannel, paramInt2);
  }
  
  public int getBytes(int paramInt1, FileChannel paramFileChannel, long paramLong, int paramInt2) throws IOException {
    return this.buf.getBytes(paramInt1, paramFileChannel, paramLong, paramInt2);
  }
  
  public CharSequence getCharSequence(int paramInt1, int paramInt2, Charset paramCharset) {
    return this.buf.getCharSequence(paramInt1, paramInt2, paramCharset);
  }
  
  public ByteBuf setBoolean(int paramInt, boolean paramBoolean) {
    return this.buf.setBoolean(paramInt, paramBoolean);
  }
  
  public ByteBuf setByte(int paramInt1, int paramInt2) {
    return this.buf.setByte(paramInt1, paramInt2);
  }
  
  public ByteBuf setShort(int paramInt1, int paramInt2) {
    return this.buf.setShort(paramInt1, paramInt2);
  }
  
  public ByteBuf setShortLE(int paramInt1, int paramInt2) {
    return this.buf.setShortLE(paramInt1, paramInt2);
  }
  
  public ByteBuf setMedium(int paramInt1, int paramInt2) {
    return this.buf.setMedium(paramInt1, paramInt2);
  }
  
  public ByteBuf setMediumLE(int paramInt1, int paramInt2) {
    return this.buf.setMediumLE(paramInt1, paramInt2);
  }
  
  public ByteBuf setInt(int paramInt1, int paramInt2) {
    return this.buf.setInt(paramInt1, paramInt2);
  }
  
  public ByteBuf setIntLE(int paramInt1, int paramInt2) {
    return this.buf.setIntLE(paramInt1, paramInt2);
  }
  
  public ByteBuf setLong(int paramInt, long paramLong) {
    return this.buf.setLong(paramInt, paramLong);
  }
  
  public ByteBuf setLongLE(int paramInt, long paramLong) {
    return this.buf.setLongLE(paramInt, paramLong);
  }
  
  public ByteBuf setChar(int paramInt1, int paramInt2) {
    return this.buf.setChar(paramInt1, paramInt2);
  }
  
  public ByteBuf setFloat(int paramInt, float paramFloat) {
    return this.buf.setFloat(paramInt, paramFloat);
  }
  
  public ByteBuf setDouble(int paramInt, double paramDouble) {
    return this.buf.setDouble(paramInt, paramDouble);
  }
  
  public ByteBuf setBytes(int paramInt, ByteBuf paramByteBuf) {
    return this.buf.setBytes(paramInt, paramByteBuf);
  }
  
  public ByteBuf setBytes(int paramInt1, ByteBuf paramByteBuf, int paramInt2) {
    return this.buf.setBytes(paramInt1, paramByteBuf, paramInt2);
  }
  
  public ByteBuf setBytes(int paramInt1, ByteBuf paramByteBuf, int paramInt2, int paramInt3) {
    return this.buf.setBytes(paramInt1, paramByteBuf, paramInt2, paramInt3);
  }
  
  public ByteBuf setBytes(int paramInt, byte[] paramArrayOfbyte) {
    return this.buf.setBytes(paramInt, paramArrayOfbyte);
  }
  
  public ByteBuf setBytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) {
    return this.buf.setBytes(paramInt1, paramArrayOfbyte, paramInt2, paramInt3);
  }
  
  public ByteBuf setBytes(int paramInt, ByteBuffer paramByteBuffer) {
    return this.buf.setBytes(paramInt, paramByteBuffer);
  }
  
  public int setBytes(int paramInt1, InputStream paramInputStream, int paramInt2) throws IOException {
    return this.buf.setBytes(paramInt1, paramInputStream, paramInt2);
  }
  
  public int setBytes(int paramInt1, ScatteringByteChannel paramScatteringByteChannel, int paramInt2) throws IOException {
    return this.buf.setBytes(paramInt1, paramScatteringByteChannel, paramInt2);
  }
  
  public int setBytes(int paramInt1, FileChannel paramFileChannel, long paramLong, int paramInt2) throws IOException {
    return this.buf.setBytes(paramInt1, paramFileChannel, paramLong, paramInt2);
  }
  
  public ByteBuf setZero(int paramInt1, int paramInt2) {
    return this.buf.setZero(paramInt1, paramInt2);
  }
  
  public int setCharSequence(int paramInt, CharSequence paramCharSequence, Charset paramCharset) {
    return this.buf.setCharSequence(paramInt, paramCharSequence, paramCharset);
  }
  
  public boolean readBoolean() {
    return this.buf.readBoolean();
  }
  
  public byte readByte() {
    return this.buf.readByte();
  }
  
  public short readUnsignedByte() {
    return this.buf.readUnsignedByte();
  }
  
  public short readShort() {
    return this.buf.readShort();
  }
  
  public short readShortLE() {
    return this.buf.readShortLE();
  }
  
  public int readUnsignedShort() {
    return this.buf.readUnsignedShort();
  }
  
  public int readUnsignedShortLE() {
    return this.buf.readUnsignedShortLE();
  }
  
  public int readMedium() {
    return this.buf.readMedium();
  }
  
  public int readMediumLE() {
    return this.buf.readMediumLE();
  }
  
  public int readUnsignedMedium() {
    return this.buf.readUnsignedMedium();
  }
  
  public int readUnsignedMediumLE() {
    return this.buf.readUnsignedMediumLE();
  }
  
  public int readInt() {
    return this.buf.readInt();
  }
  
  public int readIntLE() {
    return this.buf.readIntLE();
  }
  
  public long readUnsignedInt() {
    return this.buf.readUnsignedInt();
  }
  
  public long readUnsignedIntLE() {
    return this.buf.readUnsignedIntLE();
  }
  
  public long readLong() {
    return this.buf.readLong();
  }
  
  public long readLongLE() {
    return this.buf.readLongLE();
  }
  
  public char readChar() {
    return this.buf.readChar();
  }
  
  public float readFloat() {
    return this.buf.readFloat();
  }
  
  public double readDouble() {
    return this.buf.readDouble();
  }
  
  public ByteBuf readBytes(int paramInt) {
    return this.buf.readBytes(paramInt);
  }
  
  public ByteBuf readSlice(int paramInt) {
    return this.buf.readSlice(paramInt);
  }
  
  public ByteBuf readRetainedSlice(int paramInt) {
    return this.buf.readRetainedSlice(paramInt);
  }
  
  public ByteBuf readBytes(ByteBuf paramByteBuf) {
    return this.buf.readBytes(paramByteBuf);
  }
  
  public ByteBuf readBytes(ByteBuf paramByteBuf, int paramInt) {
    return this.buf.readBytes(paramByteBuf, paramInt);
  }
  
  public ByteBuf readBytes(ByteBuf paramByteBuf, int paramInt1, int paramInt2) {
    return this.buf.readBytes(paramByteBuf, paramInt1, paramInt2);
  }
  
  public ByteBuf readBytes(byte[] paramArrayOfbyte) {
    return this.buf.readBytes(paramArrayOfbyte);
  }
  
  public ByteBuf readBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    return this.buf.readBytes(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public ByteBuf readBytes(ByteBuffer paramByteBuffer) {
    return this.buf.readBytes(paramByteBuffer);
  }
  
  public ByteBuf readBytes(OutputStream paramOutputStream, int paramInt) throws IOException {
    return this.buf.readBytes(paramOutputStream, paramInt);
  }
  
  public int readBytes(GatheringByteChannel paramGatheringByteChannel, int paramInt) throws IOException {
    return this.buf.readBytes(paramGatheringByteChannel, paramInt);
  }
  
  public CharSequence readCharSequence(int paramInt, Charset paramCharset) {
    return this.buf.readCharSequence(paramInt, paramCharset);
  }
  
  public int readBytes(FileChannel paramFileChannel, long paramLong, int paramInt) throws IOException {
    return this.buf.readBytes(paramFileChannel, paramLong, paramInt);
  }
  
  public ByteBuf skipBytes(int paramInt) {
    return this.buf.skipBytes(paramInt);
  }
  
  public ByteBuf writeBoolean(boolean paramBoolean) {
    return this.buf.writeBoolean(paramBoolean);
  }
  
  public ByteBuf writeByte(int paramInt) {
    return this.buf.writeByte(paramInt);
  }
  
  public ByteBuf writeShort(int paramInt) {
    return this.buf.writeShort(paramInt);
  }
  
  public ByteBuf writeShortLE(int paramInt) {
    return this.buf.writeShortLE(paramInt);
  }
  
  public ByteBuf writeMedium(int paramInt) {
    return this.buf.writeMedium(paramInt);
  }
  
  public ByteBuf writeMediumLE(int paramInt) {
    return this.buf.writeMediumLE(paramInt);
  }
  
  public ByteBuf writeInt(int paramInt) {
    return this.buf.writeInt(paramInt);
  }
  
  public ByteBuf writeIntLE(int paramInt) {
    return this.buf.writeIntLE(paramInt);
  }
  
  public ByteBuf writeLong(long paramLong) {
    return this.buf.writeLong(paramLong);
  }
  
  public ByteBuf writeLongLE(long paramLong) {
    return this.buf.writeLongLE(paramLong);
  }
  
  public ByteBuf writeChar(int paramInt) {
    return this.buf.writeChar(paramInt);
  }
  
  public ByteBuf writeFloat(float paramFloat) {
    return this.buf.writeFloat(paramFloat);
  }
  
  public ByteBuf writeDouble(double paramDouble) {
    return this.buf.writeDouble(paramDouble);
  }
  
  public ByteBuf writeBytes(ByteBuf paramByteBuf) {
    return this.buf.writeBytes(paramByteBuf);
  }
  
  public ByteBuf writeBytes(ByteBuf paramByteBuf, int paramInt) {
    return this.buf.writeBytes(paramByteBuf, paramInt);
  }
  
  public ByteBuf writeBytes(ByteBuf paramByteBuf, int paramInt1, int paramInt2) {
    return this.buf.writeBytes(paramByteBuf, paramInt1, paramInt2);
  }
  
  public ByteBuf writeBytes(byte[] paramArrayOfbyte) {
    return this.buf.writeBytes(paramArrayOfbyte);
  }
  
  public ByteBuf writeBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    return this.buf.writeBytes(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public ByteBuf writeBytes(ByteBuffer paramByteBuffer) {
    return this.buf.writeBytes(paramByteBuffer);
  }
  
  public int writeBytes(InputStream paramInputStream, int paramInt) throws IOException {
    return this.buf.writeBytes(paramInputStream, paramInt);
  }
  
  public int writeBytes(ScatteringByteChannel paramScatteringByteChannel, int paramInt) throws IOException {
    return this.buf.writeBytes(paramScatteringByteChannel, paramInt);
  }
  
  public int writeBytes(FileChannel paramFileChannel, long paramLong, int paramInt) throws IOException {
    return this.buf.writeBytes(paramFileChannel, paramLong, paramInt);
  }
  
  public ByteBuf writeZero(int paramInt) {
    return this.buf.writeZero(paramInt);
  }
  
  public int writeCharSequence(CharSequence paramCharSequence, Charset paramCharset) {
    return this.buf.writeCharSequence(paramCharSequence, paramCharset);
  }
  
  public int indexOf(int paramInt1, int paramInt2, byte paramByte) {
    return this.buf.indexOf(paramInt1, paramInt2, paramByte);
  }
  
  public int bytesBefore(byte paramByte) {
    return this.buf.bytesBefore(paramByte);
  }
  
  public int bytesBefore(int paramInt, byte paramByte) {
    return this.buf.bytesBefore(paramInt, paramByte);
  }
  
  public int bytesBefore(int paramInt1, int paramInt2, byte paramByte) {
    return this.buf.bytesBefore(paramInt1, paramInt2, paramByte);
  }
  
  public int forEachByte(ByteProcessor paramByteProcessor) {
    return this.buf.forEachByte(paramByteProcessor);
  }
  
  public int forEachByte(int paramInt1, int paramInt2, ByteProcessor paramByteProcessor) {
    return this.buf.forEachByte(paramInt1, paramInt2, paramByteProcessor);
  }

  public int forEachByteDesc(ByteProcessor paramByteProcessor) {
    return this.buf.forEachByteDesc(paramByteProcessor);
  }

//  public int forEachByteDesc(int paramInt1, int paramInt2, ByteProcessor paramByteProcessor) {
//    return this.buf.forEachByteDesc(paramInt1, paramInt2, paramByteProcessor);
//  }


  @Override
  public int forEachByteDesc(int index, int length, ByteProcessor processor) {
    return this.buf.forEachByteDesc(index, length, processor);
  }

  public ByteBuf copy() {
    return this.buf.copy();
  }
  
  public ByteBuf copy(int paramInt1, int paramInt2) {
    return this.buf.copy(paramInt1, paramInt2);
  }
  
  public ByteBuf slice() {
    return this.buf.slice();
  }
  
  public ByteBuf retainedSlice() {
    return this.buf.retainedSlice();
  }
  
  public ByteBuf slice(int paramInt1, int paramInt2) {
    return this.buf.slice(paramInt1, paramInt2);
  }
  
  public ByteBuf retainedSlice(int paramInt1, int paramInt2) {
    return this.buf.retainedSlice(paramInt1, paramInt2);
  }
  
  public ByteBuf duplicate() {
    return this.buf.duplicate();
  }
  
  public ByteBuf retainedDuplicate() {
    return this.buf.retainedDuplicate();
  }
  
  public int nioBufferCount() {
    return this.buf.nioBufferCount();
  }
  
  public ByteBuffer nioBuffer() {
    return this.buf.nioBuffer();
  }
  
  public ByteBuffer nioBuffer(int paramInt1, int paramInt2) {
    return this.buf.nioBuffer(paramInt1, paramInt2);
  }
  
  public ByteBuffer internalNioBuffer(int paramInt1, int paramInt2) {
    return this.buf.internalNioBuffer(paramInt1, paramInt2);
  }
  
  public ByteBuffer[] nioBuffers() {
    return this.buf.nioBuffers();
  }
  
  public ByteBuffer[] nioBuffers(int paramInt1, int paramInt2) {
    return this.buf.nioBuffers(paramInt1, paramInt2);
  }
  
  public boolean hasArray() {
    return this.buf.hasArray();
  }
  
  public byte[] array() {
    return this.buf.array();
  }
  
  public int arrayOffset() {
    return this.buf.arrayOffset();
  }
  
  public boolean hasMemoryAddress() {
    return this.buf.hasMemoryAddress();
  }
  
  public long memoryAddress() {
    return this.buf.memoryAddress();
  }
  
  public String toString(Charset paramCharset) {
    return this.buf.toString(paramCharset);
  }
  
  public String toString(int paramInt1, int paramInt2, Charset paramCharset) {
    return this.buf.toString(paramInt1, paramInt2, paramCharset);
  }
  
  public int hashCode() {
    return this.buf.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    return this.buf.equals(paramObject);
  }
  
  public int compareTo(ByteBuf paramByteBuf) {
    return this.buf.compareTo(paramByteBuf);
  }
  
  public String toString() {
    return this.buf.toString();
  }
  
  public ByteBuf retain(int paramInt) {
    return this.buf.retain(paramInt);
  }
  
  public ByteBuf retain() {
    return this.buf.retain();
  }
  
  public ByteBuf touch() {
    return this.buf.touch();
  }
  
  public ByteBuf touch(Object paramObject) {
    return this.buf.touch(paramObject);
  }
  
  public int refCnt() {
    return this.buf.refCnt();
  }
  
  public boolean release() {
    return this.buf.release();
  }
  
  public boolean release(int paramInt) {
    return this.buf.release(paramInt);
  }
}
