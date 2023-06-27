package net.minecraft.entity.item;

import net.minecraft.world.*;
import net.minecraft.util.*;
import com.google.common.collect.*;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;

public class EntityPainting extends EntityHanging
{
    public EnumArt art;
    
    public EntityPainting(final World worldIn) {
        super(worldIn);
    }
    
    public EntityPainting(final World worldIn, final BlockPos pos, final EnumFacing facing) {
        super(worldIn, pos);
        final List<EnumArt> list = (List<EnumArt>)Lists.newArrayList();
        for (final EnumArt entitypainting$enumart : EnumArt.values()) {
            this.art = entitypainting$enumart;
            this.updateFacingWithBoundingBox(facing);
            if (this.onValidSurface()) {
                list.add(entitypainting$enumart);
            }
        }
        if (!list.isEmpty()) {
            this.art = list.get(this.rand.nextInt(list.size()));
        }
        this.updateFacingWithBoundingBox(facing);
    }
    
    public EntityPainting(final World worldIn, final BlockPos pos, final EnumFacing facing, final String title) {
        this(worldIn, pos, facing);
        for (final EnumArt entitypainting$enumart : EnumArt.values()) {
            if (entitypainting$enumart.title.equals(title)) {
                this.art = entitypainting$enumart;
                break;
            }
        }
        this.updateFacingWithBoundingBox(facing);
    }
    
    @Override
    public void writeEntityToNBT(final NBTTagCompound tagCompound) {
        tagCompound.setString("Motive", this.art.title);
        super.writeEntityToNBT(tagCompound);
    }
    
    @Override
    public void readEntityFromNBT(final NBTTagCompound tagCompund) {
        final String s = tagCompund.getString("Motive");
        for (final EnumArt entitypainting$enumart : EnumArt.values()) {
            if (entitypainting$enumart.title.equals(s)) {
                this.art = entitypainting$enumart;
            }
        }
        if (this.art == null) {
            this.art = EnumArt.KEBAB;
        }
        super.readEntityFromNBT(tagCompund);
    }
    
    @Override
    public int getWidthPixels() {
        return this.art.sizeX;
    }
    
    @Override
    public int getHeightPixels() {
        return this.art.sizeY;
    }
    
    @Override
    public void onBroken(final Entity brokenEntity) {
        if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
            if (brokenEntity instanceof EntityPlayer) {
                final EntityPlayer entityplayer = (EntityPlayer)brokenEntity;
                if (entityplayer.capabilities.isCreativeMode) {
                    return;
                }
            }
            this.entityDropItem(new ItemStack(Items.painting), 0.0f);
        }
    }
    
    @Override
    public void setLocationAndAngles(final double x, final double y, final double z, final float yaw, final float pitch) {
        final BlockPos blockpos = this.hangingPosition.add(x - this.posX, y - this.posY, z - this.posZ);
        this.setPosition(blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }
    
    @Override
    public void setPositionAndRotation2(final double x, final double y, final double z, final float yaw, final float pitch, final int posRotationIncrements, final boolean p_180426_10_) {
        final BlockPos blockpos = this.hangingPosition.add(x - this.posX, y - this.posY, z - this.posZ);
        this.setPosition(blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }
    
    public enum EnumArt
    {
        KEBAB("Kebab", 16, 16, 0, 0), 
        AZTEC("Aztec", 16, 16, 16, 0), 
        ALBAN("Alban", 16, 16, 32, 0), 
        AZTEC_2("Aztec2", 16, 16, 48, 0), 
        BOMB("Bomb", 16, 16, 64, 0), 
        PLANT("Plant", 16, 16, 80, 0), 
        WASTELAND("Wasteland", 16, 16, 96, 0), 
        POOL("Pool", 32, 16, 0, 32), 
        COURBET("Courbet", 32, 16, 32, 32), 
        SEA("Sea", 32, 16, 64, 32), 
        SUNSET("Sunset", 32, 16, 96, 32), 
        CREEBET("Creebet", 32, 16, 128, 32), 
        WANDERER("Wanderer", 16, 32, 0, 64), 
        GRAHAM("Graham", 16, 32, 16, 64), 
        MATCH("Match", 32, 32, 0, 128), 
        BUST("Bust", 32, 32, 32, 128), 
        STAGE("Stage", 32, 32, 64, 128), 
        VOID("Void", 32, 32, 96, 128), 
        SKULL_AND_ROSES("SkullAndRoses", 32, 32, 128, 128), 
        WITHER("Wither", 32, 32, 160, 128), 
        FIGHTERS("Fighters", 64, 32, 0, 96), 
        POINTER("Pointer", 64, 64, 0, 192), 
        PIGSCENE("Pigscene", 64, 64, 64, 192), 
        BURNING_SKULL("BurningSkull", 64, 64, 128, 192), 
        SKELETON("Skeleton", 64, 48, 192, 64), 
        DONKEY_KONG("DonkeyKong", 64, 48, 192, 112);
        
        public static final int field_180001_A;
        public final String title;
        public final int sizeX;
        public final int sizeY;
        public final int offsetX;
        public final int offsetY;
        
        private EnumArt(final String titleIn, final int width, final int height, final int textureU, final int textureV) {
            this.title = titleIn;
            this.sizeX = width;
            this.sizeY = height;
            this.offsetX = textureU;
            this.offsetY = textureV;
        }
        
        static {
            field_180001_A = "SkullAndRoses".length();
        }
    }
}
