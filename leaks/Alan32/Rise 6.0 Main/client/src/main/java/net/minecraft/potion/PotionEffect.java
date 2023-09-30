package net.minecraft.potion;

import com.alan.clients.module.impl.movement.PotionExtender;
import com.alan.clients.util.interfaces.InstanceAccess;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEffect implements InstanceAccess {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * ID value of the potion this effect matches.
     */
    public final int potionID;

    /**
     * The duration of the potion effect
     */
    public int duration;

    /**
     * The amplifier of the potion effect
     */
    public int amplifier;

    /**
     * Whether the potion is a splash potion
     */
    public boolean isSplashPotion;

    /**
     * Whether the potion effect came from a beacon
     */
    public boolean isAmbient;

    /**
     * True if potion effect duration is at maximum, false otherwise.
     */
    private boolean isPotionDurationMax;
    private boolean showParticles;

    public PotionEffect(final int id, final int effectDuration) {
        this(id, effectDuration, 0);
    }

    public PotionEffect(final int id, final int effectDuration, final int effectAmplifier) {
        this(id, effectDuration, effectAmplifier, false, true);
    }

    public PotionEffect(final int id, final int effectDuration, final int effectAmplifier, final boolean ambient, final boolean showParticles) {
        this.potionID = id;
        this.duration = effectDuration;
        this.amplifier = effectAmplifier;
        this.isAmbient = ambient;
        this.showParticles = showParticles;
    }

    public PotionEffect(final PotionEffect other) {
        this.potionID = other.potionID;
        this.duration = other.duration;
        this.amplifier = other.amplifier;
        this.isAmbient = other.isAmbient;
        this.showParticles = other.showParticles;
    }

    /**
     * merges the input PotionEffect into this one if this.amplifier <= tomerge.amplifier. The duration in the supplied
     * potion effect is assumed to be greater.
     */
    public void combine(final PotionEffect other) {
        if (this.potionID != other.potionID) {
            LOGGER.warn("This method should only be called for matching effects!");
        }

        if (other.amplifier > this.amplifier) {
            this.amplifier = other.amplifier;
            this.duration = other.duration;
        } else if (other.amplifier == this.amplifier && this.duration < other.duration) {
            this.duration = other.duration;
        } else if (!other.isAmbient && this.isAmbient) {
            this.isAmbient = other.isAmbient;
        }

        this.showParticles = other.showParticles;
    }

    /**
     * Retrieve the ID of the potion this effect matches.
     */
    public int getPotionID() {
        return this.potionID;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    /**
     * Set whether this potion is a splash potion.
     */
    public void setSplashPotion(final boolean splashPotion) {
        this.isSplashPotion = splashPotion;
    }

    /**
     * Gets whether this potion effect originated from a beacon
     */
    public boolean getIsAmbient() {
        return this.isAmbient;
    }

    public boolean getIsShowParticles() {
        return this.showParticles;
    }

    public boolean onUpdate(final EntityLivingBase entityIn) {
        if (this.duration > 0) {
            if (this.potionID < Potion.potionTypes.length) { // else if fixes server-induced crash
                if (Potion.potionTypes[this.potionID].isReady(this.duration, this.amplifier)) {
                    this.performEffect(entityIn);
                }

                this.deincrementDuration();
            } else {
                this.duration = 0;
            }
        }

        return this.duration > 0;
    }

    private int deincrementDuration() {
        final PotionExtender potionExtender = this.getModule(PotionExtender.class);

        if (potionExtender != null && potionExtender.isEnabled() && potionExtender.potions.containsKey(this)) {
            return this.duration;
        }

        return --this.duration;
    }

    public void performEffect(final EntityLivingBase entityIn) {
        if (this.duration > 0) {
            Potion.potionTypes[this.potionID].performEffect(entityIn, this.amplifier);
        }
    }

    public String getEffectName() {
        return Potion.potionTypes[this.potionID].getName();
    }

    public int hashCode() {
        return this.potionID;
    }

    public String toString() {
        String s = "";

        if (this.getAmplifier() > 0) {
            s = this.getEffectName() + " x " + (this.getAmplifier() + 1) + ", Duration: " + this.getDuration();
        } else {
            s = this.getEffectName() + ", Duration: " + this.getDuration();
        }

        if (this.isSplashPotion) {
            s = s + ", Splash: true";
        }

        if (!this.showParticles) {
            s = s + ", Particles: false";
        }

        return Potion.potionTypes[this.potionID].isUsable() ? "(" + s + ")" : s;
    }

    public boolean equals(final Object p_equals_1_) {
        if (!(p_equals_1_ instanceof PotionEffect)) {
            return false;
        } else {
            final PotionEffect potioneffect = (PotionEffect) p_equals_1_;
            return this.potionID == potioneffect.potionID && this.amplifier == potioneffect.amplifier && this.duration == potioneffect.duration && this.isSplashPotion == potioneffect.isSplashPotion && this.isAmbient == potioneffect.isAmbient;
        }
    }

    /**
     * Write a custom potion effect to a potion item's NBT data.
     */
    public NBTTagCompound writeCustomPotionEffectToNBT(final NBTTagCompound nbt) {
        nbt.setByte("Id", (byte) this.getPotionID());
        nbt.setByte("Amplifier", (byte) this.getAmplifier());
        nbt.setInteger("Duration", this.getDuration());
        nbt.setBoolean("Ambient", this.getIsAmbient());
        nbt.setBoolean("ShowParticles", this.getIsShowParticles());
        return nbt;
    }

    /**
     * Read a custom potion effect from a potion item's NBT data.
     */
    public static PotionEffect readCustomPotionEffectFromNBT(final NBTTagCompound nbt) {
        final int i = nbt.getByte("Id");

        if (i >= 0 && i < Potion.potionTypes.length && Potion.potionTypes[i] != null) {
            final int j = nbt.getByte("Amplifier");
            final int k = nbt.getInteger("Duration");
            final boolean flag = nbt.getBoolean("Ambient");
            boolean flag1 = true;

            if (nbt.hasKey("ShowParticles", 1)) {
                flag1 = nbt.getBoolean("ShowParticles");
            }

            return new PotionEffect(i, k, j, flag, flag1);
        } else {
            return null;
        }
    }

    /**
     * Toggle the isPotionDurationMax field.
     */
    public void setPotionDurationMax(final boolean maxDuration) {
        this.isPotionDurationMax = maxDuration;
    }

    public boolean getIsPotionDurationMax() {
        return this.isPotionDurationMax;
    }
}
