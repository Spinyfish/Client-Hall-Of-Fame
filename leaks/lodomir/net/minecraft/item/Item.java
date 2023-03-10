/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 */
package net.minecraft.item;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockWall;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmorStand;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemDoublePlant;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemEnderEye;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemLilyPad;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemRedstone;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemSimpleFoiled;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemSnow;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Item {
    public static final RegistryNamespaced<ResourceLocation, Item> itemRegistry = new RegistryNamespaced();
    private static final Map<Block, Item> BLOCK_TO_ITEM = Maps.newHashMap();
    protected static final UUID itemModifierUUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private CreativeTabs tabToDisplayOn;
    protected static Random itemRand = new Random();
    protected int maxStackSize = 64;
    private int maxDamage;
    protected boolean bFull3D;
    protected boolean hasSubtypes;
    private Item containerItem;
    private String potionEffect;
    private String unlocalizedName;

    public static int getIdFromItem(Item itemIn) {
        return itemIn == null ? 0 : itemRegistry.getIDForObject(itemIn);
    }

    public static Item getItemById(int id) {
        return itemRegistry.getObjectById(id);
    }

    public static Item getItemFromBlock(Block blockIn) {
        return BLOCK_TO_ITEM.get(blockIn);
    }

    public static Item getByNameOrId(String id) {
        Item item = itemRegistry.getObject(new ResourceLocation(id));
        if (item == null) {
            try {
                return Item.getItemById(Integer.parseInt(id));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return item;
    }

    public boolean updateItemStackNBT(NBTTagCompound nbt) {
        return false;
    }

    public Item setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    public float getStrVsBlock(ItemStack stack, Block block) {
        return 1.0f;
    }

    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        return itemStackIn;
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        return stack;
    }

    public int getItemStackLimit() {
        return this.maxStackSize;
    }

    public int getMetadata(int damage) {
        return 0;
    }

    public boolean getHasSubtypes() {
        return this.hasSubtypes;
    }

    protected Item setHasSubtypes(boolean hasSubtypes) {
        this.hasSubtypes = hasSubtypes;
        return this;
    }

    public int getMaxDamage() {
        return this.maxDamage;
    }

    protected Item setMaxDamage(int maxDamageIn) {
        this.maxDamage = maxDamageIn;
        return this;
    }

    public boolean isDamageable() {
        return this.maxDamage > 0 && !this.hasSubtypes;
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return false;
    }

    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn) {
        return false;
    }

    public boolean canHarvestBlock(Block blockIn) {
        return false;
    }

    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target) {
        return false;
    }

    public Item setFull3D() {
        this.bFull3D = true;
        return this;
    }

    public boolean isFull3D() {
        return this.bFull3D;
    }

    public boolean shouldRotateAroundWhenRendering() {
        return false;
    }

    public Item setUnlocalizedName(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
        return this;
    }

    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        String s = this.getUnlocalizedName(stack);
        return s == null ? "" : StatCollector.translateToLocal(s);
    }

    public String getUnlocalizedName() {
        return "item." + this.unlocalizedName;
    }

    public String getUnlocalizedName(ItemStack stack) {
        return "item." + this.unlocalizedName;
    }

    public Item setContainerItem(Item containerItem) {
        this.containerItem = containerItem;
        return this;
    }

    public boolean getShareTag() {
        return true;
    }

    public Item getContainerItem() {
        return this.containerItem;
    }

    public boolean hasContainerItem() {
        return this.containerItem != null;
    }

    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return 0xFFFFFF;
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    }

    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
    }

    public boolean isMap() {
        return false;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 0;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
    }

    protected Item setPotionEffect(String potionEffect) {
        this.potionEffect = potionEffect;
        return this;
    }

    public String getPotionEffect(ItemStack stack) {
        return this.potionEffect;
    }

    public boolean isPotionIngredient(ItemStack stack) {
        return this.getPotionEffect(stack) != null;
    }

    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
    }

    public boolean hasEffect(ItemStack stack) {
        return stack.isItemEnchanted();
    }

    public EnumRarity getRarity(ItemStack stack) {
        return stack.isItemEnchanted() ? EnumRarity.RARE : EnumRarity.COMMON;
    }

    public boolean isItemTool(ItemStack stack) {
        return this.getItemStackLimit() == 1 && this.isDamageable();
    }

    protected MovingObjectPosition getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3 vec3 = new Vec3(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0;
        Vec3 vec31 = vec3.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return worldIn.rayTraceBlocks(vec3, vec31, useLiquids, !useLiquids, false);
    }

    public int getItemEnchantability() {
        return 0;
    }

    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        subItems.add(new ItemStack(itemIn, 1, 0));
    }

    public CreativeTabs getCreativeTab() {
        return this.tabToDisplayOn;
    }

    public Item setCreativeTab(CreativeTabs tab) {
        this.tabToDisplayOn = tab;
        return this;
    }

    public boolean canItemEditBlocks() {
        return false;
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
        return HashMultimap.create();
    }

    public static void registerItems() {
        Item.registerItemBlock(Blocks.stone, new ItemMultiTexture(Blocks.stone, Blocks.stone, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockStone.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("stone"));
        Item.registerItemBlock(Blocks.grass, new ItemColored(Blocks.grass, false));
        Item.registerItemBlock(Blocks.dirt, new ItemMultiTexture(Blocks.dirt, Blocks.dirt, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockDirt.DirtType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("dirt"));
        Item.registerItemBlock(Blocks.cobblestone);
        Item.registerItemBlock(Blocks.planks, new ItemMultiTexture(Blocks.planks, Blocks.planks, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("wood"));
        Item.registerItemBlock(Blocks.sapling, new ItemMultiTexture(Blocks.sapling, Blocks.sapling, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("sapling"));
        Item.registerItemBlock(Blocks.bedrock);
        Item.registerItemBlock(Blocks.sand, new ItemMultiTexture((Block)Blocks.sand, (Block)Blocks.sand, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockSand.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("sand"));
        Item.registerItemBlock(Blocks.gravel);
        Item.registerItemBlock(Blocks.gold_ore);
        Item.registerItemBlock(Blocks.iron_ore);
        Item.registerItemBlock(Blocks.coal_ore);
        Item.registerItemBlock(Blocks.log, new ItemMultiTexture(Blocks.log, Blocks.log, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("log"));
        Item.registerItemBlock(Blocks.log2, new ItemMultiTexture(Blocks.log2, Blocks.log2, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata() + 4).getUnlocalizedName();
            }
        }).setUnlocalizedName("log"));
        Item.registerItemBlock(Blocks.leaves, new ItemLeaves(Blocks.leaves).setUnlocalizedName("leaves"));
        Item.registerItemBlock(Blocks.leaves2, new ItemLeaves(Blocks.leaves2).setUnlocalizedName("leaves"));
        Item.registerItemBlock(Blocks.sponge, new ItemMultiTexture(Blocks.sponge, Blocks.sponge, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return (p_apply_1_.getMetadata() & 1) == 1 ? "wet" : "dry";
            }
        }).setUnlocalizedName("sponge"));
        Item.registerItemBlock(Blocks.glass);
        Item.registerItemBlock(Blocks.lapis_ore);
        Item.registerItemBlock(Blocks.lapis_block);
        Item.registerItemBlock(Blocks.dispenser);
        Item.registerItemBlock(Blocks.sandstone, new ItemMultiTexture(Blocks.sandstone, Blocks.sandstone, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockSandStone.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("sandStone"));
        Item.registerItemBlock(Blocks.noteblock);
        Item.registerItemBlock(Blocks.golden_rail);
        Item.registerItemBlock(Blocks.detector_rail);
        Item.registerItemBlock(Blocks.sticky_piston, new ItemPiston(Blocks.sticky_piston));
        Item.registerItemBlock(Blocks.web);
        Item.registerItemBlock(Blocks.tallgrass, new ItemColored(Blocks.tallgrass, true).setSubtypeNames(new String[]{"shrub", "grass", "fern"}));
        Item.registerItemBlock(Blocks.deadbush);
        Item.registerItemBlock(Blocks.piston, new ItemPiston(Blocks.piston));
        Item.registerItemBlock(Blocks.wool, new ItemCloth(Blocks.wool).setUnlocalizedName("cloth"));
        Item.registerItemBlock(Blocks.yellow_flower, new ItemMultiTexture((Block)Blocks.yellow_flower, (Block)Blocks.yellow_flower, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.YELLOW, p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("flower"));
        Item.registerItemBlock(Blocks.red_flower, new ItemMultiTexture((Block)Blocks.red_flower, (Block)Blocks.red_flower, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("rose"));
        Item.registerItemBlock(Blocks.brown_mushroom);
        Item.registerItemBlock(Blocks.red_mushroom);
        Item.registerItemBlock(Blocks.gold_block);
        Item.registerItemBlock(Blocks.iron_block);
        Item.registerItemBlock(Blocks.stone_slab, new ItemSlab(Blocks.stone_slab, Blocks.stone_slab, Blocks.double_stone_slab).setUnlocalizedName("stoneSlab"));
        Item.registerItemBlock(Blocks.brick_block);
        Item.registerItemBlock(Blocks.tnt);
        Item.registerItemBlock(Blocks.bookshelf);
        Item.registerItemBlock(Blocks.mossy_cobblestone);
        Item.registerItemBlock(Blocks.obsidian);
        Item.registerItemBlock(Blocks.torch);
        Item.registerItemBlock(Blocks.mob_spawner);
        Item.registerItemBlock(Blocks.oak_stairs);
        Item.registerItemBlock(Blocks.chest);
        Item.registerItemBlock(Blocks.diamond_ore);
        Item.registerItemBlock(Blocks.diamond_block);
        Item.registerItemBlock(Blocks.crafting_table);
        Item.registerItemBlock(Blocks.farmland);
        Item.registerItemBlock(Blocks.furnace);
        Item.registerItemBlock(Blocks.lit_furnace);
        Item.registerItemBlock(Blocks.ladder);
        Item.registerItemBlock(Blocks.rail);
        Item.registerItemBlock(Blocks.stone_stairs);
        Item.registerItemBlock(Blocks.lever);
        Item.registerItemBlock(Blocks.stone_pressure_plate);
        Item.registerItemBlock(Blocks.wooden_pressure_plate);
        Item.registerItemBlock(Blocks.redstone_ore);
        Item.registerItemBlock(Blocks.redstone_torch);
        Item.registerItemBlock(Blocks.stone_button);
        Item.registerItemBlock(Blocks.snow_layer, new ItemSnow(Blocks.snow_layer));
        Item.registerItemBlock(Blocks.ice);
        Item.registerItemBlock(Blocks.snow);
        Item.registerItemBlock(Blocks.cactus);
        Item.registerItemBlock(Blocks.clay);
        Item.registerItemBlock(Blocks.jukebox);
        Item.registerItemBlock(Blocks.oak_fence);
        Item.registerItemBlock(Blocks.spruce_fence);
        Item.registerItemBlock(Blocks.birch_fence);
        Item.registerItemBlock(Blocks.jungle_fence);
        Item.registerItemBlock(Blocks.dark_oak_fence);
        Item.registerItemBlock(Blocks.acacia_fence);
        Item.registerItemBlock(Blocks.pumpkin);
        Item.registerItemBlock(Blocks.netherrack);
        Item.registerItemBlock(Blocks.soul_sand);
        Item.registerItemBlock(Blocks.glowstone);
        Item.registerItemBlock(Blocks.lit_pumpkin);
        Item.registerItemBlock(Blocks.trapdoor);
        Item.registerItemBlock(Blocks.monster_egg, new ItemMultiTexture(Blocks.monster_egg, Blocks.monster_egg, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockSilverfish.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("monsterStoneEgg"));
        Item.registerItemBlock(Blocks.stonebrick, new ItemMultiTexture(Blocks.stonebrick, Blocks.stonebrick, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockStoneBrick.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("stonebricksmooth"));
        Item.registerItemBlock(Blocks.brown_mushroom_block);
        Item.registerItemBlock(Blocks.red_mushroom_block);
        Item.registerItemBlock(Blocks.iron_bars);
        Item.registerItemBlock(Blocks.glass_pane);
        Item.registerItemBlock(Blocks.melon_block);
        Item.registerItemBlock(Blocks.vine, new ItemColored(Blocks.vine, false));
        Item.registerItemBlock(Blocks.oak_fence_gate);
        Item.registerItemBlock(Blocks.spruce_fence_gate);
        Item.registerItemBlock(Blocks.birch_fence_gate);
        Item.registerItemBlock(Blocks.jungle_fence_gate);
        Item.registerItemBlock(Blocks.dark_oak_fence_gate);
        Item.registerItemBlock(Blocks.acacia_fence_gate);
        Item.registerItemBlock(Blocks.brick_stairs);
        Item.registerItemBlock(Blocks.stone_brick_stairs);
        Item.registerItemBlock(Blocks.mycelium);
        Item.registerItemBlock(Blocks.waterlily, new ItemLilyPad(Blocks.waterlily));
        Item.registerItemBlock(Blocks.nether_brick);
        Item.registerItemBlock(Blocks.nether_brick_fence);
        Item.registerItemBlock(Blocks.nether_brick_stairs);
        Item.registerItemBlock(Blocks.enchanting_table);
        Item.registerItemBlock(Blocks.end_portal_frame);
        Item.registerItemBlock(Blocks.end_stone);
        Item.registerItemBlock(Blocks.dragon_egg);
        Item.registerItemBlock(Blocks.redstone_lamp);
        Item.registerItemBlock(Blocks.wooden_slab, new ItemSlab(Blocks.wooden_slab, Blocks.wooden_slab, Blocks.double_wooden_slab).setUnlocalizedName("woodSlab"));
        Item.registerItemBlock(Blocks.sandstone_stairs);
        Item.registerItemBlock(Blocks.emerald_ore);
        Item.registerItemBlock(Blocks.ender_chest);
        Item.registerItemBlock(Blocks.tripwire_hook);
        Item.registerItemBlock(Blocks.emerald_block);
        Item.registerItemBlock(Blocks.spruce_stairs);
        Item.registerItemBlock(Blocks.birch_stairs);
        Item.registerItemBlock(Blocks.jungle_stairs);
        Item.registerItemBlock(Blocks.command_block);
        Item.registerItemBlock(Blocks.beacon);
        Item.registerItemBlock(Blocks.cobblestone_wall, new ItemMultiTexture(Blocks.cobblestone_wall, Blocks.cobblestone_wall, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockWall.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("cobbleWall"));
        Item.registerItemBlock(Blocks.wooden_button);
        Item.registerItemBlock(Blocks.anvil, new ItemAnvilBlock(Blocks.anvil).setUnlocalizedName("anvil"));
        Item.registerItemBlock(Blocks.trapped_chest);
        Item.registerItemBlock(Blocks.light_weighted_pressure_plate);
        Item.registerItemBlock(Blocks.heavy_weighted_pressure_plate);
        Item.registerItemBlock(Blocks.daylight_detector);
        Item.registerItemBlock(Blocks.redstone_block);
        Item.registerItemBlock(Blocks.quartz_ore);
        Item.registerItemBlock(Blocks.hopper);
        Item.registerItemBlock(Blocks.quartz_block, new ItemMultiTexture(Blocks.quartz_block, Blocks.quartz_block, new String[]{"default", "chiseled", "lines"}).setUnlocalizedName("quartzBlock"));
        Item.registerItemBlock(Blocks.quartz_stairs);
        Item.registerItemBlock(Blocks.activator_rail);
        Item.registerItemBlock(Blocks.dropper);
        Item.registerItemBlock(Blocks.stained_hardened_clay, new ItemCloth(Blocks.stained_hardened_clay).setUnlocalizedName("clayHardenedStained"));
        Item.registerItemBlock(Blocks.barrier);
        Item.registerItemBlock(Blocks.iron_trapdoor);
        Item.registerItemBlock(Blocks.hay_block);
        Item.registerItemBlock(Blocks.carpet, new ItemCloth(Blocks.carpet).setUnlocalizedName("woolCarpet"));
        Item.registerItemBlock(Blocks.hardened_clay);
        Item.registerItemBlock(Blocks.coal_block);
        Item.registerItemBlock(Blocks.packed_ice);
        Item.registerItemBlock(Blocks.acacia_stairs);
        Item.registerItemBlock(Blocks.dark_oak_stairs);
        Item.registerItemBlock(Blocks.slime_block);
        Item.registerItemBlock(Blocks.double_plant, new ItemDoublePlant((Block)Blocks.double_plant, (Block)Blocks.double_plant, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockDoublePlant.EnumPlantType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("doublePlant"));
        Item.registerItemBlock(Blocks.stained_glass, new ItemCloth(Blocks.stained_glass).setUnlocalizedName("stainedGlass"));
        Item.registerItemBlock(Blocks.stained_glass_pane, new ItemCloth(Blocks.stained_glass_pane).setUnlocalizedName("stainedGlassPane"));
        Item.registerItemBlock(Blocks.prismarine, new ItemMultiTexture(Blocks.prismarine, Blocks.prismarine, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockPrismarine.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("prismarine"));
        Item.registerItemBlock(Blocks.sea_lantern);
        Item.registerItemBlock(Blocks.red_sandstone, new ItemMultiTexture(Blocks.red_sandstone, Blocks.red_sandstone, new Function<ItemStack, String>(){

            public String apply(ItemStack p_apply_1_) {
                return BlockRedSandstone.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        }).setUnlocalizedName("redSandStone"));
        Item.registerItemBlock(Blocks.red_sandstone_stairs);
        Item.registerItemBlock(Blocks.stone_slab2, new ItemSlab(Blocks.stone_slab2, Blocks.stone_slab2, Blocks.double_stone_slab2).setUnlocalizedName("stoneSlab2"));
        Item.registerItem(256, "iron_shovel", new ItemSpade(ToolMaterial.IRON).setUnlocalizedName("shovelIron"));
        Item.registerItem(257, "iron_pickaxe", new ItemPickaxe(ToolMaterial.IRON).setUnlocalizedName("pickaxeIron"));
        Item.registerItem(258, "iron_axe", new ItemAxe(ToolMaterial.IRON).setUnlocalizedName("hatchetIron"));
        Item.registerItem(259, "flint_and_steel", new ItemFlintAndSteel().setUnlocalizedName("flintAndSteel"));
        Item.registerItem(260, "apple", new ItemFood(4, 0.3f, false).setUnlocalizedName("apple"));
        Item.registerItem(261, "bow", new ItemBow().setUnlocalizedName("bow"));
        Item.registerItem(262, "arrow", new Item().setUnlocalizedName("arrow").setCreativeTab(CreativeTabs.tabCombat));
        Item.registerItem(263, "coal", new ItemCoal().setUnlocalizedName("coal"));
        Item.registerItem(264, "diamond", new Item().setUnlocalizedName("diamond").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(265, "iron_ingot", new Item().setUnlocalizedName("ingotIron").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(266, "gold_ingot", new Item().setUnlocalizedName("ingotGold").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(267, "iron_sword", new ItemSword(ToolMaterial.IRON).setUnlocalizedName("swordIron"));
        Item.registerItem(268, "wooden_sword", new ItemSword(ToolMaterial.WOOD).setUnlocalizedName("swordWood"));
        Item.registerItem(269, "wooden_shovel", new ItemSpade(ToolMaterial.WOOD).setUnlocalizedName("shovelWood"));
        Item.registerItem(270, "wooden_pickaxe", new ItemPickaxe(ToolMaterial.WOOD).setUnlocalizedName("pickaxeWood"));
        Item.registerItem(271, "wooden_axe", new ItemAxe(ToolMaterial.WOOD).setUnlocalizedName("hatchetWood"));
        Item.registerItem(272, "stone_sword", new ItemSword(ToolMaterial.STONE).setUnlocalizedName("swordStone"));
        Item.registerItem(273, "stone_shovel", new ItemSpade(ToolMaterial.STONE).setUnlocalizedName("shovelStone"));
        Item.registerItem(274, "stone_pickaxe", new ItemPickaxe(ToolMaterial.STONE).setUnlocalizedName("pickaxeStone"));
        Item.registerItem(275, "stone_axe", new ItemAxe(ToolMaterial.STONE).setUnlocalizedName("hatchetStone"));
        Item.registerItem(276, "diamond_sword", new ItemSword(ToolMaterial.EMERALD).setUnlocalizedName("swordDiamond"));
        Item.registerItem(277, "diamond_shovel", new ItemSpade(ToolMaterial.EMERALD).setUnlocalizedName("shovelDiamond"));
        Item.registerItem(278, "diamond_pickaxe", new ItemPickaxe(ToolMaterial.EMERALD).setUnlocalizedName("pickaxeDiamond"));
        Item.registerItem(279, "diamond_axe", new ItemAxe(ToolMaterial.EMERALD).setUnlocalizedName("hatchetDiamond"));
        Item.registerItem(280, "stick", new Item().setFull3D().setUnlocalizedName("stick").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(281, "bowl", new Item().setUnlocalizedName("bowl").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(282, "mushroom_stew", new ItemSoup(6).setUnlocalizedName("mushroomStew"));
        Item.registerItem(283, "golden_sword", new ItemSword(ToolMaterial.GOLD).setUnlocalizedName("swordGold"));
        Item.registerItem(284, "golden_shovel", new ItemSpade(ToolMaterial.GOLD).setUnlocalizedName("shovelGold"));
        Item.registerItem(285, "golden_pickaxe", new ItemPickaxe(ToolMaterial.GOLD).setUnlocalizedName("pickaxeGold"));
        Item.registerItem(286, "golden_axe", new ItemAxe(ToolMaterial.GOLD).setUnlocalizedName("hatchetGold"));
        Item.registerItem(287, "string", new ItemReed(Blocks.tripwire).setUnlocalizedName("string").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(288, "feather", new Item().setUnlocalizedName("feather").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(289, "gunpowder", new Item().setUnlocalizedName("sulphur").setPotionEffect("+14&13-13").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(290, "wooden_hoe", new ItemHoe(ToolMaterial.WOOD).setUnlocalizedName("hoeWood"));
        Item.registerItem(291, "stone_hoe", new ItemHoe(ToolMaterial.STONE).setUnlocalizedName("hoeStone"));
        Item.registerItem(292, "iron_hoe", new ItemHoe(ToolMaterial.IRON).setUnlocalizedName("hoeIron"));
        Item.registerItem(293, "diamond_hoe", new ItemHoe(ToolMaterial.EMERALD).setUnlocalizedName("hoeDiamond"));
        Item.registerItem(294, "golden_hoe", new ItemHoe(ToolMaterial.GOLD).setUnlocalizedName("hoeGold"));
        Item.registerItem(295, "wheat_seeds", new ItemSeeds(Blocks.wheat, Blocks.farmland).setUnlocalizedName("seeds"));
        Item.registerItem(296, "wheat", new Item().setUnlocalizedName("wheat").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(297, "bread", new ItemFood(5, 0.6f, false).setUnlocalizedName("bread"));
        Item.registerItem(298, "leather_helmet", new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 0).setUnlocalizedName("helmetCloth"));
        Item.registerItem(299, "leather_chestplate", new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 1).setUnlocalizedName("chestplateCloth"));
        Item.registerItem(300, "leather_leggings", new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 2).setUnlocalizedName("leggingsCloth"));
        Item.registerItem(301, "leather_boots", new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 3).setUnlocalizedName("bootsCloth"));
        Item.registerItem(302, "chainmail_helmet", new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 0).setUnlocalizedName("helmetChain"));
        Item.registerItem(303, "chainmail_chestplate", new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 1).setUnlocalizedName("chestplateChain"));
        Item.registerItem(304, "chainmail_leggings", new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 2).setUnlocalizedName("leggingsChain"));
        Item.registerItem(305, "chainmail_boots", new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 3).setUnlocalizedName("bootsChain"));
        Item.registerItem(306, "iron_helmet", new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 0).setUnlocalizedName("helmetIron"));
        Item.registerItem(307, "iron_chestplate", new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 1).setUnlocalizedName("chestplateIron"));
        Item.registerItem(308, "iron_leggings", new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 2).setUnlocalizedName("leggingsIron"));
        Item.registerItem(309, "iron_boots", new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 3).setUnlocalizedName("bootsIron"));
        Item.registerItem(310, "diamond_helmet", new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 0).setUnlocalizedName("helmetDiamond"));
        Item.registerItem(311, "diamond_chestplate", new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 1).setUnlocalizedName("chestplateDiamond"));
        Item.registerItem(312, "diamond_leggings", new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 2).setUnlocalizedName("leggingsDiamond"));
        Item.registerItem(313, "diamond_boots", new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 3).setUnlocalizedName("bootsDiamond"));
        Item.registerItem(314, "golden_helmet", new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 0).setUnlocalizedName("helmetGold"));
        Item.registerItem(315, "golden_chestplate", new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 1).setUnlocalizedName("chestplateGold"));
        Item.registerItem(316, "golden_leggings", new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 2).setUnlocalizedName("leggingsGold"));
        Item.registerItem(317, "golden_boots", new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 3).setUnlocalizedName("bootsGold"));
        Item.registerItem(318, "flint", new Item().setUnlocalizedName("flint").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(319, "porkchop", new ItemFood(3, 0.3f, true).setUnlocalizedName("porkchopRaw"));
        Item.registerItem(320, "cooked_porkchop", new ItemFood(8, 0.8f, true).setUnlocalizedName("porkchopCooked"));
        Item.registerItem(321, "painting", new ItemHangingEntity(EntityPainting.class).setUnlocalizedName("painting"));
        Item.registerItem(322, "golden_apple", new ItemAppleGold(4, 1.2f, false).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 5, 1, 1.0f).setUnlocalizedName("appleGold"));
        Item.registerItem(323, "sign", new ItemSign().setUnlocalizedName("sign"));
        Item.registerItem(324, "wooden_door", new ItemDoor(Blocks.oak_door).setUnlocalizedName("doorOak"));
        Item item = new ItemBucket(Blocks.air).setUnlocalizedName("bucket").setMaxStackSize(16);
        Item.registerItem(325, "bucket", item);
        Item.registerItem(326, "water_bucket", new ItemBucket(Blocks.flowing_water).setUnlocalizedName("bucketWater").setContainerItem(item));
        Item.registerItem(327, "lava_bucket", new ItemBucket(Blocks.flowing_lava).setUnlocalizedName("bucketLava").setContainerItem(item));
        Item.registerItem(328, "minecart", new ItemMinecart(EntityMinecart.EnumMinecartType.RIDEABLE).setUnlocalizedName("minecart"));
        Item.registerItem(329, "saddle", new ItemSaddle().setUnlocalizedName("saddle"));
        Item.registerItem(330, "iron_door", new ItemDoor(Blocks.iron_door).setUnlocalizedName("doorIron"));
        Item.registerItem(331, "redstone", new ItemRedstone().setUnlocalizedName("redstone").setPotionEffect("-5+6-7"));
        Item.registerItem(332, "snowball", new ItemSnowball().setUnlocalizedName("snowball"));
        Item.registerItem(333, "boat", new ItemBoat().setUnlocalizedName("boat"));
        Item.registerItem(334, "leather", new Item().setUnlocalizedName("leather").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(335, "milk_bucket", new ItemBucketMilk().setUnlocalizedName("milk").setContainerItem(item));
        Item.registerItem(336, "brick", new Item().setUnlocalizedName("brick").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(337, "clay_ball", new Item().setUnlocalizedName("clay").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(338, "reeds", new ItemReed(Blocks.reeds).setUnlocalizedName("reeds").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(339, "paper", new Item().setUnlocalizedName("paper").setCreativeTab(CreativeTabs.tabMisc));
        Item.registerItem(340, "book", new ItemBook().setUnlocalizedName("book").setCreativeTab(CreativeTabs.tabMisc));
        Item.registerItem(341, "slime_ball", new Item().setUnlocalizedName("slimeball").setCreativeTab(CreativeTabs.tabMisc));
        Item.registerItem(342, "chest_minecart", new ItemMinecart(EntityMinecart.EnumMinecartType.CHEST).setUnlocalizedName("minecartChest"));
        Item.registerItem(343, "furnace_minecart", new ItemMinecart(EntityMinecart.EnumMinecartType.FURNACE).setUnlocalizedName("minecartFurnace"));
        Item.registerItem(344, "egg", new ItemEgg().setUnlocalizedName("egg"));
        Item.registerItem(345, "compass", new Item().setUnlocalizedName("compass").setCreativeTab(CreativeTabs.tabTools));
        Item.registerItem(346, "fishing_rod", new ItemFishingRod().setUnlocalizedName("fishingRod"));
        Item.registerItem(347, "clock", new Item().setUnlocalizedName("clock").setCreativeTab(CreativeTabs.tabTools));
        Item.registerItem(348, "glowstone_dust", new Item().setUnlocalizedName("yellowDust").setPotionEffect("+5-6-7").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(349, "fish", new ItemFishFood(false).setUnlocalizedName("fish").setHasSubtypes(true));
        Item.registerItem(350, "cooked_fish", new ItemFishFood(true).setUnlocalizedName("fish").setHasSubtypes(true));
        Item.registerItem(351, "dye", new ItemDye().setUnlocalizedName("dyePowder"));
        Item.registerItem(352, "bone", new Item().setUnlocalizedName("bone").setFull3D().setCreativeTab(CreativeTabs.tabMisc));
        Item.registerItem(353, "sugar", new Item().setUnlocalizedName("sugar").setPotionEffect("-0+1-2-3&4-4+13").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(354, "cake", new ItemReed(Blocks.cake).setMaxStackSize(1).setUnlocalizedName("cake").setCreativeTab(CreativeTabs.tabFood));
        Item.registerItem(355, "bed", new ItemBed().setMaxStackSize(1).setUnlocalizedName("bed"));
        Item.registerItem(356, "repeater", new ItemReed(Blocks.unpowered_repeater).setUnlocalizedName("diode").setCreativeTab(CreativeTabs.tabRedstone));
        Item.registerItem(357, "cookie", new ItemFood(2, 0.1f, false).setUnlocalizedName("cookie"));
        Item.registerItem(358, "filled_map", new ItemMap().setUnlocalizedName("map"));
        Item.registerItem(359, "shears", new ItemShears().setUnlocalizedName("shears"));
        Item.registerItem(360, "melon", new ItemFood(2, 0.3f, false).setUnlocalizedName("melon"));
        Item.registerItem(361, "pumpkin_seeds", new ItemSeeds(Blocks.pumpkin_stem, Blocks.farmland).setUnlocalizedName("seeds_pumpkin"));
        Item.registerItem(362, "melon_seeds", new ItemSeeds(Blocks.melon_stem, Blocks.farmland).setUnlocalizedName("seeds_melon"));
        Item.registerItem(363, "beef", new ItemFood(3, 0.3f, true).setUnlocalizedName("beefRaw"));
        Item.registerItem(364, "cooked_beef", new ItemFood(8, 0.8f, true).setUnlocalizedName("beefCooked"));
        Item.registerItem(365, "chicken", new ItemFood(2, 0.3f, true).setPotionEffect(Potion.hunger.id, 30, 0, 0.3f).setUnlocalizedName("chickenRaw"));
        Item.registerItem(366, "cooked_chicken", new ItemFood(6, 0.6f, true).setUnlocalizedName("chickenCooked"));
        Item.registerItem(367, "rotten_flesh", new ItemFood(4, 0.1f, true).setPotionEffect(Potion.hunger.id, 30, 0, 0.8f).setUnlocalizedName("rottenFlesh"));
        Item.registerItem(368, "ender_pearl", new ItemEnderPearl().setUnlocalizedName("enderPearl"));
        Item.registerItem(369, "blaze_rod", new Item().setUnlocalizedName("blazeRod").setCreativeTab(CreativeTabs.tabMaterials).setFull3D());
        Item.registerItem(370, "ghast_tear", new Item().setUnlocalizedName("ghastTear").setPotionEffect("+0-1-2-3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing));
        Item.registerItem(371, "gold_nugget", new Item().setUnlocalizedName("goldNugget").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(372, "nether_wart", new ItemSeeds(Blocks.nether_wart, Blocks.soul_sand).setUnlocalizedName("netherStalkSeeds").setPotionEffect("+4"));
        Item.registerItem(373, "potion", new ItemPotion().setUnlocalizedName("potion"));
        Item.registerItem(374, "glass_bottle", new ItemGlassBottle().setUnlocalizedName("glassBottle"));
        Item.registerItem(375, "spider_eye", new ItemFood(2, 0.8f, false).setPotionEffect(Potion.poison.id, 5, 0, 1.0f).setUnlocalizedName("spiderEye").setPotionEffect("-0-1+2-3&4-4+13"));
        Item.registerItem(376, "fermented_spider_eye", new Item().setUnlocalizedName("fermentedSpiderEye").setPotionEffect("-0+3-4+13").setCreativeTab(CreativeTabs.tabBrewing));
        Item.registerItem(377, "blaze_powder", new Item().setUnlocalizedName("blazePowder").setPotionEffect("+0-1-2+3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing));
        Item.registerItem(378, "magma_cream", new Item().setUnlocalizedName("magmaCream").setPotionEffect("+0+1-2-3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing));
        Item.registerItem(379, "brewing_stand", new ItemReed(Blocks.brewing_stand).setUnlocalizedName("brewingStand").setCreativeTab(CreativeTabs.tabBrewing));
        Item.registerItem(380, "cauldron", new ItemReed(Blocks.cauldron).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.tabBrewing));
        Item.registerItem(381, "ender_eye", new ItemEnderEye().setUnlocalizedName("eyeOfEnder"));
        Item.registerItem(382, "speckled_melon", new Item().setUnlocalizedName("speckledMelon").setPotionEffect("+0-1+2-3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing));
        Item.registerItem(383, "spawn_egg", new ItemMonsterPlacer().setUnlocalizedName("monsterPlacer"));
        Item.registerItem(384, "experience_bottle", new ItemExpBottle().setUnlocalizedName("expBottle"));
        Item.registerItem(385, "fire_charge", new ItemFireball().setUnlocalizedName("fireball"));
        Item.registerItem(386, "writable_book", new ItemWritableBook().setUnlocalizedName("writingBook").setCreativeTab(CreativeTabs.tabMisc));
        Item.registerItem(387, "written_book", new ItemEditableBook().setUnlocalizedName("writtenBook").setMaxStackSize(16));
        Item.registerItem(388, "emerald", new Item().setUnlocalizedName("emerald").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(389, "item_frame", new ItemHangingEntity(EntityItemFrame.class).setUnlocalizedName("frame"));
        Item.registerItem(390, "flower_pot", new ItemReed(Blocks.flower_pot).setUnlocalizedName("flowerPot").setCreativeTab(CreativeTabs.tabDecorations));
        Item.registerItem(391, "carrot", new ItemSeedFood(3, 0.6f, Blocks.carrots, Blocks.farmland).setUnlocalizedName("carrots"));
        Item.registerItem(392, "potato", new ItemSeedFood(1, 0.3f, Blocks.potatoes, Blocks.farmland).setUnlocalizedName("potato"));
        Item.registerItem(393, "baked_potato", new ItemFood(5, 0.6f, false).setUnlocalizedName("potatoBaked"));
        Item.registerItem(394, "poisonous_potato", new ItemFood(2, 0.3f, false).setPotionEffect(Potion.poison.id, 5, 0, 0.6f).setUnlocalizedName("potatoPoisonous"));
        Item.registerItem(395, "map", new ItemEmptyMap().setUnlocalizedName("emptyMap"));
        Item.registerItem(396, "golden_carrot", new ItemFood(6, 1.2f, false).setUnlocalizedName("carrotGolden").setPotionEffect("-0+1+2-3+13&4-4").setCreativeTab(CreativeTabs.tabBrewing));
        Item.registerItem(397, "skull", new ItemSkull().setUnlocalizedName("skull"));
        Item.registerItem(398, "carrot_on_a_stick", new ItemCarrotOnAStick().setUnlocalizedName("carrotOnAStick"));
        Item.registerItem(399, "nether_star", new ItemSimpleFoiled().setUnlocalizedName("netherStar").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(400, "pumpkin_pie", new ItemFood(8, 0.3f, false).setUnlocalizedName("pumpkinPie").setCreativeTab(CreativeTabs.tabFood));
        Item.registerItem(401, "fireworks", new ItemFirework().setUnlocalizedName("fireworks"));
        Item.registerItem(402, "firework_charge", new ItemFireworkCharge().setUnlocalizedName("fireworksCharge").setCreativeTab(CreativeTabs.tabMisc));
        Item.registerItem(403, "enchanted_book", new ItemEnchantedBook().setMaxStackSize(1).setUnlocalizedName("enchantedBook"));
        Item.registerItem(404, "comparator", new ItemReed(Blocks.unpowered_comparator).setUnlocalizedName("comparator").setCreativeTab(CreativeTabs.tabRedstone));
        Item.registerItem(405, "netherbrick", new Item().setUnlocalizedName("netherbrick").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(406, "quartz", new Item().setUnlocalizedName("netherquartz").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(407, "tnt_minecart", new ItemMinecart(EntityMinecart.EnumMinecartType.TNT).setUnlocalizedName("minecartTnt"));
        Item.registerItem(408, "hopper_minecart", new ItemMinecart(EntityMinecart.EnumMinecartType.HOPPER).setUnlocalizedName("minecartHopper"));
        Item.registerItem(409, "prismarine_shard", new Item().setUnlocalizedName("prismarineShard").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(410, "prismarine_crystals", new Item().setUnlocalizedName("prismarineCrystals").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(411, "rabbit", new ItemFood(3, 0.3f, true).setUnlocalizedName("rabbitRaw"));
        Item.registerItem(412, "cooked_rabbit", new ItemFood(5, 0.6f, true).setUnlocalizedName("rabbitCooked"));
        Item.registerItem(413, "rabbit_stew", new ItemSoup(10).setUnlocalizedName("rabbitStew"));
        Item.registerItem(414, "rabbit_foot", new Item().setUnlocalizedName("rabbitFoot").setPotionEffect("+0+1-2+3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing));
        Item.registerItem(415, "rabbit_hide", new Item().setUnlocalizedName("rabbitHide").setCreativeTab(CreativeTabs.tabMaterials));
        Item.registerItem(416, "armor_stand", new ItemArmorStand().setUnlocalizedName("armorStand").setMaxStackSize(16));
        Item.registerItem(417, "iron_horse_armor", new Item().setUnlocalizedName("horsearmormetal").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc));
        Item.registerItem(418, "golden_horse_armor", new Item().setUnlocalizedName("horsearmorgold").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc));
        Item.registerItem(419, "diamond_horse_armor", new Item().setUnlocalizedName("horsearmordiamond").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc));
        Item.registerItem(420, "lead", new ItemLead().setUnlocalizedName("leash"));
        Item.registerItem(421, "name_tag", new ItemNameTag().setUnlocalizedName("nameTag"));
        Item.registerItem(422, "command_block_minecart", new ItemMinecart(EntityMinecart.EnumMinecartType.COMMAND_BLOCK).setUnlocalizedName("minecartCommandBlock").setCreativeTab(null));
        Item.registerItem(423, "mutton", new ItemFood(2, 0.3f, true).setUnlocalizedName("muttonRaw"));
        Item.registerItem(424, "cooked_mutton", new ItemFood(6, 0.8f, true).setUnlocalizedName("muttonCooked"));
        Item.registerItem(425, "banner", (Item)new ItemBanner().setUnlocalizedName("banner"));
        Item.registerItem(427, "spruce_door", new ItemDoor(Blocks.spruce_door).setUnlocalizedName("doorSpruce"));
        Item.registerItem(428, "birch_door", new ItemDoor(Blocks.birch_door).setUnlocalizedName("doorBirch"));
        Item.registerItem(429, "jungle_door", new ItemDoor(Blocks.jungle_door).setUnlocalizedName("doorJungle"));
        Item.registerItem(430, "acacia_door", new ItemDoor(Blocks.acacia_door).setUnlocalizedName("doorAcacia"));
        Item.registerItem(431, "dark_oak_door", new ItemDoor(Blocks.dark_oak_door).setUnlocalizedName("doorDarkOak"));
        Item.registerItem(2256, "record_13", new ItemRecord("13").setUnlocalizedName("record"));
        Item.registerItem(2257, "record_cat", new ItemRecord("cat").setUnlocalizedName("record"));
        Item.registerItem(2258, "record_blocks", new ItemRecord("blocks").setUnlocalizedName("record"));
        Item.registerItem(2259, "record_chirp", new ItemRecord("chirp").setUnlocalizedName("record"));
        Item.registerItem(2260, "record_far", new ItemRecord("far").setUnlocalizedName("record"));
        Item.registerItem(2261, "record_mall", new ItemRecord("mall").setUnlocalizedName("record"));
        Item.registerItem(2262, "record_mellohi", new ItemRecord("mellohi").setUnlocalizedName("record"));
        Item.registerItem(2263, "record_stal", new ItemRecord("stal").setUnlocalizedName("record"));
        Item.registerItem(2264, "record_strad", new ItemRecord("strad").setUnlocalizedName("record"));
        Item.registerItem(2265, "record_ward", new ItemRecord("ward").setUnlocalizedName("record"));
        Item.registerItem(2266, "record_11", new ItemRecord("11").setUnlocalizedName("record"));
        Item.registerItem(2267, "record_wait", new ItemRecord("wait").setUnlocalizedName("record"));
    }

    private static void registerItemBlock(Block blockIn) {
        Item.registerItemBlock(blockIn, new ItemBlock(blockIn));
    }

    protected static void registerItemBlock(Block blockIn, Item itemIn) {
        Item.registerItem(Block.getIdFromBlock(blockIn), (ResourceLocation)Block.blockRegistry.getNameForObject(blockIn), itemIn);
        BLOCK_TO_ITEM.put(blockIn, itemIn);
    }

    private static void registerItem(int id, String textualID, Item itemIn) {
        Item.registerItem(id, new ResourceLocation(textualID), itemIn);
    }

    private static void registerItem(int id, ResourceLocation textualID, Item itemIn) {
        itemRegistry.register(id, textualID, itemIn);
    }

    public static enum ToolMaterial {
        WOOD(0, 59, 2.0f, 0.0f, 15),
        STONE(1, 131, 4.0f, 1.0f, 5),
        IRON(2, 250, 6.0f, 2.0f, 14),
        EMERALD(3, 1561, 8.0f, 3.0f, 10),
        GOLD(0, 32, 12.0f, 0.0f, 22);

        private final int harvestLevel;
        private final int maxUses;
        private final float efficiencyOnProperMaterial;
        private final float damageVsEntity;
        private final int enchantability;

        private ToolMaterial(int harvestLevel, int maxUses, float efficiency, float damageVsEntity, int enchantability) {
            this.harvestLevel = harvestLevel;
            this.maxUses = maxUses;
            this.efficiencyOnProperMaterial = efficiency;
            this.damageVsEntity = damageVsEntity;
            this.enchantability = enchantability;
        }

        public int getMaxUses() {
            return this.maxUses;
        }

        public float getEfficiencyOnProperMaterial() {
            return this.efficiencyOnProperMaterial;
        }

        public float getDamageVsEntity() {
            return this.damageVsEntity;
        }

        public int getHarvestLevel() {
            return this.harvestLevel;
        }

        public int getEnchantability() {
            return this.enchantability;
        }

        public Item getRepairItem() {
            return this == WOOD ? Item.getItemFromBlock(Blocks.planks) : (this == STONE ? Item.getItemFromBlock(Blocks.cobblestone) : (this == GOLD ? Items.gold_ingot : (this == IRON ? Items.iron_ingot : (this == EMERALD ? Items.diamond : null))));
        }
    }
}

