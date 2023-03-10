/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class S21PacketChunkData
implements Packet<INetHandlerPlayClient> {
    private int chunkX;
    private int chunkZ;
    private Extracted extractedData;
    private boolean field_149279_g;

    public S21PacketChunkData() {
    }

    public S21PacketChunkData(Chunk chunkIn, boolean p_i45196_2_, int p_i45196_3_) {
        this.chunkX = chunkIn.xPosition;
        this.chunkZ = chunkIn.zPosition;
        this.field_149279_g = p_i45196_2_;
        this.extractedData = S21PacketChunkData.func_179756_a(chunkIn, p_i45196_2_, !chunkIn.getWorld().provider.getHasNoSky(), p_i45196_3_);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();
        this.field_149279_g = buf.readBoolean();
        this.extractedData = new Extracted();
        this.extractedData.dataSize = buf.readShort();
        this.extractedData.data = buf.readByteArray();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);
        buf.writeBoolean(this.field_149279_g);
        buf.writeShort((short)(this.extractedData.dataSize & 0xFFFF));
        buf.writeByteArray(this.extractedData.data);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleChunkData(this);
    }

    public byte[] func_149272_d() {
        return this.extractedData.data;
    }

    protected static int func_180737_a(int p_180737_0_, boolean p_180737_1_, boolean p_180737_2_) {
        int i = p_180737_0_ * 2 * 16 * 16 * 16;
        int j = p_180737_0_ * 16 * 16 * 16 / 2;
        int k = p_180737_1_ ? p_180737_0_ * 16 * 16 * 16 / 2 : 0;
        int l = p_180737_2_ ? 256 : 0;
        return i + j + k + l;
    }

    public static Extracted func_179756_a(Chunk p_179756_0_, boolean p_179756_1_, boolean p_179756_2_, int p_179756_3_) {
        ExtendedBlockStorage[] aextendedblockstorage = p_179756_0_.getBlockStorageArray();
        Extracted s21packetchunkdata$extracted = new Extracted();
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < aextendedblockstorage.length; ++i) {
            ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[i];
            if (extendedblockstorage == null || p_179756_1_ && extendedblockstorage.isEmpty() || (p_179756_3_ & 1 << i) == 0) continue;
            s21packetchunkdata$extracted.dataSize |= 1 << i;
            list.add(extendedblockstorage);
        }
        s21packetchunkdata$extracted.data = new byte[S21PacketChunkData.func_180737_a(Integer.bitCount(s21packetchunkdata$extracted.dataSize), p_179756_2_, p_179756_1_)];
        int j = 0;
        for (ExtendedBlockStorage extendedblockstorage1 : list) {
            char[] achar;
            for (char c0 : achar = extendedblockstorage1.getData()) {
                s21packetchunkdata$extracted.data[j++] = (byte)(c0 & 0xFF);
                s21packetchunkdata$extracted.data[j++] = (byte)(c0 >> 8 & 0xFF);
            }
        }
        for (ExtendedBlockStorage extendedblockstorage2 : list) {
            j = S21PacketChunkData.func_179757_a(extendedblockstorage2.getBlocklightArray().getData(), s21packetchunkdata$extracted.data, j);
        }
        if (p_179756_2_) {
            for (ExtendedBlockStorage extendedblockstorage3 : list) {
                j = S21PacketChunkData.func_179757_a(extendedblockstorage3.getSkylightArray().getData(), s21packetchunkdata$extracted.data, j);
            }
        }
        if (p_179756_1_) {
            S21PacketChunkData.func_179757_a(p_179756_0_.getBiomeArray(), s21packetchunkdata$extracted.data, j);
        }
        return s21packetchunkdata$extracted;
    }

    private static int func_179757_a(byte[] p_179757_0_, byte[] p_179757_1_, int p_179757_2_) {
        System.arraycopy(p_179757_0_, 0, p_179757_1_, p_179757_2_, p_179757_0_.length);
        return p_179757_2_ + p_179757_0_.length;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public int getExtractedSize() {
        return this.extractedData.dataSize;
    }

    public boolean func_149274_i() {
        return this.field_149279_g;
    }

    public static class Extracted {
        public byte[] data;
        public int dataSize;
    }
}

