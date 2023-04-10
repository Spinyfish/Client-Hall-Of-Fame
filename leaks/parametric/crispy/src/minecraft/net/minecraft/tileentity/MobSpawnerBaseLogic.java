package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic
{
    /** The delay to spawn. */
    private int spawnDelay = 20;
    private String mobID = "Pig";

    /** List of minecart to spawn. */
    private final List minecartToSpawn = Lists.newArrayList();
    private MobSpawnerBaseLogic.WeightedRandomMinecart randomEntity;

    /** The rotation of the mob inside the mob spawner */
    private double mobRotation;

    /** the previous rotation of the mob inside the mob spawner */
    private double prevMobRotation;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;

    /** Cached instance of the entity to render inside the spawner. */
    private Entity cachedEntity;
    private int maxNearbyEntities = 6;

    /** The distance from which a player activates the spawner. */
    private int activatingRangeFromPlayer = 16;

    /** The range coefficient for spawning entities around. */
    private int spawnRange = 4;

    /**
     * Gets the entity name that should be spawned.
     */
    private String getEntityNameToSpawn()
    {
        if (this.getRandomEntity() == null)
        {
            if (this.mobID.equals("Minecart"))
            {
                this.mobID = "MinecartRideable";
            }

            return this.mobID;
        }
        else
        {
            return this.getRandomEntity().entityType;
        }
    }

    public void setEntityName(String name)
    {
        this.mobID = name;
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to activate it.
     */
    private boolean isActivated()
    {
        BlockPos var1 = this.getSpawnerPosition();
        return this.getSpawnerWorld().isAnyPlayerWithinRangeAt((double)var1.getX() + 0.5D, (double)var1.getY() + 0.5D, (double)var1.getZ() + 0.5D, (double)this.activatingRangeFromPlayer);
    }

    public void updateSpawner()
    {
        if (this.isActivated())
        {
            BlockPos var1 = this.getSpawnerPosition();
            double var6;

            if (this.getSpawnerWorld().isRemote)
            {
                double var2 = (double)((float)var1.getX() + this.getSpawnerWorld().rand.nextFloat());
                double var4 = (double)((float)var1.getY() + this.getSpawnerWorld().rand.nextFloat());
                var6 = (double)((float)var1.getZ() + this.getSpawnerWorld().rand.nextFloat());
                this.getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var2, var4, var6, 0.0D, 0.0D, 0.0D, new int[0]);
                this.getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, var2, var4, var6, 0.0D, 0.0D, 0.0D, new int[0]);

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                }

                this.prevMobRotation = this.mobRotation;
                this.mobRotation = (this.mobRotation + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
            }
            else
            {
                if (this.spawnDelay == -1)
                {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                    return;
                }

                boolean var13 = false;

                for (int var3 = 0; var3 < this.spawnCount; ++var3)
                {
                    Entity var14 = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());

                    if (var14 == null)
                    {
                        return;
                    }

                    int var5 = this.getSpawnerWorld().getEntitiesWithinAABB(var14.getClass(), (new AxisAlignedBB((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), (double)(var1.getX() + 1), (double)(var1.getY() + 1), (double)(var1.getZ() + 1))).expand((double)this.spawnRange, (double)this.spawnRange, (double)this.spawnRange)).size();

                    if (var5 >= this.maxNearbyEntities)
                    {
                        this.resetTimer();
                        return;
                    }

                    var6 = (double)var1.getX() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * (double)this.spawnRange + 0.5D;
                    double var8 = (double)(var1.getY() + this.getSpawnerWorld().rand.nextInt(3) - 1);
                    double var10 = (double)var1.getZ() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * (double)this.spawnRange + 0.5D;
                    EntityLiving var12 = var14 instanceof EntityLiving ? (EntityLiving)var14 : null;
                    var14.setLocationAndAngles(var6, var8, var10, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);

                    if (var12 == null || var12.getCanSpawnHere() && var12.isNotColliding())
                    {
                        this.spawnNewEntity(var14, true);
                        this.getSpawnerWorld().playAuxSFX(2004, var1, 0);

                        if (var12 != null)
                        {
                            var12.spawnExplosionParticle();
                        }

                        var13 = true;
                    }
                }

                if (var13)
                {
                    this.resetTimer();
                }
            }
        }
    }

    private Entity spawnNewEntity(Entity entityIn, boolean spawn)
    {
        if (this.getRandomEntity() != null)
        {
            NBTTagCompound var3 = new NBTTagCompound();
            entityIn.writeToNBTOptional(var3);
            Iterator var4 = this.getRandomEntity().nbtData.getKeySet().iterator();

            while (var4.hasNext())
            {
                String var5 = (String)var4.next();
                NBTBase var6 = this.getRandomEntity().nbtData.getTag(var5);
                var3.setTag(var5, var6.copy());
            }

            entityIn.readFromNBT(var3);

            if (entityIn.worldObj != null && spawn)
            {
                entityIn.worldObj.spawnEntityInWorld(entityIn);
            }

            NBTTagCompound var12;

            for (Entity var11 = entityIn; var3.hasKey("Riding", 10); var3 = var12)
            {
                var12 = var3.getCompoundTag("Riding");
                Entity var13 = EntityList.createEntityByName(var12.getString("id"), entityIn.worldObj);

                if (var13 != null)
                {
                    NBTTagCompound var7 = new NBTTagCompound();
                    var13.writeToNBTOptional(var7);
                    Iterator var8 = var12.getKeySet().iterator();

                    while (var8.hasNext())
                    {
                        String var9 = (String)var8.next();
                        NBTBase var10 = var12.getTag(var9);
                        var7.setTag(var9, var10.copy());
                    }

                    var13.readFromNBT(var7);
                    var13.setLocationAndAngles(var11.posX, var11.posY, var11.posZ, var11.rotationYaw, var11.rotationPitch);

                    if (entityIn.worldObj != null && spawn)
                    {
                        entityIn.worldObj.spawnEntityInWorld(var13);
                    }

                    var11.mountEntity(var13);
                }

                var11 = var13;
            }
        }
        else if (entityIn instanceof EntityLivingBase && entityIn.worldObj != null && spawn)
        {
            ((EntityLiving)entityIn).onInitialSpawn(entityIn.worldObj.getDifficultyForLocation(new BlockPos(entityIn)), (IEntityLivingData)null);
            entityIn.worldObj.spawnEntityInWorld(entityIn);
        }

        return entityIn;
    }

    private void resetTimer()
    {
        if (this.maxSpawnDelay <= this.minSpawnDelay)
        {
            this.spawnDelay = this.minSpawnDelay;
        }
        else
        {
            int var10003 = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(var10003);
        }

        if (this.minecartToSpawn.size() > 0)
        {
            this.setRandomEntity((MobSpawnerBaseLogic.WeightedRandomMinecart)WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.minecartToSpawn));
        }

        this.func_98267_a(1);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        this.mobID = nbt.getString("EntityId");
        this.spawnDelay = nbt.getShort("Delay");
        this.minecartToSpawn.clear();

        if (nbt.hasKey("SpawnPotentials", 9))
        {
            NBTTagList var2 = nbt.getTagList("SpawnPotentials", 10);

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                this.minecartToSpawn.add(new MobSpawnerBaseLogic.WeightedRandomMinecart(var2.getCompoundTagAt(var3)));
            }
        }

        if (nbt.hasKey("SpawnData", 10))
        {
            this.setRandomEntity(new MobSpawnerBaseLogic.WeightedRandomMinecart(nbt.getCompoundTag("SpawnData"), this.mobID));
        }
        else
        {
            this.setRandomEntity((MobSpawnerBaseLogic.WeightedRandomMinecart)null);
        }

        if (nbt.hasKey("MinSpawnDelay", 99))
        {
            this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
            this.spawnCount = nbt.getShort("SpawnCount");
        }

        if (nbt.hasKey("MaxNearbyEntities", 99))
        {
            this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
            this.activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
        }

        if (nbt.hasKey("SpawnRange", 99))
        {
            this.spawnRange = nbt.getShort("SpawnRange");
        }

        if (this.getSpawnerWorld() != null)
        {
            this.cachedEntity = null;
        }
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setString("EntityId", this.getEntityNameToSpawn());
        nbt.setShort("Delay", (short)this.spawnDelay);
        nbt.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
        nbt.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        nbt.setShort("SpawnCount", (short)this.spawnCount);
        nbt.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        nbt.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
        nbt.setShort("SpawnRange", (short)this.spawnRange);

        if (this.getRandomEntity() != null)
        {
            nbt.setTag("SpawnData", this.getRandomEntity().nbtData.copy());
        }

        if (this.getRandomEntity() != null || this.minecartToSpawn.size() > 0)
        {
            NBTTagList var2 = new NBTTagList();

            if (this.minecartToSpawn.size() > 0)
            {
                Iterator var3 = this.minecartToSpawn.iterator();

                while (var3.hasNext())
                {
                    MobSpawnerBaseLogic.WeightedRandomMinecart var4 = (MobSpawnerBaseLogic.WeightedRandomMinecart)var3.next();
                    var2.appendTag(var4.toNBT());
                }
            }
            else
            {
                var2.appendTag(this.getRandomEntity().toNBT());
            }

            nbt.setTag("SpawnPotentials", var2);
        }
    }

    public Entity func_180612_a(World worldIn)
    {
        if (this.cachedEntity == null)
        {
            Entity var2 = EntityList.createEntityByName(this.getEntityNameToSpawn(), worldIn);

            if (var2 != null)
            {
                var2 = this.spawnNewEntity(var2, false);
                this.cachedEntity = var2;
            }
        }

        return this.cachedEntity;
    }

    /**
     * Sets the delay to minDelay if parameter given is 1, else return false.
     */
    public boolean setDelayToMin(int delay)
    {
        if (delay == 1 && this.getSpawnerWorld().isRemote)
        {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        else
        {
            return false;
        }
    }

    private MobSpawnerBaseLogic.WeightedRandomMinecart getRandomEntity()
    {
        return this.randomEntity;
    }

    public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart p_98277_1_)
    {
        this.randomEntity = p_98277_1_;
    }

    public abstract void func_98267_a(int var1);

    public abstract World getSpawnerWorld();

    public abstract BlockPos getSpawnerPosition();

    public double getMobRotation()
    {
        return this.mobRotation;
    }

    public double getPrevMobRotation()
    {
        return this.prevMobRotation;
    }

    public class WeightedRandomMinecart extends WeightedRandom.Item
    {
        private final NBTTagCompound nbtData;
        private final String entityType;

        public WeightedRandomMinecart(NBTTagCompound tagCompound)
        {
            this(tagCompound.getCompoundTag("Properties"), tagCompound.getString("Type"), tagCompound.getInteger("Weight"));
        }

        public WeightedRandomMinecart(NBTTagCompound tagCompound, String type)
        {
            this(tagCompound, type, 1);
        }

        private WeightedRandomMinecart(NBTTagCompound tagCompound, String type, int weight)
        {
            super(weight);

            if (type.equals("Minecart"))
            {
                if (tagCompound != null)
                {
                    type = EntityMinecart.EnumMinecartType.byNetworkID(tagCompound.getInteger("Type")).getName();
                }
                else
                {
                    type = "MinecartRideable";
                }
            }

            this.nbtData = tagCompound;
            this.entityType = type;
        }

        public NBTTagCompound toNBT()
        {
            NBTTagCompound var1 = new NBTTagCompound();
            var1.setTag("Properties", this.nbtData);
            var1.setString("Type", this.entityType);
            var1.setInteger("Weight", this.itemWeight);
            return var1;
        }
    }
}
