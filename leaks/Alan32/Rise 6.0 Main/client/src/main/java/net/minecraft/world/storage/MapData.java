package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

import java.util.List;
import java.util.Map;

public class MapData extends WorldSavedData {
    public int xCenter;
    public int zCenter;
    public byte dimension;
    public byte scale;

    /**
     * colours
     */
    public byte[] colors = new byte[16384];
    public List<MapData.MapInfo> playersArrayList = Lists.newArrayList();
    private final Map<EntityPlayer, MapData.MapInfo> playersHashMap = Maps.newHashMap();
    public Map<String, Vec4b> playersVisibleOnMap = Maps.newLinkedHashMap();

    public MapData(final String mapname) {
        super(mapname);
    }

    public void calculateMapCenter(final double x, final double z, final int mapScale) {
        final int i = 128 * (1 << mapScale);
        final int j = MathHelper.floor_double((x + 64.0D) / (double) i);
        final int k = MathHelper.floor_double((z + 64.0D) / (double) i);
        this.xCenter = j * i + i / 2 - 64;
        this.zCenter = k * i + i / 2 - 64;
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(final NBTTagCompound nbt) {
        this.dimension = nbt.getByte("dimension");
        this.xCenter = nbt.getInteger("xCenter");
        this.zCenter = nbt.getInteger("zCenter");
        this.scale = nbt.getByte("scale");
        this.scale = (byte) MathHelper.clamp_int(this.scale, 0, 4);
        final int i = nbt.getShort("width");
        final int j = nbt.getShort("height");

        if (i == 128 && j == 128) {
            this.colors = nbt.getByteArray("colors");
        } else {
            final byte[] abyte = nbt.getByteArray("colors");
            this.colors = new byte[16384];
            final int k = (128 - i) / 2;
            final int l = (128 - j) / 2;

            for (int i1 = 0; i1 < j; ++i1) {
                final int j1 = i1 + l;

                if (j1 >= 0 || j1 < 128) {
                    for (int k1 = 0; k1 < i; ++k1) {
                        final int l1 = k1 + k;

                        if (l1 >= 0 || l1 < 128) {
                            this.colors[l1 + j1 * 128] = abyte[k1 + i1 * i];
                        }
                    }
                }
            }
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(final NBTTagCompound nbt) {
        nbt.setByte("dimension", this.dimension);
        nbt.setInteger("xCenter", this.xCenter);
        nbt.setInteger("zCenter", this.zCenter);
        nbt.setByte("scale", this.scale);
        nbt.setShort("width", (short) 128);
        nbt.setShort("height", (short) 128);
        nbt.setByteArray("colors", this.colors);
    }

    /**
     * Adds the player passed to the list of visible players and checks to see which players are visible
     */
    public void updateVisiblePlayers(final EntityPlayer player, final ItemStack mapStack) {
        if (!this.playersHashMap.containsKey(player)) {
            final MapData.MapInfo mapdata$mapinfo = new MapData.MapInfo(player);
            this.playersHashMap.put(player, mapdata$mapinfo);
            this.playersArrayList.add(mapdata$mapinfo);
        }

        if (!player.inventory.hasItemStack(mapStack)) {
            this.playersVisibleOnMap.remove(player.getCommandSenderName());
        }

        for (int i = 0; i < this.playersArrayList.size(); ++i) {
            final MapData.MapInfo mapdata$mapinfo1 = this.playersArrayList.get(i);

            if (!mapdata$mapinfo1.entityplayerObj.isDead && (mapdata$mapinfo1.entityplayerObj.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame())) {
                if (!mapStack.isOnItemFrame() && mapdata$mapinfo1.entityplayerObj.dimension == this.dimension) {
                    this.updatePlayersVisibleOnMap(0, mapdata$mapinfo1.entityplayerObj.worldObj, mapdata$mapinfo1.entityplayerObj.getCommandSenderName(), mapdata$mapinfo1.entityplayerObj.posX, mapdata$mapinfo1.entityplayerObj.posZ, mapdata$mapinfo1.entityplayerObj.rotationYaw);
                }
            } else {
                this.playersHashMap.remove(mapdata$mapinfo1.entityplayerObj);
                this.playersArrayList.remove(mapdata$mapinfo1);
            }
        }

        if (mapStack.isOnItemFrame()) {
            final EntityItemFrame entityitemframe = mapStack.getItemFrame();
            final BlockPos blockpos = entityitemframe.getHangingPosition();
            this.updatePlayersVisibleOnMap(1, player.worldObj, "frame-" + entityitemframe.getEntityId(), blockpos.getX(), blockpos.getZ(), entityitemframe.facingDirection.getHorizontalIndex() * 90);
        }

        if (mapStack.hasTagCompound() && mapStack.getTagCompound().hasKey("Decorations", 9)) {
            final NBTTagList nbttaglist = mapStack.getTagCompound().getTagList("Decorations", 10);

            for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                final NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(j);

                if (!this.playersVisibleOnMap.containsKey(nbttagcompound.getString("id"))) {
                    this.updatePlayersVisibleOnMap(nbttagcompound.getByte("type"), player.worldObj, nbttagcompound.getString("id"), nbttagcompound.getDouble("x"), nbttagcompound.getDouble("z"), nbttagcompound.getDouble("rot"));
                }
            }
        }
    }

    private void updatePlayersVisibleOnMap(int type, final World worldIn, final String entityIdentifier, final double worldX, final double worldZ, double rotation) {
        final int i = 1 << this.scale;
        final float f = (float) (worldX - (double) this.xCenter) / (float) i;
        final float f1 = (float) (worldZ - (double) this.zCenter) / (float) i;
        byte b0 = (byte) ((int) ((double) (f * 2.0F) + 0.5D));
        byte b1 = (byte) ((int) ((double) (f1 * 2.0F) + 0.5D));
        final int j = 63;
        byte b2;

        if (f >= (float) (-j) && f1 >= (float) (-j) && f <= (float) j && f1 <= (float) j) {
            rotation = rotation + (rotation < 0.0D ? -8.0D : 8.0D);
            b2 = (byte) ((int) (rotation * 16.0D / 360.0D));

            if (this.dimension < 0) {
                final int k = (int) (worldIn.getWorldInfo().getWorldTime() / 10L);
                b2 = (byte) (k * k * 34187121 + k * 121 >> 15 & 15);
            }
        } else {
            if (Math.abs(f) >= 320.0F || Math.abs(f1) >= 320.0F) {
                this.playersVisibleOnMap.remove(entityIdentifier);
                return;
            }

            type = 6;
            b2 = 0;

            if (f <= (float) (-j)) {
                b0 = (byte) ((int) ((double) (j * 2) + 2.5D));
            }

            if (f1 <= (float) (-j)) {
                b1 = (byte) ((int) ((double) (j * 2) + 2.5D));
            }

            if (f >= (float) j) {
                b0 = (byte) (j * 2 + 1);
            }

            if (f1 >= (float) j) {
                b1 = (byte) (j * 2 + 1);
            }
        }

        this.playersVisibleOnMap.put(entityIdentifier, new Vec4b((byte) type, b0, b1, b2));
    }

    public Packet getMapPacket(final ItemStack mapStack, final World worldIn, final EntityPlayer player) {
        final MapData.MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
        return mapdata$mapinfo == null ? null : mapdata$mapinfo.getPacket(mapStack);
    }

    public void updateMapData(final int x, final int y) {
        super.markDirty();

        for (final MapData.MapInfo mapdata$mapinfo : this.playersArrayList) {
            mapdata$mapinfo.update(x, y);
        }
    }

    public MapData.MapInfo getMapInfo(final EntityPlayer player) {
        MapData.MapInfo mapdata$mapinfo = this.playersHashMap.get(player);

        if (mapdata$mapinfo == null) {
            mapdata$mapinfo = new MapData.MapInfo(player);
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

        public MapInfo(final EntityPlayer player) {
            this.entityplayerObj = player;
        }

        public Packet getPacket(final ItemStack stack) {
            if (this.field_176105_d) {
                this.field_176105_d = false;
                return new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.playersVisibleOnMap.values(), MapData.this.colors, this.minX, this.minY, this.maxX + 1 - this.minX, this.maxY + 1 - this.minY);
            } else {
                return this.field_176109_i++ % 5 == 0 ? new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.playersVisibleOnMap.values(), MapData.this.colors, 0, 0, 0, 0) : null;
            }
        }

        public void update(final int x, final int y) {
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
