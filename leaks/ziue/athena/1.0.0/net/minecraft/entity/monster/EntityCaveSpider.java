package net.minecraft.entity.monster;

import net.minecraft.potion.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;

public class EntityCaveSpider extends EntitySpider
{
    public EntityCaveSpider(final World worldIn) {
        super(worldIn);
        this.setSize(0.7f, 0.5f);
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(12.0);
    }
    
    @Override
    public boolean attackEntityAsMob(final Entity entityIn) {
        if (super.attackEntityAsMob(entityIn)) {
            if (entityIn instanceof EntityLivingBase) {
                int i = 0;
                if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL) {
                    i = 7;
                }
                else if (this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
                    i = 15;
                }
                if (i > 0) {
                    ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(Potion.poison.id, i * 20, 0));
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(final DifficultyInstance difficulty, final IEntityLivingData livingdata) {
        return livingdata;
    }
    
    @Override
    public float getEyeHeight() {
        return 0.45f;
    }
}
