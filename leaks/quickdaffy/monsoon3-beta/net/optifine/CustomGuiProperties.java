/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.CustomGuis;
import net.optifine.config.ConnectedParser;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeListInt;
import net.optifine.config.VillagerProfession;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorField;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;

public class CustomGuiProperties {
    private String fileName = null;
    private String basePath = null;
    private EnumContainer container = null;
    private Map<ResourceLocation, ResourceLocation> textureLocations = null;
    private NbtTagValue nbtName = null;
    private BiomeGenBase[] biomes = null;
    private RangeListInt heights = null;
    private Boolean large = null;
    private Boolean trapped = null;
    private Boolean christmas = null;
    private Boolean ender = null;
    private RangeListInt levels = null;
    private VillagerProfession[] professions = null;
    private EnumVariant[] variants = null;
    private EnumDyeColor[] colors = null;
    private static final EnumVariant[] VARIANTS_HORSE = new EnumVariant[]{EnumVariant.HORSE, EnumVariant.DONKEY, EnumVariant.MULE, EnumVariant.LLAMA};
    private static final EnumVariant[] VARIANTS_DISPENSER = new EnumVariant[]{EnumVariant.DISPENSER, EnumVariant.DROPPER};
    private static final EnumVariant[] VARIANTS_INVALID = new EnumVariant[0];
    private static final EnumDyeColor[] COLORS_INVALID = new EnumDyeColor[0];
    private static final ResourceLocation ANVIL_GUI_TEXTURE = new ResourceLocation("textures/gui/container/anvil.png");
    private static final ResourceLocation BEACON_GUI_TEXTURE = new ResourceLocation("textures/gui/container/beacon.png");
    private static final ResourceLocation BREWING_STAND_GUI_TEXTURE = new ResourceLocation("textures/gui/container/brewing_stand.png");
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/crafting_table.png");
    private static final ResourceLocation HORSE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/horse.png");
    private static final ResourceLocation DISPENSER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/dispenser.png");
    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation FURNACE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");
    private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/hopper.png");
    private static final ResourceLocation INVENTORY_GUI_TEXTURE = new ResourceLocation("textures/gui/container/inventory.png");
    private static final ResourceLocation SHULKER_BOX_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ResourceLocation VILLAGER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");

    public CustomGuiProperties(Properties props, String path) {
        ConnectedParser connectedparser = new ConnectedParser("CustomGuis");
        this.fileName = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.container = (EnumContainer)connectedparser.parseEnum(props.getProperty("container"), EnumContainer.values(), "container");
        this.textureLocations = CustomGuiProperties.parseTextureLocations(props, "texture", this.container, "textures/gui/", this.basePath);
        this.nbtName = connectedparser.parseNbtTagValue("name", props.getProperty("name"));
        this.biomes = connectedparser.parseBiomes(props.getProperty("biomes"));
        this.heights = connectedparser.parseRangeListInt(props.getProperty("heights"));
        this.large = connectedparser.parseBooleanObject(props.getProperty("large"));
        this.trapped = connectedparser.parseBooleanObject(props.getProperty("trapped"));
        this.christmas = connectedparser.parseBooleanObject(props.getProperty("christmas"));
        this.ender = connectedparser.parseBooleanObject(props.getProperty("ender"));
        this.levels = connectedparser.parseRangeListInt(props.getProperty("levels"));
        this.professions = connectedparser.parseProfessions(props.getProperty("professions"));
        Enum[] acustomguiproperties$enumvariant = CustomGuiProperties.getContainerVariants(this.container);
        this.variants = (EnumVariant[])connectedparser.parseEnums(props.getProperty("variants"), acustomguiproperties$enumvariant, "variants", VARIANTS_INVALID);
        this.colors = CustomGuiProperties.parseEnumDyeColors(props.getProperty("colors"));
    }

    private static EnumVariant[] getContainerVariants(EnumContainer cont) {
        return cont == EnumContainer.HORSE ? VARIANTS_HORSE : (cont == EnumContainer.DISPENSER ? VARIANTS_DISPENSER : new EnumVariant[]{});
    }

    private static EnumDyeColor[] parseEnumDyeColors(String str) {
        if (str == null) {
            return null;
        }
        str = str.toLowerCase();
        String[] astring = Config.tokenize(str, " ");
        EnumDyeColor[] aenumdyecolor = new EnumDyeColor[astring.length];
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            EnumDyeColor enumdyecolor = CustomGuiProperties.parseEnumDyeColor(s);
            if (enumdyecolor == null) {
                CustomGuiProperties.warn("Invalid color: " + s);
                return COLORS_INVALID;
            }
            aenumdyecolor[i] = enumdyecolor;
        }
        return aenumdyecolor;
    }

    private static EnumDyeColor parseEnumDyeColor(String str) {
        if (str == null) {
            return null;
        }
        EnumDyeColor[] aenumdyecolor = EnumDyeColor.values();
        for (int i = 0; i < aenumdyecolor.length; ++i) {
            EnumDyeColor enumdyecolor = aenumdyecolor[i];
            if (enumdyecolor.getName().equals(str)) {
                return enumdyecolor;
            }
            if (!enumdyecolor.getUnlocalizedName().equals(str)) continue;
            return enumdyecolor;
        }
        return null;
    }

    private static ResourceLocation parseTextureLocation(String str, String basePath) {
        if (str == null) {
            return null;
        }
        String s = TextureUtils.fixResourcePath(str = str.trim(), basePath);
        if (!s.endsWith(".png")) {
            s = s + ".png";
        }
        return new ResourceLocation(basePath + "/" + s);
    }

    private static Map<ResourceLocation, ResourceLocation> parseTextureLocations(Properties props, String property, EnumContainer container, String pathPrefix, String basePath) {
        HashMap<ResourceLocation, ResourceLocation> map = new HashMap<ResourceLocation, ResourceLocation>();
        String s = props.getProperty(property);
        if (s != null) {
            ResourceLocation resourcelocation = CustomGuiProperties.getGuiTextureLocation(container);
            ResourceLocation resourcelocation1 = CustomGuiProperties.parseTextureLocation(s, basePath);
            if (resourcelocation != null && resourcelocation1 != null) {
                map.put(resourcelocation, resourcelocation1);
            }
        }
        String s5 = property + ".";
        for (Object e : props.keySet()) {
            String s1 = (String)e;
            if (!s1.startsWith(s5)) continue;
            String s2 = s1.substring(s5.length());
            s2 = s2.replace('\\', '/');
            s2 = StrUtils.removePrefixSuffix(s2, "/", ".png");
            String s3 = pathPrefix + s2 + ".png";
            String s4 = props.getProperty(s1);
            ResourceLocation resourcelocation2 = new ResourceLocation(s3);
            ResourceLocation resourcelocation3 = CustomGuiProperties.parseTextureLocation(s4, basePath);
            map.put(resourcelocation2, resourcelocation3);
        }
        return map;
    }

    private static ResourceLocation getGuiTextureLocation(EnumContainer container) {
        switch (container) {
            case ANVIL: {
                return ANVIL_GUI_TEXTURE;
            }
            case BEACON: {
                return BEACON_GUI_TEXTURE;
            }
            case BREWING_STAND: {
                return BREWING_STAND_GUI_TEXTURE;
            }
            case CHEST: {
                return CHEST_GUI_TEXTURE;
            }
            case CRAFTING: {
                return CRAFTING_TABLE_GUI_TEXTURE;
            }
            case CREATIVE: {
                return null;
            }
            case DISPENSER: {
                return DISPENSER_GUI_TEXTURE;
            }
            case ENCHANTMENT: {
                return ENCHANTMENT_TABLE_GUI_TEXTURE;
            }
            case FURNACE: {
                return FURNACE_GUI_TEXTURE;
            }
            case HOPPER: {
                return HOPPER_GUI_TEXTURE;
            }
            case HORSE: {
                return HORSE_GUI_TEXTURE;
            }
            case INVENTORY: {
                return INVENTORY_GUI_TEXTURE;
            }
            case SHULKER_BOX: {
                return SHULKER_BOX_GUI_TEXTURE;
            }
            case VILLAGER: {
                return VILLAGER_GUI_TEXTURE;
            }
        }
        return null;
    }

    public boolean isValid(String path) {
        if (this.fileName != null && this.fileName.length() > 0) {
            if (this.basePath == null) {
                CustomGuiProperties.warn("No base path found: " + path);
                return false;
            }
            if (this.container == null) {
                CustomGuiProperties.warn("No container found: " + path);
                return false;
            }
            if (this.textureLocations.isEmpty()) {
                CustomGuiProperties.warn("No texture found: " + path);
                return false;
            }
            if (this.professions == ConnectedParser.PROFESSIONS_INVALID) {
                CustomGuiProperties.warn("Invalid professions or careers: " + path);
                return false;
            }
            if (this.variants == VARIANTS_INVALID) {
                CustomGuiProperties.warn("Invalid variants: " + path);
                return false;
            }
            if (this.colors == COLORS_INVALID) {
                CustomGuiProperties.warn("Invalid colors: " + path);
                return false;
            }
            return true;
        }
        CustomGuiProperties.warn("No name found: " + path);
        return false;
    }

    private static void warn(String str) {
        Config.warn("[CustomGuis] " + str);
    }

    private boolean matchesGeneral(EnumContainer ec, BlockPos pos, IBlockAccess blockAccess) {
        BiomeGenBase biomegenbase;
        if (this.container != ec) {
            return false;
        }
        if (this.biomes != null && !Matches.biome(biomegenbase = blockAccess.getBiomeGenForCoords(pos), this.biomes)) {
            return false;
        }
        return this.heights == null || this.heights.isInRange(pos.getY());
    }

    public boolean matchesPos(EnumContainer ec, BlockPos pos, IBlockAccess blockAccess, GuiScreen screen) {
        String s;
        if (!this.matchesGeneral(ec, pos, blockAccess)) {
            return false;
        }
        if (this.nbtName != null && !this.nbtName.matchesValue(s = CustomGuiProperties.getName(screen))) {
            return false;
        }
        switch (ec) {
            case BEACON: {
                return this.matchesBeacon(pos, blockAccess);
            }
            case CHEST: {
                return this.matchesChest(pos, blockAccess);
            }
            case DISPENSER: {
                return this.matchesDispenser(pos, blockAccess);
            }
        }
        return true;
    }

    public static String getName(GuiScreen screen) {
        IWorldNameable iworldnameable = CustomGuiProperties.getWorldNameable(screen);
        return iworldnameable == null ? null : iworldnameable.getDisplayName().getUnformattedText();
    }

    private static IWorldNameable getWorldNameable(GuiScreen screen) {
        return screen instanceof GuiBeacon ? CustomGuiProperties.getWorldNameable(screen, Reflector.GuiBeacon_tileBeacon) : (screen instanceof GuiBrewingStand ? CustomGuiProperties.getWorldNameable(screen, Reflector.GuiBrewingStand_tileBrewingStand) : (screen instanceof GuiChest ? CustomGuiProperties.getWorldNameable(screen, Reflector.GuiChest_lowerChestInventory) : (screen instanceof GuiDispenser ? ((GuiDispenser)screen).dispenserInventory : (screen instanceof GuiEnchantment ? CustomGuiProperties.getWorldNameable(screen, Reflector.GuiEnchantment_nameable) : (screen instanceof GuiFurnace ? CustomGuiProperties.getWorldNameable(screen, Reflector.GuiFurnace_tileFurnace) : (screen instanceof GuiHopper ? CustomGuiProperties.getWorldNameable(screen, Reflector.GuiHopper_hopperInventory) : null))))));
    }

    private static IWorldNameable getWorldNameable(GuiScreen screen, ReflectorField fieldInventory) {
        Object object = Reflector.getFieldValue(screen, fieldInventory);
        return !(object instanceof IWorldNameable) ? null : (IWorldNameable)object;
    }

    private boolean matchesBeacon(BlockPos pos, IBlockAccess blockAccess) {
        TileEntity tileentity = blockAccess.getTileEntity(pos);
        if (!(tileentity instanceof TileEntityBeacon)) {
            return false;
        }
        TileEntityBeacon tileentitybeacon = (TileEntityBeacon)tileentity;
        if (this.levels != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            tileentitybeacon.writeToNBT(nbttagcompound);
            int i = nbttagcompound.getInteger("Levels");
            if (!this.levels.isInRange(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesChest(BlockPos pos, IBlockAccess blockAccess) {
        TileEntity tileentity = blockAccess.getTileEntity(pos);
        if (tileentity instanceof TileEntityChest) {
            TileEntityChest tileentitychest = (TileEntityChest)tileentity;
            return this.matchesChest(tileentitychest, pos, blockAccess);
        }
        if (tileentity instanceof TileEntityEnderChest) {
            TileEntityEnderChest tileentityenderchest = (TileEntityEnderChest)tileentity;
            return this.matchesEnderChest(tileentityenderchest, pos, blockAccess);
        }
        return false;
    }

    private boolean matchesChest(TileEntityChest tec, BlockPos pos, IBlockAccess blockAccess) {
        boolean flag = tec.adjacentChestXNeg != null || tec.adjacentChestXPos != null || tec.adjacentChestZNeg != null || tec.adjacentChestZPos != null;
        boolean flag1 = tec.getChestType() == 1;
        boolean flag2 = CustomGuis.isChristmas;
        boolean flag3 = false;
        return this.matchesChest(flag, flag1, flag2, flag3);
    }

    private boolean matchesEnderChest(TileEntityEnderChest teec, BlockPos pos, IBlockAccess blockAccess) {
        return this.matchesChest(false, false, false, true);
    }

    private boolean matchesChest(boolean isLarge, boolean isTrapped, boolean isChristmas, boolean isEnder) {
        return this.large != null && this.large != isLarge ? false : (this.trapped != null && this.trapped != isTrapped ? false : (this.christmas != null && this.christmas != isChristmas ? false : this.ender == null || this.ender == isEnder));
    }

    private boolean matchesDispenser(BlockPos pos, IBlockAccess blockAccess) {
        EnumVariant customguiproperties$enumvariant;
        TileEntity tileentity = blockAccess.getTileEntity(pos);
        if (!(tileentity instanceof TileEntityDispenser)) {
            return false;
        }
        TileEntityDispenser tileentitydispenser = (TileEntityDispenser)tileentity;
        return this.variants == null || Config.equalsOne((Object)(customguiproperties$enumvariant = this.getDispenserVariant(tileentitydispenser)), (Object[])this.variants);
    }

    private EnumVariant getDispenserVariant(TileEntityDispenser ted) {
        return ted instanceof TileEntityDropper ? EnumVariant.DROPPER : EnumVariant.DISPENSER;
    }

    public boolean matchesEntity(EnumContainer ec, Entity entity, IBlockAccess blockAccess) {
        String s;
        if (!this.matchesGeneral(ec, entity.getPosition(), blockAccess)) {
            return false;
        }
        if (this.nbtName != null && !this.nbtName.matchesValue(s = entity.getCommandSenderName())) {
            return false;
        }
        switch (ec) {
            case HORSE: {
                return this.matchesHorse(entity, blockAccess);
            }
            case VILLAGER: {
                return this.matchesVillager(entity, blockAccess);
            }
        }
        return true;
    }

    private boolean matchesVillager(Entity entity, IBlockAccess blockAccess) {
        if (!(entity instanceof EntityVillager)) {
            return false;
        }
        EntityVillager entityvillager = (EntityVillager)entity;
        if (this.professions != null) {
            int i = entityvillager.getProfession();
            int j = Reflector.getFieldValueInt(entityvillager, Reflector.EntityVillager_careerId, -1);
            if (j < 0) {
                return false;
            }
            boolean flag = false;
            for (int k = 0; k < this.professions.length; ++k) {
                VillagerProfession villagerprofession = this.professions[k];
                if (!villagerprofession.matches(i, j)) continue;
                flag = true;
                break;
            }
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesHorse(Entity entity, IBlockAccess blockAccess) {
        EnumVariant customguiproperties$enumvariant;
        if (!(entity instanceof EntityHorse)) {
            return false;
        }
        EntityHorse entityhorse = (EntityHorse)entity;
        return this.variants == null || Config.equalsOne((Object)(customguiproperties$enumvariant = this.getHorseVariant(entityhorse)), (Object[])this.variants);
    }

    private EnumVariant getHorseVariant(EntityHorse entity) {
        int i = entity.getHorseType();
        switch (i) {
            case 0: {
                return EnumVariant.HORSE;
            }
            case 1: {
                return EnumVariant.DONKEY;
            }
            case 2: {
                return EnumVariant.MULE;
            }
        }
        return null;
    }

    public EnumContainer getContainer() {
        return this.container;
    }

    public ResourceLocation getTextureLocation(ResourceLocation loc) {
        ResourceLocation resourcelocation = this.textureLocations.get(loc);
        return resourcelocation == null ? loc : resourcelocation;
    }

    public String toString() {
        return "name: " + this.fileName + ", container: " + (Object)((Object)this.container) + ", textures: " + this.textureLocations;
    }

    private static enum EnumVariant {
        HORSE,
        DONKEY,
        MULE,
        LLAMA,
        DISPENSER,
        DROPPER;

    }

    public static enum EnumContainer {
        ANVIL,
        BEACON,
        BREWING_STAND,
        CHEST,
        CRAFTING,
        DISPENSER,
        ENCHANTMENT,
        FURNACE,
        HOPPER,
        HORSE,
        VILLAGER,
        SHULKER_BOX,
        CREATIVE,
        INVENTORY;

        public static final EnumContainer[] VALUES;

        static {
            VALUES = EnumContainer.values();
        }
    }
}

