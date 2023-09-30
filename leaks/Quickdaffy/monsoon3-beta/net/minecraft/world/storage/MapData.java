/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec4b;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData
extends WorldSavedData {
    public int xCenter;
    public int zCenter;
    public byte dimension;
    public byte scale;
    public byte[] colors = new byte[16384];
    public List<MapInfo> playersArrayList = Lists.newArrayList();
    private Map<EntityPlayer, MapInfo> playersHashMap = Maps.newHashMap();
    public Map<String, Vec4b> playersVisibleOnMap = Maps.newLinkedHashMap();

    public MapData(String mapname) {
        super(mapname);
    }

    public void calculateMapCenter(double x, double z, int mapScale) {
        int i = 128 * (1 << mapScale);
        int j = MathHelper.floor_double((x + 64.0) / (double)i);
        int k = MathHelper.floor_double((z + 64.0) / (double)i);
        this.xCenter = j * i + i / 2 - 64;
        this.zCenter = k * i + i / 2 - 64;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.dimension = nbt.getByte("dimension");
        this.xCenter = nbt.getInteger("xCenter");
        this.zCenter = nbt.getInteger("zCenter");
        this.scale = nbt.getByte("scale");
        this.scale = (byte)MathHelper.clamp_int(this.scale, 0, 4);
        int i = nbt.getShort("width");
        int j = nbt.getShort("height");
        if (i == 128 && j == 128) {
            this.colors = nbt.getByteArray("colors");
        } else {
            byte[] abyte = nbt.getByteArray("colors");
            this.colors = new byte[16384];
            int k = (128 - i) / 2;
            int l = (128 - j) / 2;
            for (int i1 = 0; i1 < j; ++i1) {
                int j1 = i1 + l;
                if (j1 < 0 && j1 >= 128) continue;
                for (int k1 = 0; k1 < i; ++k1) {
                    int l1 = k1 + k;
                    if (l1 < 0 && l1 >= 128) continue;
                    this.colors[l1 + j1 * 128] = abyte[k1 + i1 * i];
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setByte("dimension", this.dimension);
        nbt.setInteger("xCenter", this.xCenter);
        nbt.setInteger("zCenter", this.zCenter);
        nbt.setByte("scale", this.scale);
        nbt.setShort("width", (short)128);
        nbt.setShort("height", (short)128);
        nbt.setByteArray("colors", this.colors);
    }

    public void updateVisiblePlayers(EntityPlayer player, ItemStack mapStack) {
        if (!this.playersHashMap.containsKey(player)) {
            MapInfo mapdata$mapinfo = new MapInfo(player);
            this.playersHashMap.put(player, mapdata$mapinfo);
            this.playersArrayList.add(mapdata$mapinfo);
        }
        if (!player.inventory.hasItemStack(mapStack)) {
            this.playersVisibleOnMap.remove(player.getCommandSenderName());
        }
        for (int i = 0; i < this.playersArrayList.size(); ++i) {
            MapInfo mapdata$mapinfo1 = this.playersArrayList.get(i);
            if (!mapdata$mapinfo1.entityplayerObj.isDead && (mapdata$mapinfo1.entityplayerObj.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame())) {
                if (mapStack.isOnItemFrame() || mapdata$mapinfo1.entityplayerObj.dimension != this.dimension) continue;
                this.updatePlayersVisibleOnMap(0, mapdata$mapinfo1.entityplayerObj.worldObj, mapdata$mapinfo1.entityplayerObj.getCommandSenderName(), mapdata$mapinfo1.entityplayerObj.posX, mapdata$mapinfo1.entityplayerObj.posZ, mapdata$mapinfo1.entityplayerObj.rotationYaw);
                continue;
            }
            this.playersHashMap.remove(mapdata$mapinfo1.entityplayerObj);
            this.playersArrayList.remove(mapdata$mapinfo1);
        }
        if (mapStack.isOnItemFrame()) {
            EntityItemFrame entityitemframe = mapStack.getItemFrame();
            BlockPos blockpos = entityitemframe.getHangingPosition();
            this.updatePlayersVisibleOnMap(1, player.worldObj, "frame-" + entityitemframe.getEntityId(), blockpos.getX(), blockpos.getZ(), entityitemframe.facingDirection.getHorizontalIndex() * 90);
        }
        if (mapStack.hasTagCompound() && mapStack.getTagCompound().hasKey("Decorations", 9)) {
            NBTTagList nbttaglist = mapStack.getTagCompound().getTagList("Decorations", 10);
            for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(j);
                if (this.playersVisibleOnMap.containsKey(nbttagcompound.getString("id"))) continue;
                this.updatePlayersVisibleOnMap(nbttagcompound.getByte("type"), player.worldObj, nbttagcompound.getString("id"), nbttagcompound.getDouble("x"), nbttagcompound.getDouble("z"), nbttagcompound.getDouble("rot"));
            }
        }
    }

    private void updatePlayersVisibleOnMap(int type, World worldIn, String entityIdentifier, double worldX, double worldZ, double rotation) {
        byte b2;
        int i = 1 << this.scale;
        float f = (float)(worldX - (double)this.xCenter) / (float)i;
        float f1 = (float)(worldZ - (double)this.zCenter) / (float)i;
        byte b0 = (byte)((double)(f * 2.0f) + 0.5);
        byte b1 = (byte)((double)(f1 * 2.0f) + 0.5);
        int j = 63;
        if (f >= (float)(-j) && f1 >= (float)(-j) && f <= (float)j && f1 <= (float)j) {
            b2 = (byte)((rotation += rotation < 0.0 ? -8.0 : 8.0) * 16.0 / 360.0);
            if (this.dimension < 0) {
                int k = (int)(worldIn.getWorldInfo().getWorldTime() / 10L);
                b2 = (byte)(k * k * 34187121 + k * 121 >> 15 & 0xF);
            }
        } else {
            if (Math.abs(f) >= 320.0f || Math.abs(f1) >= 320.0f) {
                this.playersVisibleOnMap.remove(entityIdentifier);
                return;
            }
            type = 6;
            b2 = 0;
            if (f <= (float)(-j)) {
                b0 = (byte)((double)(j * 2) + 2.5);
            }
            if (f1 <= (float)(-j)) {
                b1 = (byte)((double)(j * 2) + 2.5);
            }
            if (f >= (float)j) {
                b0 = (byte)(j * 2 + 1);
            }
            if (f1 >= (float)j) {
                b1 = (byte)(j * 2 + 1);
            }
        }
        this.playersVisibleOnMap.put(entityIdentifier, new Vec4b((byte)type, b0, b1, b2));
    }

    public Packet getMapPacket(ItemStack mapStack, World worldIn, EntityPlayer player) {
        MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
        return mapdata$mapinfo == null ? null : mapdata$mapinfo.getPacket(mapStack);
    }

    public void updateMapData(int x, int y) {
        super.markDirty();
        for (MapInfo mapdata$mapinfo : this.playersArrayList) {
            mapdata$mapinfo.update(x, y);
        }
    }

    public MapInfo getMapInfo(EntityPlayer player) {
        MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
        if (mapdata$mapinfo == null) {
            mapdata$mapinfo = new MapInfo(player);
            this.playersHashMap.put(player, mapdata$mapinfo);
            this.playersArrayList.add(mapdata$mapinfo);
        }
        return mapdata$mapinfo;
    }

    public class MapInfo {
        public final EntityPlayer entityplayerObj;
        private boolean field_176105_d = true;
        private int minX = 0;
        private int minY = 0;
        private int maxX = 127;
        private int maxY = 127;
        private int field_176109_i;
        public int field_82569_d;

        public MapInfo(EntityPlayer player) {
            this.entityplayerObj = player;
        }

        public Packet getPacket(ItemStack stack) {
            if (this.field_176105_d) {
                this.field_176105_d = false;
                return new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.playersVisibleOnMap.values(), MapData.this.colors, this.minX, this.minY, this.maxX + 1 - this.minX, this.maxY + 1 - this.minY);
            }
            return this.field_176109_i++ % 5 == 0 ? new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.playersVisibleOnMap.values(), MapData.this.colors, 0, 0, 0, 0) : null;
        }

        public void update(int x, int y) {
            if (this.field_176105_d) {
                this.minX = Math.min(this.minX, x);
                this.minY = Math.min(this.minY, y);
                this.maxX = Math.max(this.maxX, x);
                this.maxY = Math.max(this.maxY, y);
            } else {
                this.field_176105_d = true;
                this.minX = x;
                this.minY = y;
                this.maxX = x;
                this.maxY = y;
            }
        }
    }
}

