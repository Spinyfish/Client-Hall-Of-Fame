/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultiset
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multiset
 *  com.google.common.collect.Multisets
 */
package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;

public class ItemMap
extends ItemMapBase {
    protected ItemMap() {
        this.setHasSubtypes(true);
    }

    public static MapData loadMapData(int mapId, World worldIn) {
        String s = "map_" + mapId;
        MapData mapdata = (MapData)worldIn.loadItemData(MapData.class, s);
        if (mapdata == null) {
            mapdata = new MapData(s);
            worldIn.setItemData(s, mapdata);
        }
        return mapdata;
    }

    public MapData getMapData(ItemStack stack, World worldIn) {
        String s = "map_" + stack.getMetadata();
        MapData mapdata = (MapData)worldIn.loadItemData(MapData.class, s);
        if (mapdata == null && !worldIn.isRemote) {
            stack.setItemDamage(worldIn.getUniqueDataId("map"));
            s = "map_" + stack.getMetadata();
            mapdata = new MapData(s);
            mapdata.scale = (byte)3;
            mapdata.calculateMapCenter(worldIn.getWorldInfo().getSpawnX(), worldIn.getWorldInfo().getSpawnZ(), mapdata.scale);
            mapdata.dimension = (byte)worldIn.provider.getDimensionId();
            mapdata.markDirty();
            worldIn.setItemData(s, mapdata);
        }
        return mapdata;
    }

    public void updateMapData(World worldIn, Entity viewer, MapData data) {
        if (worldIn.provider.getDimensionId() == data.dimension && viewer instanceof EntityPlayer) {
            int i = 1 << data.scale;
            int j = data.xCenter;
            int k = data.zCenter;
            int l = MathHelper.floor_double(viewer.posX - (double)j) / i + 64;
            int i1 = MathHelper.floor_double(viewer.posZ - (double)k) / i + 64;
            int j1 = 128 / i;
            if (worldIn.provider.getHasNoSky()) {
                j1 /= 2;
            }
            MapData.MapInfo mapdata$mapinfo = data.getMapInfo((EntityPlayer)viewer);
            ++mapdata$mapinfo.field_82569_d;
            boolean flag = false;
            for (int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
                if ((k1 & 0xF) != (mapdata$mapinfo.field_82569_d & 0xF) && !flag) continue;
                flag = false;
                double d0 = 0.0;
                for (int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
                    byte b1;
                    byte b0;
                    MapColor mapcolor;
                    if (k1 < 0 || l1 < -1 || k1 >= 128 || l1 >= 128) continue;
                    int i2 = k1 - l;
                    int j2 = l1 - i1;
                    boolean flag1 = i2 * i2 + j2 * j2 > (j1 - 2) * (j1 - 2);
                    int k2 = (j / i + k1 - 64) * i;
                    int l2 = (k / i + l1 - 64) * i;
                    HashMultiset multiset = HashMultiset.create();
                    Chunk chunk = worldIn.getChunkFromBlockCoords(new BlockPos(k2, 0, l2));
                    if (chunk.isEmpty()) continue;
                    int i3 = k2 & 0xF;
                    int j3 = l2 & 0xF;
                    int k3 = 0;
                    double d1 = 0.0;
                    if (worldIn.provider.getHasNoSky()) {
                        int l3 = k2 + l2 * 231871;
                        if (((l3 = l3 * l3 * 31287121 + l3 * 11) >> 20 & 1) == 0) {
                            multiset.add((Object)Blocks.dirt.getMapColor(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT)), 10);
                        } else {
                            multiset.add((Object)Blocks.stone.getMapColor(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE)), 100);
                        }
                        d1 = 100.0;
                    } else {
                        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                        for (int i4 = 0; i4 < i; ++i4) {
                            for (int j4 = 0; j4 < i; ++j4) {
                                int k4 = chunk.getHeightValue(i4 + i3, j4 + j3) + 1;
                                IBlockState iblockstate = Blocks.air.getDefaultState();
                                if (k4 > 1) {
                                    while ((iblockstate = chunk.getBlockState(blockpos$mutableblockpos.func_181079_c(i4 + i3, --k4, j4 + j3))).getBlock().getMapColor(iblockstate) == MapColor.airColor && k4 > 0) {
                                    }
                                    if (k4 > 0 && iblockstate.getBlock().getMaterial().isLiquid()) {
                                        Block block;
                                        int l4 = k4 - 1;
                                        do {
                                            block = chunk.getBlock(i4 + i3, l4--, j4 + j3);
                                            ++k3;
                                        } while (l4 > 0 && block.getMaterial().isLiquid());
                                    }
                                }
                                d1 += (double)k4 / (double)(i * i);
                                multiset.add((Object)iblockstate.getBlock().getMapColor(iblockstate));
                            }
                        }
                    }
                    k3 /= i * i;
                    double d2 = (d1 - d0) * 4.0 / (double)(i + 4) + ((double)(k1 + l1 & 1) - 0.5) * 0.4;
                    int i5 = 1;
                    if (d2 > 0.6) {
                        i5 = 2;
                    }
                    if (d2 < -0.6) {
                        i5 = 0;
                    }
                    if ((mapcolor = (MapColor)Iterables.getFirst((Iterable)Multisets.copyHighestCountFirst((Multiset)multiset), (Object)MapColor.airColor)) == MapColor.waterColor) {
                        d2 = (double)k3 * 0.1 + (double)(k1 + l1 & 1) * 0.2;
                        i5 = 1;
                        if (d2 < 0.5) {
                            i5 = 2;
                        }
                        if (d2 > 0.9) {
                            i5 = 0;
                        }
                    }
                    d0 = d1;
                    if (l1 < 0 || i2 * i2 + j2 * j2 >= j1 * j1 || flag1 && (k1 + l1 & 1) == 0 || (b0 = data.colors[k1 + l1 * 128]) == (b1 = (byte)(mapcolor.colorIndex * 4 + i5))) continue;
                    data.colors[k1 + l1 * 128] = b1;
                    data.updateMapData(k1, l1);
                    flag = true;
                }
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote) {
            MapData mapdata = this.getMapData(stack, worldIn);
            if (entityIn instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)entityIn;
                mapdata.updateVisiblePlayers(entityplayer, stack);
            }
            if (isSelected) {
                this.updateMapData(worldIn, entityIn, mapdata);
            }
        }
    }

    @Override
    public Packet createMapDataPacket(ItemStack stack, World worldIn, EntityPlayer player) {
        return this.getMapData(stack, worldIn).getMapPacket(stack, worldIn, player);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("map_is_scaling")) {
            MapData mapdata = Items.filled_map.getMapData(stack, worldIn);
            stack.setItemDamage(worldIn.getUniqueDataId("map"));
            MapData mapdata1 = new MapData("map_" + stack.getMetadata());
            mapdata1.scale = (byte)(mapdata.scale + 1);
            if (mapdata1.scale > 4) {
                mapdata1.scale = (byte)4;
            }
            mapdata1.calculateMapCenter(mapdata.xCenter, mapdata.zCenter, mapdata1.scale);
            mapdata1.dimension = mapdata.dimension;
            mapdata1.markDirty();
            worldIn.setItemData("map_" + stack.getMetadata(), mapdata1);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        MapData mapdata = this.getMapData(stack, playerIn.worldObj);
        if (advanced) {
            if (mapdata == null) {
                tooltip.add("Unknown map");
            } else {
                tooltip.add("Scaling at 1:" + (1 << mapdata.scale));
                tooltip.add("(Level " + mapdata.scale + "/" + 4 + ")");
            }
        }
    }
}

