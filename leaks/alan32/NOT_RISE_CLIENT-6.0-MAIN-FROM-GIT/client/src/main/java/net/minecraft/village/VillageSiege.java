package net.minecraft.village;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class VillageSiege {
    private final World worldObj;
    private boolean field_75535_b;
    private int field_75536_c = -1;
    private int field_75533_d;
    private int field_75534_e;

    /**
     * Instance of Village.
     */
    private Village theVillage;
    private int field_75532_g;
    private int field_75538_h;
    private int field_75539_i;

    public VillageSiege(final World worldIn) {
        this.worldObj = worldIn;
    }

    /**
     * Runs a single tick for the village siege
     */
    public void tick() {
        if (this.worldObj.isDaytime()) {
            this.field_75536_c = 0;
        } else if (this.field_75536_c != 2) {
            if (this.field_75536_c == 0) {
                final float f = this.worldObj.getCelestialAngle(0.0F);

                if ((double) f < 0.5D || (double) f > 0.501D) {
                    return;
                }

                this.field_75536_c = this.worldObj.rand.nextInt(10) == 0 ? 1 : 2;
                this.field_75535_b = false;

                if (this.field_75536_c == 2) {
                    return;
                }
            }

            if (this.field_75536_c != -1) {
                if (!this.field_75535_b) {
                    if (!this.func_75529_b()) {
                        return;
                    }

                    this.field_75535_b = true;
                }

                if (this.field_75534_e > 0) {
                    --this.field_75534_e;
                } else {
                    this.field_75534_e = 2;

                    if (this.field_75533_d > 0) {
                        this.spawnZombie();
                        --this.field_75533_d;
                    } else {
                        this.field_75536_c = 2;
                    }
                }
            }
        }
    }

    private boolean func_75529_b() {
        final List<EntityPlayer> list = this.worldObj.playerEntities;
        final Iterator iterator = list.iterator();

        while (true) {
            if (!iterator.hasNext()) {
                return false;
            }

            final EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (!entityplayer.isSpectator()) {
                this.theVillage = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(entityplayer), 1);

                if (this.theVillage != null && this.theVillage.getNumVillageDoors() >= 10 && this.theVillage.getTicksSinceLastDoorAdding() >= 20 && this.theVillage.getNumVillagers() >= 20) {
                    final BlockPos blockpos = this.theVillage.getCenter();
                    final float f = (float) this.theVillage.getVillageRadius();
                    boolean flag = false;

                    for (int i = 0; i < 10; ++i) {
                        final float f1 = this.worldObj.rand.nextFloat() * (float) Math.PI * 2.0F;
                        this.field_75532_g = blockpos.getX() + (int) ((double) (MathHelper.cos(f1) * f) * 0.9D);
                        this.field_75538_h = blockpos.getY();
                        this.field_75539_i = blockpos.getZ() + (int) ((double) (MathHelper.sin(f1) * f) * 0.9D);
                        flag = false;

                        for (final Village village : this.worldObj.getVillageCollection().getVillageList()) {
                            if (village != this.theVillage && village.func_179866_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i))) {
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            break;
                        }
                    }

                    if (flag) {
                        return false;
                    }

                    final Vec3 vec3 = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));

                    if (vec3 != null) {
                        break;
                    }
                }
            }
        }

        this.field_75534_e = 0;
        this.field_75533_d = 20;
        return true;
    }

    private boolean spawnZombie() {
        final Vec3 vec3 = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));

        if (vec3 == null) {
            return false;
        } else {
            final EntityZombie entityzombie;

            try {
                entityzombie = new EntityZombie(this.worldObj);
                entityzombie.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityzombie)), null);
                entityzombie.setVillager(false);
            } catch (final Exception exception) {
                exception.printStackTrace();
                return false;
            }

            entityzombie.setLocationAndAngles(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
            this.worldObj.spawnEntityInWorld(entityzombie);
            final BlockPos blockpos = this.theVillage.getCenter();
            entityzombie.setHomePosAndDistance(blockpos, this.theVillage.getVillageRadius());
            return true;
        }
    }

    private Vec3 func_179867_a(final BlockPos p_179867_1_) {
        for (int i = 0; i < 10; ++i) {
            final BlockPos blockpos = p_179867_1_.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);

            if (this.theVillage.func_179866_a(blockpos) && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, this.worldObj, blockpos)) {
                return new Vec3(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
        }

        return null;
    }
}
