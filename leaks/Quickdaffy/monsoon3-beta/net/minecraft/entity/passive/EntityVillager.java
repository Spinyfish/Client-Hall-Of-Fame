/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAIHarvestFarmland;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityVillager
extends EntityAgeable
implements IMerchant,
INpc {
    private int randomTickDivider;
    private boolean isMating;
    private boolean isPlaying;
    Village villageObj;
    private EntityPlayer buyingPlayer;
    private MerchantRecipeList buyingList;
    private int timeUntilReset;
    private boolean needsInitilization;
    private boolean isWillingToTrade;
    private int wealth;
    private String lastBuyingPlayer;
    private int careerId;
    private int careerLevel;
    private boolean isLookingForHome;
    private boolean areAdditionalTasksSet;
    private InventoryBasic villagerInventory = new InventoryBasic("Items", false, 8);
    private static final ITradeList[][][][] DEFAULT_TRADE_LIST_MAP = new ITradeList[][][][]{{{{new EmeraldForItems(Items.wheat, new PriceInfo(18, 22)), new EmeraldForItems(Items.potato, new PriceInfo(15, 19)), new EmeraldForItems(Items.carrot, new PriceInfo(15, 19)), new ListItemForEmeralds(Items.bread, new PriceInfo(-4, -2))}, {new EmeraldForItems(Item.getItemFromBlock(Blocks.pumpkin), new PriceInfo(8, 13)), new ListItemForEmeralds(Items.pumpkin_pie, new PriceInfo(-3, -2))}, {new EmeraldForItems(Item.getItemFromBlock(Blocks.melon_block), new PriceInfo(7, 12)), new ListItemForEmeralds(Items.apple, new PriceInfo(-5, -7))}, {new ListItemForEmeralds(Items.cookie, new PriceInfo(-6, -10)), new ListItemForEmeralds(Items.cake, new PriceInfo(1, 1))}}, {{new EmeraldForItems(Items.string, new PriceInfo(15, 20)), new EmeraldForItems(Items.coal, new PriceInfo(16, 24)), new ItemAndEmeraldToItem(Items.fish, new PriceInfo(6, 6), Items.cooked_fish, new PriceInfo(6, 6))}, {new ListEnchantedItemForEmeralds(Items.fishing_rod, new PriceInfo(7, 8))}}, {{new EmeraldForItems(Item.getItemFromBlock(Blocks.wool), new PriceInfo(16, 22)), new ListItemForEmeralds(Items.shears, new PriceInfo(3, 4))}, {new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 0), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 1), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 2), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 3), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 4), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 5), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 6), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 7), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 8), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 9), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 10), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 11), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 12), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 13), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 14), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 15), new PriceInfo(1, 2))}}, {{new EmeraldForItems(Items.string, new PriceInfo(15, 20)), new ListItemForEmeralds(Items.arrow, new PriceInfo(-12, -8))}, {new ListItemForEmeralds(Items.bow, new PriceInfo(2, 3)), new ItemAndEmeraldToItem(Item.getItemFromBlock(Blocks.gravel), new PriceInfo(10, 10), Items.flint, new PriceInfo(6, 10))}}}, {{{new EmeraldForItems(Items.paper, new PriceInfo(24, 36)), new ListEnchantedBookForEmeralds()}, {new EmeraldForItems(Items.book, new PriceInfo(8, 10)), new ListItemForEmeralds(Items.compass, new PriceInfo(10, 12)), new ListItemForEmeralds(Item.getItemFromBlock(Blocks.bookshelf), new PriceInfo(3, 4))}, {new EmeraldForItems(Items.written_book, new PriceInfo(2, 2)), new ListItemForEmeralds(Items.clock, new PriceInfo(10, 12)), new ListItemForEmeralds(Item.getItemFromBlock(Blocks.glass), new PriceInfo(-5, -3))}, {new ListEnchantedBookForEmeralds()}, {new ListEnchantedBookForEmeralds()}, {new ListItemForEmeralds(Items.name_tag, new PriceInfo(20, 22))}}}, {{{new EmeraldForItems(Items.rotten_flesh, new PriceInfo(36, 40)), new EmeraldForItems(Items.gold_ingot, new PriceInfo(8, 10))}, {new ListItemForEmeralds(Items.redstone, new PriceInfo(-4, -1)), new ListItemForEmeralds(new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()), new PriceInfo(-2, -1))}, {new ListItemForEmeralds(Items.ender_eye, new PriceInfo(7, 11)), new ListItemForEmeralds(Item.getItemFromBlock(Blocks.glowstone), new PriceInfo(-3, -1))}, {new ListItemForEmeralds(Items.experience_bottle, new PriceInfo(3, 11))}}}, {{{new EmeraldForItems(Items.coal, new PriceInfo(16, 24)), new ListItemForEmeralds(Items.iron_helmet, new PriceInfo(4, 6))}, {new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9)), new ListItemForEmeralds(Items.iron_chestplate, new PriceInfo(10, 14))}, {new EmeraldForItems(Items.diamond, new PriceInfo(3, 4)), new ListEnchantedItemForEmeralds(Items.diamond_chestplate, new PriceInfo(16, 19))}, {new ListItemForEmeralds(Items.chainmail_boots, new PriceInfo(5, 7)), new ListItemForEmeralds(Items.chainmail_leggings, new PriceInfo(9, 11)), new ListItemForEmeralds(Items.chainmail_helmet, new PriceInfo(5, 7)), new ListItemForEmeralds(Items.chainmail_chestplate, new PriceInfo(11, 15))}}, {{new EmeraldForItems(Items.coal, new PriceInfo(16, 24)), new ListItemForEmeralds(Items.iron_axe, new PriceInfo(6, 8))}, {new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9)), new ListEnchantedItemForEmeralds(Items.iron_sword, new PriceInfo(9, 10))}, {new EmeraldForItems(Items.diamond, new PriceInfo(3, 4)), new ListEnchantedItemForEmeralds(Items.diamond_sword, new PriceInfo(12, 15)), new ListEnchantedItemForEmeralds(Items.diamond_axe, new PriceInfo(9, 12))}}, {{new EmeraldForItems(Items.coal, new PriceInfo(16, 24)), new ListEnchantedItemForEmeralds(Items.iron_shovel, new PriceInfo(5, 7))}, {new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9)), new ListEnchantedItemForEmeralds(Items.iron_pickaxe, new PriceInfo(9, 11))}, {new EmeraldForItems(Items.diamond, new PriceInfo(3, 4)), new ListEnchantedItemForEmeralds(Items.diamond_pickaxe, new PriceInfo(12, 15))}}}, {{{new EmeraldForItems(Items.porkchop, new PriceInfo(14, 18)), new EmeraldForItems(Items.chicken, new PriceInfo(14, 18))}, {new EmeraldForItems(Items.coal, new PriceInfo(16, 24)), new ListItemForEmeralds(Items.cooked_porkchop, new PriceInfo(-7, -5)), new ListItemForEmeralds(Items.cooked_chicken, new PriceInfo(-8, -6))}}, {{new EmeraldForItems(Items.leather, new PriceInfo(9, 12)), new ListItemForEmeralds(Items.leather_leggings, new PriceInfo(2, 4))}, {new ListEnchantedItemForEmeralds(Items.leather_chestplate, new PriceInfo(7, 12))}, {new ListItemForEmeralds(Items.saddle, new PriceInfo(8, 10))}}}};

    public EntityVillager(World worldIn) {
        this(worldIn, 0);
    }

    public EntityVillager(World worldIn, int professionId) {
        super(worldIn);
        this.setProfession(professionId);
        this.setSize(0.6f, 1.8f);
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
        ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoidEntity<EntityZombie>(this, EntityZombie.class, 8.0f, 0.6, 0.6));
        this.tasks.addTask(1, new EntityAITradePlayer(this));
        this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6));
        this.tasks.addTask(6, new EntityAIVillagerMate(this));
        this.tasks.addTask(7, new EntityAIFollowGolem(this));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0f, 1.0f));
        this.tasks.addTask(9, new EntityAIVillagerInteract(this));
        this.tasks.addTask(9, new EntityAIWander(this, 0.6));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f));
        this.setCanPickUpLoot(true);
    }

    private void setAdditionalAItasks() {
        if (!this.areAdditionalTasksSet) {
            this.areAdditionalTasksSet = true;
            if (this.isChild()) {
                this.tasks.addTask(8, new EntityAIPlay(this, 0.32));
            } else if (this.getProfession() == 0) {
                this.tasks.addTask(6, new EntityAIHarvestFarmland(this, 0.6));
            }
        }
    }

    @Override
    protected void onGrowingAdult() {
        if (this.getProfession() == 0) {
            this.tasks.addTask(8, new EntityAIHarvestFarmland(this, 0.6));
        }
        super.onGrowingAdult();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
    }

    @Override
    protected void updateAITasks() {
        if (--this.randomTickDivider <= 0) {
            BlockPos blockpos = new BlockPos(this);
            this.worldObj.getVillageCollection().addToVillagerPositionList(blockpos);
            this.randomTickDivider = 70 + this.rand.nextInt(50);
            this.villageObj = this.worldObj.getVillageCollection().getNearestVillage(blockpos, 32);
            if (this.villageObj == null) {
                this.detachHome();
            } else {
                BlockPos blockpos1 = this.villageObj.getCenter();
                this.setHomePosAndDistance(blockpos1, (int)((float)this.villageObj.getVillageRadius() * 1.0f));
                if (this.isLookingForHome) {
                    this.isLookingForHome = false;
                    this.villageObj.setDefaultPlayerReputation(5);
                }
            }
        }
        if (!this.isTrading() && this.timeUntilReset > 0) {
            --this.timeUntilReset;
            if (this.timeUntilReset <= 0) {
                if (this.needsInitilization) {
                    for (MerchantRecipe merchantrecipe : this.buyingList) {
                        if (!merchantrecipe.isRecipeDisabled()) continue;
                        merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                    }
                    this.populateBuyingList();
                    this.needsInitilization = false;
                    if (this.villageObj != null && this.lastBuyingPlayer != null) {
                        this.worldObj.setEntityState(this, (byte)14);
                        this.villageObj.setReputationForPlayer(this.lastBuyingPlayer, 1);
                    }
                }
                this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 0));
            }
        }
        super.updateAITasks();
    }

    @Override
    public boolean interact(EntityPlayer player) {
        boolean flag;
        ItemStack itemstack = player.inventory.getCurrentItem();
        boolean bl = flag = itemstack != null && itemstack.getItem() == Items.spawn_egg;
        if (!flag && this.isEntityAlive() && !this.isTrading() && !this.isChild()) {
            if (!(this.worldObj.isRemote || this.buyingList != null && this.buyingList.size() <= 0)) {
                this.setCustomer(player);
                player.displayVillagerTradeGui(this);
            }
            player.triggerAchievement(StatList.timesTalkedToVillagerStat);
            return true;
        }
        return super.interact(player);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, 0);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Profession", this.getProfession());
        tagCompound.setInteger("Riches", this.wealth);
        tagCompound.setInteger("Career", this.careerId);
        tagCompound.setInteger("CareerLevel", this.careerLevel);
        tagCompound.setBoolean("Willing", this.isWillingToTrade);
        if (this.buyingList != null) {
            tagCompound.setTag("Offers", this.buyingList.getRecipiesAsTags());
        }
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.villagerInventory.getStackInSlot(i);
            if (itemstack == null) continue;
            nbttaglist.appendTag(itemstack.writeToNBT(new NBTTagCompound()));
        }
        tagCompound.setTag("Inventory", nbttaglist);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setProfession(tagCompund.getInteger("Profession"));
        this.wealth = tagCompund.getInteger("Riches");
        this.careerId = tagCompund.getInteger("Career");
        this.careerLevel = tagCompund.getInteger("CareerLevel");
        this.isWillingToTrade = tagCompund.getBoolean("Willing");
        if (tagCompund.hasKey("Offers", 10)) {
            NBTTagCompound nbttagcompound = tagCompund.getCompoundTag("Offers");
            this.buyingList = new MerchantRecipeList(nbttagcompound);
        }
        NBTTagList nbttaglist = tagCompund.getTagList("Inventory", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttaglist.getCompoundTagAt(i));
            if (itemstack == null) continue;
            this.villagerInventory.func_174894_a(itemstack);
        }
        this.setCanPickUpLoot(true);
        this.setAdditionalAItasks();
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected String getLivingSound() {
        return this.isTrading() ? "mob.villager.haggle" : "mob.villager.idle";
    }

    @Override
    public String getHurtSound() {
        return "mob.villager.hit";
    }

    @Override
    protected String getDeathSound() {
        return "mob.villager.death";
    }

    public void setProfession(int professionId) {
        this.dataWatcher.updateObject(16, professionId);
    }

    public int getProfession() {
        return Math.max(this.dataWatcher.getWatchableObjectInt(16) % 5, 0);
    }

    public boolean isMating() {
        return this.isMating;
    }

    public void setMating(boolean mating) {
        this.isMating = mating;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    @Override
    public void setRevengeTarget(EntityLivingBase livingBase) {
        super.setRevengeTarget(livingBase);
        if (this.villageObj != null && livingBase != null) {
            this.villageObj.addOrRenewAgressor(livingBase);
            if (livingBase instanceof EntityPlayer) {
                int i = -1;
                if (this.isChild()) {
                    i = -3;
                }
                this.villageObj.setReputationForPlayer(livingBase.getCommandSenderName(), i);
                if (this.isEntityAlive()) {
                    this.worldObj.setEntityState(this, (byte)13);
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (this.villageObj != null) {
            Entity entity = cause.getEntity();
            if (entity != null) {
                if (entity instanceof EntityPlayer) {
                    this.villageObj.setReputationForPlayer(entity.getCommandSenderName(), -2);
                } else if (entity instanceof IMob) {
                    this.villageObj.endMatingSeason();
                }
            } else {
                EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 16.0);
                if (entityplayer != null) {
                    this.villageObj.endMatingSeason();
                }
            }
        }
        super.onDeath(cause);
    }

    @Override
    public void setCustomer(EntityPlayer p_70932_1_) {
        this.buyingPlayer = p_70932_1_;
    }

    @Override
    public EntityPlayer getCustomer() {
        return this.buyingPlayer;
    }

    public boolean isTrading() {
        return this.buyingPlayer != null;
    }

    public boolean getIsWillingToTrade(boolean updateFirst) {
        if (!this.isWillingToTrade && updateFirst && this.func_175553_cp()) {
            boolean flag = false;
            for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.villagerInventory.getStackInSlot(i);
                if (itemstack != null) {
                    if (itemstack.getItem() == Items.bread && itemstack.stackSize >= 3) {
                        flag = true;
                        this.villagerInventory.decrStackSize(i, 3);
                    } else if ((itemstack.getItem() == Items.potato || itemstack.getItem() == Items.carrot) && itemstack.stackSize >= 12) {
                        flag = true;
                        this.villagerInventory.decrStackSize(i, 12);
                    }
                }
                if (!flag) continue;
                this.worldObj.setEntityState(this, (byte)18);
                this.isWillingToTrade = true;
                break;
            }
        }
        return this.isWillingToTrade;
    }

    public void setIsWillingToTrade(boolean willingToTrade) {
        this.isWillingToTrade = willingToTrade;
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        recipe.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
        int i = 3 + this.rand.nextInt(4);
        if (recipe.getToolUses() == 1 || this.rand.nextInt(5) == 0) {
            this.timeUntilReset = 40;
            this.needsInitilization = true;
            this.isWillingToTrade = true;
            this.lastBuyingPlayer = this.buyingPlayer != null ? this.buyingPlayer.getCommandSenderName() : null;
            i += 5;
        }
        if (recipe.getItemToBuy().getItem() == Items.emerald) {
            this.wealth += recipe.getItemToBuy().stackSize;
        }
        if (recipe.getRewardsExp()) {
            this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY + 0.5, this.posZ, i));
        }
    }

    @Override
    public void verifySellingItem(ItemStack stack) {
        if (!this.worldObj.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
            this.livingSoundTime = -this.getTalkInterval();
            if (stack != null) {
                this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
            } else {
                this.playSound("mob.villager.no", this.getSoundVolume(), this.getSoundPitch());
            }
        }
    }

    @Override
    public MerchantRecipeList getRecipes(EntityPlayer p_70934_1_) {
        if (this.buyingList == null) {
            this.populateBuyingList();
        }
        return this.buyingList;
    }

    private void populateBuyingList() {
        ITradeList[][][] aentityvillager$itradelist = DEFAULT_TRADE_LIST_MAP[this.getProfession()];
        if (this.careerId != 0 && this.careerLevel != 0) {
            ++this.careerLevel;
        } else {
            this.careerId = this.rand.nextInt(aentityvillager$itradelist.length) + 1;
            this.careerLevel = 1;
        }
        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }
        int i = this.careerId - 1;
        int j = this.careerLevel - 1;
        ITradeList[][] aentityvillager$itradelist1 = aentityvillager$itradelist[i];
        if (j >= 0 && j < aentityvillager$itradelist1.length) {
            ITradeList[] aentityvillager$itradelist2;
            for (ITradeList entityvillager$itradelist : aentityvillager$itradelist2 = aentityvillager$itradelist1[j]) {
                entityvillager$itradelist.modifyMerchantRecipeList(this.buyingList, this.rand);
            }
        }
    }

    @Override
    public void setRecipes(MerchantRecipeList recipeList) {
    }

    @Override
    public IChatComponent getDisplayName() {
        String s = this.getCustomNameTag();
        if (s != null && s.length() > 0) {
            ChatComponentText chatcomponenttext = new ChatComponentText(s);
            chatcomponenttext.getChatStyle().setChatHoverEvent(this.getHoverEvent());
            chatcomponenttext.getChatStyle().setInsertion(this.getUniqueID().toString());
            return chatcomponenttext;
        }
        if (this.buyingList == null) {
            this.populateBuyingList();
        }
        String s1 = null;
        switch (this.getProfession()) {
            case 0: {
                if (this.careerId == 1) {
                    s1 = "farmer";
                    break;
                }
                if (this.careerId == 2) {
                    s1 = "fisherman";
                    break;
                }
                if (this.careerId == 3) {
                    s1 = "shepherd";
                    break;
                }
                if (this.careerId != 4) break;
                s1 = "fletcher";
                break;
            }
            case 1: {
                s1 = "librarian";
                break;
            }
            case 2: {
                s1 = "cleric";
                break;
            }
            case 3: {
                if (this.careerId == 1) {
                    s1 = "armor";
                    break;
                }
                if (this.careerId == 2) {
                    s1 = "weapon";
                    break;
                }
                if (this.careerId != 3) break;
                s1 = "tool";
                break;
            }
            case 4: {
                if (this.careerId == 1) {
                    s1 = "butcher";
                    break;
                }
                if (this.careerId != 2) break;
                s1 = "leather";
            }
        }
        if (s1 != null) {
            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("entity.Villager." + s1, new Object[0]);
            chatcomponenttranslation.getChatStyle().setChatHoverEvent(this.getHoverEvent());
            chatcomponenttranslation.getChatStyle().setInsertion(this.getUniqueID().toString());
            return chatcomponenttranslation;
        }
        return super.getDisplayName();
    }

    @Override
    public float getEyeHeight() {
        float f = 1.62f;
        if (this.isChild()) {
            f = (float)((double)f - 0.81);
        }
        return f;
    }

    @Override
    public void handleHealthUpdate(byte id) {
        if (id == 12) {
            this.spawnParticles(EnumParticleTypes.HEART);
        } else if (id == 13) {
            this.spawnParticles(EnumParticleTypes.VILLAGER_ANGRY);
        } else if (id == 14) {
            this.spawnParticles(EnumParticleTypes.VILLAGER_HAPPY);
        } else {
            super.handleHealthUpdate(id);
        }
    }

    private void spawnParticles(EnumParticleTypes particleType) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02;
            double d1 = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            this.worldObj.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 1.0 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d0, d1, d2, new int[0]);
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setProfession(this.worldObj.rand.nextInt(5));
        this.setAdditionalAItasks();
        return livingdata;
    }

    public void setLookingForHome() {
        this.isLookingForHome = true;
    }

    @Override
    public EntityVillager createChild(EntityAgeable ageable) {
        EntityVillager entityvillager = new EntityVillager(this.worldObj);
        entityvillager.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityvillager)), null);
        return entityvillager;
    }

    @Override
    public boolean allowLeashing() {
        return false;
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
        if (!this.worldObj.isRemote && !this.isDead) {
            EntityWitch entitywitch = new EntityWitch(this.worldObj);
            entitywitch.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            entitywitch.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entitywitch)), null);
            entitywitch.setNoAI(this.isAIDisabled());
            if (this.hasCustomName()) {
                entitywitch.setCustomNameTag(this.getCustomNameTag());
                entitywitch.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
            }
            this.worldObj.spawnEntityInWorld(entitywitch);
            this.setDead();
        }
    }

    public InventoryBasic getVillagerInventory() {
        return this.villagerInventory;
    }

    @Override
    protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
        ItemStack itemstack = itemEntity.getEntityItem();
        Item item = itemstack.getItem();
        if (this.canVillagerPickupItem(item)) {
            ItemStack itemstack1 = this.villagerInventory.func_174894_a(itemstack);
            if (itemstack1 == null) {
                itemEntity.setDead();
            } else {
                itemstack.stackSize = itemstack1.stackSize;
            }
        }
    }

    private boolean canVillagerPickupItem(Item itemIn) {
        return itemIn == Items.bread || itemIn == Items.potato || itemIn == Items.carrot || itemIn == Items.wheat || itemIn == Items.wheat_seeds;
    }

    public boolean func_175553_cp() {
        return this.hasEnoughItems(1);
    }

    public boolean canAbondonItems() {
        return this.hasEnoughItems(2);
    }

    public boolean func_175557_cr() {
        boolean flag;
        boolean bl = flag = this.getProfession() == 0;
        return flag ? !this.hasEnoughItems(5) : !this.hasEnoughItems(1);
    }

    private boolean hasEnoughItems(int multiplier) {
        boolean flag = this.getProfession() == 0;
        for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.villagerInventory.getStackInSlot(i);
            if (itemstack == null) continue;
            if (itemstack.getItem() == Items.bread && itemstack.stackSize >= 3 * multiplier || itemstack.getItem() == Items.potato && itemstack.stackSize >= 12 * multiplier || itemstack.getItem() == Items.carrot && itemstack.stackSize >= 12 * multiplier) {
                return true;
            }
            if (!flag || itemstack.getItem() != Items.wheat || itemstack.stackSize < 9 * multiplier) continue;
            return true;
        }
        return false;
    }

    public boolean isFarmItemInInventory() {
        for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.villagerInventory.getStackInSlot(i);
            if (itemstack == null || itemstack.getItem() != Items.wheat_seeds && itemstack.getItem() != Items.potato && itemstack.getItem() != Items.carrot) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        if (super.replaceItemInInventory(inventorySlot, itemStackIn)) {
            return true;
        }
        int i = inventorySlot - 300;
        if (i >= 0 && i < this.villagerInventory.getSizeInventory()) {
            this.villagerInventory.setInventorySlotContents(i, itemStackIn);
            return true;
        }
        return false;
    }

    static class PriceInfo
    extends Tuple<Integer, Integer> {
        public PriceInfo(int p_i45810_1_, int p_i45810_2_) {
            super(p_i45810_1_, p_i45810_2_);
        }

        public int getPrice(Random rand) {
            return (Integer)this.getFirst() >= (Integer)this.getSecond() ? (Integer)this.getFirst() : (Integer)this.getFirst() + rand.nextInt((Integer)this.getSecond() - (Integer)this.getFirst() + 1);
        }
    }

    static class ListItemForEmeralds
    implements ITradeList {
        public ItemStack field_179403_a;
        public PriceInfo field_179402_b;

        public ListItemForEmeralds(Item par1Item, PriceInfo priceInfo) {
            this.field_179403_a = new ItemStack(par1Item);
            this.field_179402_b = priceInfo;
        }

        public ListItemForEmeralds(ItemStack stack, PriceInfo priceInfo) {
            this.field_179403_a = stack;
            this.field_179402_b = priceInfo;
        }

        @Override
        public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
            ItemStack itemstack1;
            ItemStack itemstack;
            int i = 1;
            if (this.field_179402_b != null) {
                i = this.field_179402_b.getPrice(random);
            }
            if (i < 0) {
                itemstack = new ItemStack(Items.emerald, 1, 0);
                itemstack1 = new ItemStack(this.field_179403_a.getItem(), -i, this.field_179403_a.getMetadata());
            } else {
                itemstack = new ItemStack(Items.emerald, i, 0);
                itemstack1 = new ItemStack(this.field_179403_a.getItem(), 1, this.field_179403_a.getMetadata());
            }
            recipeList.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    static class ListEnchantedItemForEmeralds
    implements ITradeList {
        public ItemStack field_179407_a;
        public PriceInfo field_179406_b;

        public ListEnchantedItemForEmeralds(Item p_i45814_1_, PriceInfo p_i45814_2_) {
            this.field_179407_a = new ItemStack(p_i45814_1_);
            this.field_179406_b = p_i45814_2_;
        }

        @Override
        public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
            int i = 1;
            if (this.field_179406_b != null) {
                i = this.field_179406_b.getPrice(random);
            }
            ItemStack itemstack = new ItemStack(Items.emerald, i, 0);
            ItemStack itemstack1 = new ItemStack(this.field_179407_a.getItem(), 1, this.field_179407_a.getMetadata());
            itemstack1 = EnchantmentHelper.addRandomEnchantment(random, itemstack1, 5 + random.nextInt(15));
            recipeList.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    static class ListEnchantedBookForEmeralds
    implements ITradeList {
        ListEnchantedBookForEmeralds() {
        }

        @Override
        public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
            Enchantment enchantment = Enchantment.enchantmentsBookList[random.nextInt(Enchantment.enchantmentsBookList.length)];
            int i = MathHelper.getRandomIntegerInRange(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
            ItemStack itemstack = Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, i));
            int j = 2 + random.nextInt(5 + i * 10) + 3 * i;
            if (j > 64) {
                j = 64;
            }
            recipeList.add(new MerchantRecipe(new ItemStack(Items.book), new ItemStack(Items.emerald, j), itemstack));
        }
    }

    static class ItemAndEmeraldToItem
    implements ITradeList {
        public ItemStack field_179411_a;
        public PriceInfo field_179409_b;
        public ItemStack field_179410_c;
        public PriceInfo field_179408_d;

        public ItemAndEmeraldToItem(Item p_i45813_1_, PriceInfo p_i45813_2_, Item p_i45813_3_, PriceInfo p_i45813_4_) {
            this.field_179411_a = new ItemStack(p_i45813_1_);
            this.field_179409_b = p_i45813_2_;
            this.field_179410_c = new ItemStack(p_i45813_3_);
            this.field_179408_d = p_i45813_4_;
        }

        @Override
        public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
            int i = 1;
            if (this.field_179409_b != null) {
                i = this.field_179409_b.getPrice(random);
            }
            int j = 1;
            if (this.field_179408_d != null) {
                j = this.field_179408_d.getPrice(random);
            }
            recipeList.add(new MerchantRecipe(new ItemStack(this.field_179411_a.getItem(), i, this.field_179411_a.getMetadata()), new ItemStack(Items.emerald), new ItemStack(this.field_179410_c.getItem(), j, this.field_179410_c.getMetadata())));
        }
    }

    static interface ITradeList {
        public void modifyMerchantRecipeList(MerchantRecipeList var1, Random var2);
    }

    static class EmeraldForItems
    implements ITradeList {
        public Item sellItem;
        public PriceInfo price;

        public EmeraldForItems(Item itemIn, PriceInfo priceIn) {
            this.sellItem = itemIn;
            this.price = priceIn;
        }

        @Override
        public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
            int i = 1;
            if (this.price != null) {
                i = this.price.getPrice(random);
            }
            recipeList.add(new MerchantRecipe(new ItemStack(this.sellItem, i, 0), Items.emerald));
        }
    }
}

