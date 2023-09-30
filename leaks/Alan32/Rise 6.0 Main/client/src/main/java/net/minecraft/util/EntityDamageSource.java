package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource {
    protected Entity damageSourceEntity;

    /**
     * Whether this EntityDamageSource is from an entity wearing Thorns-enchanted armor.
     */
    private boolean isThornsDamage = false;

    public EntityDamageSource(final String p_i1567_1_, final Entity damageSourceEntityIn) {
        super(p_i1567_1_);
        this.damageSourceEntity = damageSourceEntityIn;
    }

    /**
     * Sets this EntityDamageSource as originating from Thorns armor
     */
    public EntityDamageSource setIsThornsDamage() {
        this.isThornsDamage = true;
        return this;
    }

    public boolean getIsThornsDamage() {
        return this.isThornsDamage;
    }

    public Entity getEntity() {
        return this.damageSourceEntity;
    }

    /**
     * Gets the death message that is displayed when the player dies
     */
    public IChatComponent getDeathMessage(final EntityLivingBase p_151519_1_) {
        final ItemStack itemstack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase) this.damageSourceEntity).getHeldItem() : null;
        final String s = "death.attack." + this.damageType;
        final String s1 = s + ".item";
        return itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, p_151519_1_.getDisplayName(), this.damageSourceEntity.getDisplayName(), itemstack.getChatComponent()) : new ChatComponentTranslation(s, p_151519_1_.getDisplayName(), this.damageSourceEntity.getDisplayName());
    }

    /**
     * Return whether this damage source will have its damage amount scaled based on the current difficulty.
     */
    public boolean isDifficultyScaled() {
        return this.damageSourceEntity != null && this.damageSourceEntity instanceof EntityLivingBase && !(this.damageSourceEntity instanceof EntityPlayer);
    }
}
