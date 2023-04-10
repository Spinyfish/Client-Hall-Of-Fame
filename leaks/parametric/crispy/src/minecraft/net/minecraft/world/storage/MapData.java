package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
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

public class MapData extends WorldSavedData
{
    public int xCenter;
    public int zCenter;
    public byte dimension;
    public byte scale;

    /** colours */
    public byte[] colors = new byte[16384];

    /**
     * Holds a reference to the MapInfo of the players who own a copy of the map
     */
    public List playersArrayList = Lists.newArrayList();

    /**
     * Holds a reference to the players who own a copy of the map and a reference to their MapInfo
     */
    private Map playersHashMap = Maps.newHashMap();
    public Map playersVisibleOnMap = Maps.newLinkedHashMap();

    public MapData(String mapname)
    {
        super(mapname);
    }

    public void calculateMapCenter(double x, double z, int mapScale)
    {
        int var6 = 128 * (1 << mapScale);
        int var7 = MathHelper.floor_double((x + 64.0D) / (double)var6);
        int var8 = MathHelper.floor_double((z + 64.0D) / (double)var6);
        this.xCenter = var7 * var6 + var6 / 2 - 64;
        this.zCenter = var8 * var6 + var6 / 2 - 64;
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.dimension = nbt.getByte("dimension");
        this.xCenter = nbt.getInteger("xCenter");
        this.zCenter = nbt.getInteger("zCenter");
        this.scale = nbt.getByte("scale");
        this.scale = (byte)MathHelper.clamp_int(this.scale, 0, 4);
        short var2 = nbt.getShort("width");
        short var3 = nbt.getShort("height");

        if (var2 == 128 && var3 == 128)
        {
            this.colors = nbt.getByteArray("colors");
        }
        else
        {
            byte[] var4 = nbt.getByteArray("colors");
            this.colors = new byte[16384];
            int var5 = (128 - var2) / 2;
            int var6 = (128 - var3) / 2;

            for (int var7 = 0; var7 < var3; ++var7)
            {
                int var8 = var7 + var6;

                if (var8 >= 0 || var8 < 128)
                {
                    for (int var9 = 0; var9 < var2; ++var9)
                    {
                        int var10 = var9 + var5;

                        if (var10 >= 0 || var10 < 128)
                        {
                            this.colors[var10 + var8 * 128] = var4[var9 + var7 * var2];
                        }
                    }
                }
            }
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setByte("dimension", this.dimension);
        nbt.setInteger("xCenter", this.xCenter);
        nbt.setInteger("zCenter", this.zCenter);
        nbt.setByte("scale", this.scale);
        nbt.setShort("width", (short)128);
        nbt.setShort("height", (short)128);
        nbt.setByteArray("colors", this.colors);
    }

    /**
     * Adds the player passed to the list of visible players and checks to see which players are visible
     */
    public void updateVisiblePlayers(EntityPlayer player, ItemStack mapStack)
    {
        if (!this.playersHashMap.containsKey(player))
        {
            MapData.MapInfo var3 = new MapData.MapInfo(player);
            this.playersHashMap.put(player, var3);
            this.playersArrayList.add(var3);
        }

        if (!player.inventory.hasItemStack(mapStack))
        {
            this.playersVisibleOnMap.remove(player.getCommandSenderName());
        }

        for (int var6 = 0; var6 < this.playersArrayList.size(); ++var6)
        {
            MapData.MapInfo var4 = (MapData.MapInfo)this.playersArrayList.get(var6);

            if (!var4.entityplayerObj.isDead && (var4.entityplayerObj.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame()))
            {
                if (!mapStack.isOnItemFrame() && var4.entityplayerObj.dimension == this.dimension)
                {
                    this.updatePlayersVisibleOnMap(0, var4.entityplayerObj.worldObj, var4.entityplayerObj.getCommandSenderName(), var4.entityplayerObj.posX, var4.entityplayerObj.posZ, (double)var4.entityplayerObj.rotationYaw);
                }
            }
            else
            {
                this.playersHashMap.remove(var4.entityplayerObj);
                this.playersArrayList.remove(var4);
            }
        }

        if (mapStack.isOnItemFrame())
        {
            EntityItemFrame var7 = mapStack.getItemFrame();
            BlockPos var9 = var7.getHangingPosition();
            this.updatePlayersVisibleOnMap(1, player.worldObj, "frame-" + var7.getEntityId(), (double)var9.getX(), (double)var9.getZ(), (double)(var7.facingDirection.getHorizontalIndex() * 90));
        }

        if (mapStack.hasTagCompound() && mapStack.getTagCompound().hasKey("Decorations", 9))
        {
            NBTTagList var8 = mapStack.getTagCompound().getTagList("Decorations", 10);

            for (int var10 = 0; var10 < var8.tagCount(); ++var10)
            {
                NBTTagCompound var5 = var8.getCompoundTagAt(var10);

                if (!this.playersVisibleOnMap.containsKey(var5.getString("id")))
                {
                    this.updatePlayersVisibleOnMap(var5.getByte("type"), player.worldObj, var5.getString("id"), var5.getDouble("x"), var5.getDouble("z"), var5.getDouble("rot"));
                }
            }
        }
    }

    private void updatePlayersVisibleOnMap(int type, World worldIn, String entityIdentifier, double worldX, double worldZ, double rotation)
    {
        int var10 = 1 << this.scale;
        float var11 = (float)(worldX - (double)this.xCenter) / (float)var10;
        float var12 = (float)(worldZ - (double)this.zCenter) / (float)var10;
        byte var13 = (byte)((int)((double)(var11 * 2.0F) + 0.5D));
        byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5D));
        byte var16 = 63;
        byte var15;

        if (var11 >= (float)(-var16) && var12 >= (float)(-var16) && var11 <= (float)var16 && var12 <= (float)var16)
        {
            rotation += rotation < 0.0D ? -8.0D : 8.0D;
            var15 = (byte)((int)(rotation * 16.0D / 360.0D));

            if (this.dimension < 0)
            {
                int var17 = (int)(worldIn.getWorldInfo().getWorldTime() / 10L);
                var15 = (byte)(var17 * var17 * 34187121 + var17 * 121 >> 15 & 15);
            }
        }
        else
        {
            if (Math.abs(var11) >= 320.0F || Math.abs(var12) >= 320.0F)
            {
                this.playersVisibleOnMap.remove(entityIdentifier);
                return;
            }

            type = 6;
            var15 = 0;

            if (var11 <= (float)(-var16))
            {
                var13 = (byte)((int)((double)(var16 * 2) + 2.5D));
            }

            if (var12 <= (float)(-var16))
            {
                var14 = (byte)((int)((double)(var16 * 2) + 2.5D));
            }

            if (var11 >= (float)var16)
            {
                var13 = (byte)(var16 * 2 + 1);
            }

            if (var12 >= (float)var16)
            {
                var14 = (byte)(var16 * 2 + 1);
            }
        }

        this.playersVisibleOnMap.put(entityIdentifier, new Vec4b((byte)type, var13, var14, var15));
    }

    public Packet getMapPacket(ItemStack mapStack, World worldIn, EntityPlayer player)
    {
        MapData.MapInfo var4 = (MapData.MapInfo)this.playersHashMap.get(player);
        return var4 == null ? null : var4.getPacket(mapStack);
    }

    public void updateMapData(int x, int y)
    {
        super.markDirty();
        Iterator var3 = this.playersArrayList.iterator();

        while (var3.hasNext())
        {
            MapData.MapInfo var4 = (MapData.MapInfo)var3.next();
            var4.update(x, y);
        }
    }

    public MapData.MapInfo getMapInfo(EntityPlayer player)
    {
        MapData.MapInfo var2 = (MapData.MapInfo)this.playersHashMap.get(player);

        if (var2 == null)
        {
            var2 = new MapData.MapInfo(player);
            this.playersHashMap.put(player, var2);
            this.playersArrayList.add(var2);
        }

        return var2;
    }

    public class MapInfo
    {
        public final EntityPlayer entityplayerObj;
        private boolean field_176105_d = true;
        private int minX = 0;
        private int minY = 0;
        private int maxX = 127;
        private int maxY = 127;
        private int field_176109_i;
        public int field_82569_d;

        public MapInfo(EntityPlayer player)
        {
            this.entityplayerObj = player;
        }

        public Packet getPacket(ItemStack stack)
        {
            if (this.field_176105_d)
            {
                this.field_176105_d = false;
                return new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.playersVisibleOnMap.values(), MapData.this.colors, this.minX, this.minY, this.maxX + 1 - this.minX, this.maxY + 1 - this.minY);
            }
            else
            {
                return this.field_176109_i++ % 5 == 0 ? new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.playersVisibleOnMap.values(), MapData.this.colors, 0, 0, 0, 0) : null;
            }
        }

        public void update(int x, int y)
        {
            if (this.field_176105_d)
            {
                this.minX = Math.min(this.minX, x);
                this.minY = Math.min(this.minY, y);
                this.maxX = Math.max(this.maxX, x);
                this.maxY = Math.max(this.maxY, y);
            }
            else
            {
                this.field_176105_d = true;
                this.minX = x;
                this.minY = y;
                this.maxX = x;
                this.maxY = y;
            }
        }
    }
}
