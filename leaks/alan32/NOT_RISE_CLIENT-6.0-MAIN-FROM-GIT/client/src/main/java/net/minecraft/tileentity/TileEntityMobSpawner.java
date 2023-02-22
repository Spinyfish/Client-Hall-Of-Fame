package net.minecraft.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity implements ITickable {
    private final MobSpawnerBaseLogic spawnerLogic = new MobSpawnerBaseLogic() {
        public void func_98267_a(final int id) {
            TileEntityMobSpawner.this.worldObj.addBlockEvent(TileEntityMobSpawner.this.pos, Blocks.mob_spawner, id, 0);
        }

        public World getSpawnerWorld() {
            return TileEntityMobSpawner.this.worldObj;
        }

        public BlockPos getSpawnerPosition() {
            return TileEntityMobSpawner.this.pos;
        }

        public void setRandomEntity(final MobSpawnerBaseLogic.WeightedRandomMinecart p_98277_1_) {
            super.setRandomEntity(p_98277_1_);

            if (this.getSpawnerWorld() != null) {
                this.getSpawnerWorld().markBlockForUpdate(TileEntityMobSpawner.this.pos);
            }
        }
    };

    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.spawnerLogic.readFromNBT(compound);
    }

    public void writeToNBT(final NBTTagCompound compound) {
        super.writeToNBT(compound);
        this.spawnerLogic.writeToNBT(compound);
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update() {
        this.spawnerLogic.updateSpawner();
    }

    /**
     * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
     * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
     */
    public Packet getDescriptionPacket() {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        nbttagcompound.removeTag("SpawnPotentials");
        return new S35PacketUpdateTileEntity(this.pos, 1, nbttagcompound);
    }

    public boolean receiveClientEvent(final int id, final int type) {
        return this.spawnerLogic.setDelayToMin(id) || super.receiveClientEvent(id, type);
    }

    public boolean func_183000_F() {
        return true;
    }

    public MobSpawnerBaseLogic getSpawnerBaseLogic() {
        return this.spawnerLogic;
    }
}
