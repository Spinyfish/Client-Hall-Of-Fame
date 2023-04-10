package net.minecraft.entity.ai;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class EntityAIVillagerInteract extends EntityAIWatchClosest2
{
    /** The delay before the villager throws an itemstack (in ticks) */
    private int interactionDelay;
    private EntityVillager villager;

    public EntityAIVillagerInteract(EntityVillager villagerIn)
    {
        super(villagerIn, EntityVillager.class, 3.0F, 0.02F);
        this.villager = villagerIn;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();

        if (this.villager.canAbondonItems() && this.closestEntity instanceof EntityVillager && ((EntityVillager)this.closestEntity).func_175557_cr())
        {
            this.interactionDelay = 10;
        }
        else
        {
            this.interactionDelay = 0;
        }
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        super.updateTask();

        if (this.interactionDelay > 0)
        {
            --this.interactionDelay;

            if (this.interactionDelay == 0)
            {
                InventoryBasic var1 = this.villager.getVillagerInventory();

                for (int var2 = 0; var2 < var1.getSizeInventory(); ++var2)
                {
                    ItemStack var3 = var1.getStackInSlot(var2);
                    ItemStack var4 = null;

                    if (var3 != null)
                    {
                        Item var5 = var3.getItem();
                        int var6;

                        if ((var5 == Items.bread || var5 == Items.potato || var5 == Items.carrot) && var3.stackSize > 3)
                        {
                            var6 = var3.stackSize / 2;
                            var3.stackSize -= var6;
                            var4 = new ItemStack(var5, var6, var3.getMetadata());
                        }
                        else if (var5 == Items.wheat && var3.stackSize > 5)
                        {
                            var6 = var3.stackSize / 2 / 3 * 3;
                            int var7 = var6 / 3;
                            var3.stackSize -= var6;
                            var4 = new ItemStack(Items.bread, var7, 0);
                        }

                        if (var3.stackSize <= 0)
                        {
                            var1.setInventorySlotContents(var2, (ItemStack)null);
                        }
                    }

                    if (var4 != null)
                    {
                        double var11 = this.villager.posY - 0.30000001192092896D + (double)this.villager.getEyeHeight();
                        EntityItem var12 = new EntityItem(this.villager.worldObj, this.villager.posX, var11, this.villager.posZ, var4);
                        float var8 = 0.3F;
                        float var9 = this.villager.rotationYawHead;
                        float var10 = this.villager.rotationPitch;
                        var12.motionX = (double)(-MathHelper.sin(var9 / 180.0F * (float)Math.PI) * MathHelper.cos(var10 / 180.0F * (float)Math.PI) * var8);
                        var12.motionZ = (double)(MathHelper.cos(var9 / 180.0F * (float)Math.PI) * MathHelper.cos(var10 / 180.0F * (float)Math.PI) * var8);
                        var12.motionY = (double)(-MathHelper.sin(var10 / 180.0F * (float)Math.PI) * var8 + 0.1F);
                        var12.setDefaultPickupDelay();
                        this.villager.worldObj.spawnEntityInWorld(var12);
                        break;
                    }
                }
            }
        }
    }
}
