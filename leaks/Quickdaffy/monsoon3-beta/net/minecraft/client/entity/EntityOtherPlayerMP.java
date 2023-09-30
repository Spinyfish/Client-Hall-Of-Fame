/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityOtherPlayerMP
extends AbstractClientPlayer {
    private boolean isItemInUse;
    private int otherPlayerMPPosRotationIncrements;
    private double otherPlayerMPX;
    private double otherPlayerMPY;
    private double otherPlayerMPZ;
    private double otherPlayerMPYaw;
    private double otherPlayerMPPitch;

    public EntityOtherPlayerMP(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
        this.stepHeight = 0.0f;
        this.noClip = true;
        this.renderOffsetY = 0.25f;
        this.renderDistanceWeight = 10.0;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return true;
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
        this.otherPlayerMPX = x;
        this.otherPlayerMPY = y;
        this.otherPlayerMPZ = z;
        this.otherPlayerMPYaw = yaw;
        this.otherPlayerMPPitch = pitch;
        this.otherPlayerMPPosRotationIncrements = posRotationIncrements;
    }

    @Override
    public void onUpdate() {
        this.renderOffsetY = 0.0f;
        super.onUpdate();
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        this.limbSwingAmount += (f - this.limbSwingAmount) * 0.4f;
        this.limbSwing += this.limbSwingAmount;
        if (!this.isItemInUse && this.isEating() && this.inventory.mainInventory[this.inventory.currentItem] != null) {
            ItemStack itemstack = this.inventory.mainInventory[this.inventory.currentItem];
            this.setItemInUse(this.inventory.mainInventory[this.inventory.currentItem], itemstack.getItem().getMaxItemUseDuration(itemstack));
            this.isItemInUse = true;
        } else if (this.isItemInUse && !this.isEating()) {
            this.clearItemInUse();
            this.isItemInUse = false;
        }
    }

    @Override
    public void onLivingUpdate() {
        if (this.otherPlayerMPPosRotationIncrements > 0) {
            double d3;
            double d0 = this.posX + (this.otherPlayerMPX - this.posX) / (double)this.otherPlayerMPPosRotationIncrements;
            double d1 = this.posY + (this.otherPlayerMPY - this.posY) / (double)this.otherPlayerMPPosRotationIncrements;
            double d2 = this.posZ + (this.otherPlayerMPZ - this.posZ) / (double)this.otherPlayerMPPosRotationIncrements;
            for (d3 = this.otherPlayerMPYaw - (double)this.rotationYaw; d3 < -180.0; d3 += 360.0) {
            }
            while (d3 >= 180.0) {
                d3 -= 360.0;
            }
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.otherPlayerMPPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.otherPlayerMPPitch - (double)this.rotationPitch) / (double)this.otherPlayerMPPosRotationIncrements);
            --this.otherPlayerMPPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        this.prevCameraYaw = this.cameraYaw;
        this.updateArmSwingProgress();
        float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float f = (float)Math.atan(-this.motionY * (double)0.2f) * 15.0f;
        if (f1 > 0.1f) {
            f1 = 0.1f;
        }
        if (!this.onGround || this.getHealth() <= 0.0f) {
            f1 = 0.0f;
        }
        if (this.onGround || this.getHealth() <= 0.0f) {
            f = 0.0f;
        }
        this.cameraYaw += (f1 - this.cameraYaw) * 0.4f;
        this.cameraPitch += (f - this.cameraPitch) * 0.8f;
    }

    @Override
    public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
        if (slotIn == 0) {
            this.inventory.mainInventory[this.inventory.currentItem] = stack;
        } else {
            this.inventory.armorInventory[slotIn - 1] = stack;
        }
    }

    @Override
    public void addChatMessage(IChatComponent component) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(component);
    }

    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return false;
    }

    @Override
    public BlockPos getPosition() {
        return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
    }
}

