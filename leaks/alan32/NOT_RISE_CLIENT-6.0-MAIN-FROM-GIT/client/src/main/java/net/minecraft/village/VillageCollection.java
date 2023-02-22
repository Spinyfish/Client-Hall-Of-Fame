package net.minecraft.village;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;

import java.util.Iterator;
import java.util.List;

public class VillageCollection extends WorldSavedData {
    private World worldObj;
    private final List<BlockPos> villagerPositionsList = Lists.newArrayList();
    private final List<VillageDoorInfo> newDoors = Lists.newArrayList();
    private final List<Village> villageList = Lists.newArrayList();
    private int tickCounter;

    public VillageCollection(final String name) {
        super(name);
    }

    public VillageCollection(final World worldIn) {
        super(fileNameForProvider(worldIn.provider));
        this.worldObj = worldIn;
        this.markDirty();
    }

    public void setWorldsForAll(final World worldIn) {
        this.worldObj = worldIn;

        for (final Village village : this.villageList) {
            village.setWorld(worldIn);
        }
    }

    public void addToVillagerPositionList(final BlockPos pos) {
        if (this.villagerPositionsList.size() <= 64) {
            if (!this.positionInList(pos)) {
                this.villagerPositionsList.add(pos);
            }
        }
    }

    /**
     * Runs a single tick for the village collection
     */
    public void tick() {
        ++this.tickCounter;

        for (final Village village : this.villageList) {
            village.tick(this.tickCounter);
        }

        this.removeAnnihilatedVillages();
        this.dropOldestVillagerPosition();
        this.addNewDoorsToVillageOrCreateVillage();

        if (this.tickCounter % 400 == 0) {
            this.markDirty();
        }
    }

    private void removeAnnihilatedVillages() {
        final Iterator<Village> iterator = this.villageList.iterator();

        while (iterator.hasNext()) {
            final Village village = iterator.next();

            if (village.isAnnihilated()) {
                iterator.remove();
                this.markDirty();
            }
        }
    }

    public List<Village> getVillageList() {
        return this.villageList;
    }

    public Village getNearestVillage(final BlockPos doorBlock, final int radius) {
        Village village = null;
        double d0 = 3.4028234663852886E38D;

        for (final Village village1 : this.villageList) {
            final double d1 = village1.getCenter().distanceSq(doorBlock);

            if (d1 < d0) {
                final float f = (float) (radius + village1.getVillageRadius());

                if (d1 <= (double) (f * f)) {
                    village = village1;
                    d0 = d1;
                }
            }
        }

        return village;
    }

    private void dropOldestVillagerPosition() {
        if (!this.villagerPositionsList.isEmpty()) {
            this.addDoorsAround(this.villagerPositionsList.remove(0));
        }
    }

    private void addNewDoorsToVillageOrCreateVillage() {
        for (int i = 0; i < this.newDoors.size(); ++i) {
            final VillageDoorInfo villagedoorinfo = this.newDoors.get(i);
            Village village = this.getNearestVillage(villagedoorinfo.getDoorBlockPos(), 32);

            if (village == null) {
                village = new Village(this.worldObj);
                this.villageList.add(village);
                this.markDirty();
            }

            village.addVillageDoorInfo(villagedoorinfo);
        }

        this.newDoors.clear();
    }

    private void addDoorsAround(final BlockPos central) {
        final int i = 16;
        final int j = 4;
        final int k = 16;

        for (int l = -i; l < i; ++l) {
            for (int i1 = -j; i1 < j; ++i1) {
                for (int j1 = -k; j1 < k; ++j1) {
                    final BlockPos blockpos = central.add(l, i1, j1);

                    if (this.isWoodDoor(blockpos)) {
                        final VillageDoorInfo villagedoorinfo = this.checkDoorExistence(blockpos);

                        if (villagedoorinfo == null) {
                            this.addToNewDoorsList(blockpos);
                        } else {
                            villagedoorinfo.func_179849_a(this.tickCounter);
                        }
                    }
                }
            }
        }
    }

    /**
     * returns the VillageDoorInfo if it exists in any village or in the newDoor list, otherwise returns null
     */
    private VillageDoorInfo checkDoorExistence(final BlockPos doorBlock) {
        for (final VillageDoorInfo villagedoorinfo : this.newDoors) {
            if (villagedoorinfo.getDoorBlockPos().getX() == doorBlock.getX() && villagedoorinfo.getDoorBlockPos().getZ() == doorBlock.getZ() && Math.abs(villagedoorinfo.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
                return villagedoorinfo;
            }
        }

        for (final Village village : this.villageList) {
            final VillageDoorInfo villagedoorinfo1 = village.getExistedDoor(doorBlock);

            if (villagedoorinfo1 != null) {
                return villagedoorinfo1;
            }
        }

        return null;
    }

    private void addToNewDoorsList(final BlockPos doorBlock) {
        final EnumFacing enumfacing = BlockDoor.getFacing(this.worldObj, doorBlock);
        final EnumFacing enumfacing1 = enumfacing.getOpposite();
        final int i = this.countBlocksCanSeeSky(doorBlock, enumfacing, 5);
        final int j = this.countBlocksCanSeeSky(doorBlock, enumfacing1, i + 1);

        if (i != j) {
            this.newDoors.add(new VillageDoorInfo(doorBlock, i < j ? enumfacing : enumfacing1, this.tickCounter));
        }
    }

    /**
     * Check five blocks in the direction. The centerPos will not be checked.
     */
    private int countBlocksCanSeeSky(final BlockPos centerPos, final EnumFacing direction, final int limitation) {
        int i = 0;

        for (int j = 1; j <= 5; ++j) {
            if (this.worldObj.canSeeSky(centerPos.offset(direction, j))) {
                ++i;

                if (i >= limitation) {
                    return i;
                }
            }
        }

        return i;
    }

    private boolean positionInList(final BlockPos pos) {
        for (final BlockPos blockpos : this.villagerPositionsList) {
            if (blockpos.equals(pos)) {
                return true;
            }
        }

        return false;
    }

    private boolean isWoodDoor(final BlockPos doorPos) {
        final Block block = this.worldObj.getBlockState(doorPos).getBlock();
        return block instanceof BlockDoor && block.getMaterial() == Material.wood;
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(final NBTTagCompound nbt) {
        this.tickCounter = nbt.getInteger("Tick");
        final NBTTagList nbttaglist = nbt.getTagList("Villages", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            final Village village = new Village();
            village.readVillageDataFromNBT(nbttagcompound);
            this.villageList.add(village);
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(final NBTTagCompound nbt) {
        nbt.setInteger("Tick", this.tickCounter);
        final NBTTagList nbttaglist = new NBTTagList();

        for (final Village village : this.villageList) {
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            village.writeVillageDataToNBT(nbttagcompound);
            nbttaglist.appendTag(nbttagcompound);
        }

        nbt.setTag("Villages", nbttaglist);
    }

    public static String fileNameForProvider(final WorldProvider provider) {
        return "villages" + provider.getInternalNameSuffix();
    }
}
