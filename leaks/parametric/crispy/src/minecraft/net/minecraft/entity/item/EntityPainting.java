package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityPainting extends EntityHanging
{
    public EntityPainting.EnumArt art;

    public EntityPainting(World worldIn)
    {
        super(worldIn);
    }

    public EntityPainting(World worldIn, BlockPos pos, EnumFacing facing)
    {
        super(worldIn, pos);
        ArrayList var4 = Lists.newArrayList();
        EntityPainting.EnumArt[] var5 = EntityPainting.EnumArt.values();
        int var6 = var5.length;

        for (int var7 = 0; var7 < var6; ++var7)
        {
            EntityPainting.EnumArt var8 = var5[var7];
            this.art = var8;
            this.updateFacingWithBoundingBox(facing);

            if (this.onValidSurface())
            {
                var4.add(var8);
            }
        }

        if (!var4.isEmpty())
        {
            this.art = (EntityPainting.EnumArt)var4.get(this.rand.nextInt(var4.size()));
        }

        this.updateFacingWithBoundingBox(facing);
    }

    public EntityPainting(World worldIn, BlockPos pos, EnumFacing facing, String title)
    {
        this(worldIn, pos, facing);
        EntityPainting.EnumArt[] var5 = EntityPainting.EnumArt.values();
        int var6 = var5.length;

        for (int var7 = 0; var7 < var6; ++var7)
        {
            EntityPainting.EnumArt var8 = var5[var7];

            if (var8.title.equals(title))
            {
                this.art = var8;
                break;
            }
        }

        this.updateFacingWithBoundingBox(facing);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setString("Motive", this.art.title);
        super.writeEntityToNBT(tagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        String var2 = tagCompund.getString("Motive");
        EntityPainting.EnumArt[] var3 = EntityPainting.EnumArt.values();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            EntityPainting.EnumArt var6 = var3[var5];

            if (var6.title.equals(var2))
            {
                this.art = var6;
            }
        }

        if (this.art == null)
        {
            this.art = EntityPainting.EnumArt.KEBAB;
        }

        super.readEntityFromNBT(tagCompund);
    }

    public int getWidthPixels()
    {
        return this.art.sizeX;
    }

    public int getHeightPixels()
    {
        return this.art.sizeY;
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(Entity brokenEntity)
    {
        if (this.worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            if (brokenEntity instanceof EntityPlayer)
            {
                EntityPlayer var2 = (EntityPlayer)brokenEntity;

                if (var2.capabilities.isCreativeMode)
                {
                    return;
                }
            }

            this.entityDropItem(new ItemStack(Items.painting), 0.0F);
        }
    }

    /**
     * Sets the location and Yaw/Pitch of an entity in the world
     */
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)
    {
        BlockPos var9 = new BlockPos(x - this.posX, y - this.posY, z - this.posZ);
        BlockPos var10 = this.hangingPosition.add(var9);
        this.setPosition((double)var10.getX(), (double)var10.getY(), (double)var10.getZ());
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_)
    {
        BlockPos var11 = new BlockPos(x - this.posX, y - this.posY, z - this.posZ);
        BlockPos var12 = this.hangingPosition.add(var11);
        this.setPosition((double)var12.getX(), (double)var12.getY(), (double)var12.getZ());
    }

    public static enum EnumArt
    {
        KEBAB("KEBAB", 0, "Kebab", 16, 16, 0, 0),
        AZTEC("AZTEC", 1, "Aztec", 16, 16, 16, 0),
        ALBAN("ALBAN", 2, "Alban", 16, 16, 32, 0),
        AZTEC_2("AZTEC_2", 3, "Aztec2", 16, 16, 48, 0),
        BOMB("BOMB", 4, "Bomb", 16, 16, 64, 0),
        PLANT("PLANT", 5, "Plant", 16, 16, 80, 0),
        WASTELAND("WASTELAND", 6, "Wasteland", 16, 16, 96, 0),
        POOL("POOL", 7, "Pool", 32, 16, 0, 32),
        COURBET("COURBET", 8, "Courbet", 32, 16, 32, 32),
        SEA("SEA", 9, "Sea", 32, 16, 64, 32),
        SUNSET("SUNSET", 10, "Sunset", 32, 16, 96, 32),
        CREEBET("CREEBET", 11, "Creebet", 32, 16, 128, 32),
        WANDERER("WANDERER", 12, "Wanderer", 16, 32, 0, 64),
        GRAHAM("GRAHAM", 13, "Graham", 16, 32, 16, 64),
        MATCH("MATCH", 14, "Match", 32, 32, 0, 128),
        BUST("BUST", 15, "Bust", 32, 32, 32, 128),
        STAGE("STAGE", 16, "Stage", 32, 32, 64, 128),
        VOID("VOID", 17, "Void", 32, 32, 96, 128),
        SKULL_AND_ROSES("SKULL_AND_ROSES", 18, "SkullAndRoses", 32, 32, 128, 128),
        WITHER("WITHER", 19, "Wither", 32, 32, 160, 128),
        FIGHTERS("FIGHTERS", 20, "Fighters", 64, 32, 0, 96),
        POINTER("POINTER", 21, "Pointer", 64, 64, 0, 192),
        PIGSCENE("PIGSCENE", 22, "Pigscene", 64, 64, 64, 192),
        BURNING_SKULL("BURNING_SKULL", 23, "BurningSkull", 64, 64, 128, 192),
        SKELETON("SKELETON", 24, "Skeleton", 64, 48, 192, 64),
        DONKEY_KONG("DONKEY_KONG", 25, "DonkeyKong", 64, 48, 192, 112);
        public static final int field_180001_A = "SkullAndRoses".length();
        public final String title;
        public final int sizeX;
        public final int sizeY;
        public final int offsetX;
        public final int offsetY;

        private EnumArt(String p_i1598_1_, int p_i1598_2_, String titleIn, int width, int height, int textureU, int textureV)
        {
            this.title = titleIn;
            this.sizeX = width;
            this.sizeY = height;
            this.offsetX = textureU;
            this.offsetY = textureV;
        }
    }
}
