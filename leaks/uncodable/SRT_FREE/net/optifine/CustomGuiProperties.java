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
   private final String fileName;
   private final String basePath;
   private final CustomGuiProperties.EnumContainer container;
   private final Map<ResourceLocation, ResourceLocation> textureLocations;
   private final NbtTagValue nbtName;
   private final BiomeGenBase[] biomes;
   private final RangeListInt heights;
   private final Boolean large;
   private final Boolean trapped;
   private final Boolean christmas;
   private final Boolean ender;
   private final RangeListInt levels;
   private final VillagerProfession[] professions;
   private final CustomGuiProperties.EnumVariant[] variants;
   private final EnumDyeColor[] colors;
   private static final CustomGuiProperties.EnumVariant[] VARIANTS_HORSE = new CustomGuiProperties.EnumVariant[]{
      CustomGuiProperties.EnumVariant.HORSE,
      CustomGuiProperties.EnumVariant.DONKEY,
      CustomGuiProperties.EnumVariant.MULE,
      CustomGuiProperties.EnumVariant.LLAMA
   };
   private static final CustomGuiProperties.EnumVariant[] VARIANTS_DISPENSER = new CustomGuiProperties.EnumVariant[]{
      CustomGuiProperties.EnumVariant.DISPENSER, CustomGuiProperties.EnumVariant.DROPPER
   };
   private static final CustomGuiProperties.EnumVariant[] VARIANTS_INVALID = new CustomGuiProperties.EnumVariant[0];
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
      this.container = (CustomGuiProperties.EnumContainer)connectedparser.parseEnum(
         props.getProperty("container"), CustomGuiProperties.EnumContainer.values(), "container"
      );
      this.textureLocations = parseTextureLocations(props, this.container, this.basePath);
      this.nbtName = connectedparser.parseNbtTagValue("name", props.getProperty("name"));
      this.biomes = connectedparser.parseBiomes(props.getProperty("biomes"));
      this.heights = connectedparser.parseRangeListInt(props.getProperty("heights"));
      this.large = connectedparser.parseBooleanObject(props.getProperty("large"));
      this.trapped = connectedparser.parseBooleanObject(props.getProperty("trapped"));
      this.christmas = connectedparser.parseBooleanObject(props.getProperty("christmas"));
      this.ender = connectedparser.parseBooleanObject(props.getProperty("ender"));
      this.levels = connectedparser.parseRangeListInt(props.getProperty("levels"));
      this.professions = connectedparser.parseProfessions(props.getProperty("professions"));
      CustomGuiProperties.EnumVariant[] acustomguiproperties$enumvariant = getContainerVariants(this.container);
      this.variants = (CustomGuiProperties.EnumVariant[])connectedparser.parseEnums(
         props.getProperty("variants"), acustomguiproperties$enumvariant, "variants", VARIANTS_INVALID
      );
      this.colors = parseEnumDyeColors(props.getProperty("colors"));
   }

   private static CustomGuiProperties.EnumVariant[] getContainerVariants(CustomGuiProperties.EnumContainer cont) {
      return cont == CustomGuiProperties.EnumContainer.HORSE
         ? VARIANTS_HORSE
         : (cont == CustomGuiProperties.EnumContainer.DISPENSER ? VARIANTS_DISPENSER : new CustomGuiProperties.EnumVariant[0]);
   }

   private static EnumDyeColor[] parseEnumDyeColors(String str) {
      if (str == null) {
         return null;
      } else {
         str = str.toLowerCase();
         String[] astring = Config.tokenize(str, " ");
         EnumDyeColor[] aenumdyecolor = new EnumDyeColor[astring.length];

         for(int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            EnumDyeColor enumdyecolor = parseEnumDyeColor(s);
            if (enumdyecolor == null) {
               warn("Invalid color: " + s);
               return COLORS_INVALID;
            }

            aenumdyecolor[i] = enumdyecolor;
         }

         return aenumdyecolor;
      }
   }

   private static EnumDyeColor parseEnumDyeColor(String str) {
      if (str != null) {
         EnumDyeColor[] aenumdyecolor = EnumDyeColor.values();

         for(EnumDyeColor enumdyecolor : aenumdyecolor) {
            if (enumdyecolor.getName().equals(str)) {
               return enumdyecolor;
            }

            if (enumdyecolor.getUnlocalizedName().equals(str)) {
               return enumdyecolor;
            }
         }
      }

      return null;
   }

   private static ResourceLocation parseTextureLocation(String str, String basePath) {
      if (str == null) {
         return null;
      } else {
         str = str.trim();
         String s = TextureUtils.fixResourcePath(str, basePath);
         if (!s.endsWith(".png")) {
            s = s + ".png";
         }

         return new ResourceLocation(basePath + "/" + s);
      }
   }

   private static Map<ResourceLocation, ResourceLocation> parseTextureLocations(Properties props, CustomGuiProperties.EnumContainer container, String basePath) {
      HashMap map = new HashMap();
      String s = props.getProperty("texture");
      if (s != null) {
         ResourceLocation resourcelocation = getGuiTextureLocation(container);
         ResourceLocation resourcelocation1 = parseTextureLocation(s, basePath);
         if (resourcelocation != null && resourcelocation1 != null) {
            map.put(resourcelocation, resourcelocation1);
         }
      }

      String s5 = "texture.";

      for(Object e : props.keySet()) {
         String s1 = (String)e;
         if (s1.startsWith(s5)) {
            String s2 = s1.substring(s5.length());
            s2 = s2.replace('\\', '/');
            s2 = StrUtils.removePrefixSuffix(s2, "/", ".png");
            String s3 = "textures/gui/" + s2 + ".png";
            String s4 = props.getProperty(s1);
            ResourceLocation resourcelocation2 = new ResourceLocation(s3);
            ResourceLocation resourcelocation3 = parseTextureLocation(s4, basePath);
            map.put(resourcelocation2, resourcelocation3);
         }
      }

      return map;
   }

   private static ResourceLocation getGuiTextureLocation(CustomGuiProperties.EnumContainer container) {
      switch(container) {
         case ANVIL:
            return ANVIL_GUI_TEXTURE;
         case BEACON:
            return BEACON_GUI_TEXTURE;
         case BREWING_STAND:
            return BREWING_STAND_GUI_TEXTURE;
         case CHEST:
            return CHEST_GUI_TEXTURE;
         case CRAFTING:
            return CRAFTING_TABLE_GUI_TEXTURE;
         case DISPENSER:
            return DISPENSER_GUI_TEXTURE;
         case ENCHANTMENT:
            return ENCHANTMENT_TABLE_GUI_TEXTURE;
         case FURNACE:
            return FURNACE_GUI_TEXTURE;
         case HOPPER:
            return HOPPER_GUI_TEXTURE;
         case HORSE:
            return HORSE_GUI_TEXTURE;
         case INVENTORY:
            return INVENTORY_GUI_TEXTURE;
         case SHULKER_BOX:
            return SHULKER_BOX_GUI_TEXTURE;
         case VILLAGER:
            return VILLAGER_GUI_TEXTURE;
         case CREATIVE:
         default:
            return null;
      }
   }

   public boolean isValid(String path) {
      if (this.fileName == null || this.fileName.length() <= 0) {
         warn("No name found: " + path);
         return false;
      } else if (this.basePath == null) {
         warn("No base path found: " + path);
         return false;
      } else if (this.container == null) {
         warn("No container found: " + path);
         return false;
      } else if (this.textureLocations.isEmpty()) {
         warn("No texture found: " + path);
         return false;
      } else if (this.professions == ConnectedParser.PROFESSIONS_INVALID) {
         warn("Invalid professions or careers: " + path);
         return false;
      } else if (this.variants == VARIANTS_INVALID) {
         warn("Invalid variants: " + path);
         return false;
      } else if (this.colors == COLORS_INVALID) {
         warn("Invalid colors: " + path);
         return false;
      } else {
         return true;
      }
   }

   private static void warn(String str) {
      Config.warn("[CustomGuis] " + str);
   }

   private boolean matchesGeneral(CustomGuiProperties.EnumContainer ec, BlockPos pos, IBlockAccess blockAccess) {
      if (this.container != ec) {
         return true;
      } else {
         if (this.biomes != null) {
            BiomeGenBase biomegenbase = blockAccess.getBiomeGenForCoords(pos);
            if (!Matches.biome(biomegenbase, this.biomes)) {
               return true;
            }
         }

         return this.heights != null && !this.heights.isInRange(pos.getY());
      }
   }

   public boolean matchesPos(CustomGuiProperties.EnumContainer ec, BlockPos pos, IBlockAccess blockAccess, GuiScreen screen) {
      if (this.matchesGeneral(ec, pos, blockAccess)) {
         return false;
      } else {
         if (this.nbtName != null) {
            String s = getName(screen);
            if (!this.nbtName.matchesValue(s)) {
               return false;
            }
         }

         switch(ec) {
            case BEACON:
               return this.matchesBeacon(pos, blockAccess);
            case BREWING_STAND:
            case CRAFTING:
            default:
               return true;
            case CHEST:
               return this.matchesChest(pos, blockAccess);
            case DISPENSER:
               return this.matchesDispenser(pos, blockAccess);
         }
      }
   }

   public static String getName(GuiScreen screen) {
      IWorldNameable iworldnameable = getWorldNameable(screen);
      return iworldnameable == null ? null : iworldnameable.getDisplayName().getUnformattedText();
   }

   private static IWorldNameable getWorldNameable(GuiScreen screen) {
      return (IWorldNameable)(screen instanceof GuiBeacon
         ? getWorldNameable(screen, Reflector.GuiBeacon_tileBeacon)
         : (
            screen instanceof GuiBrewingStand
               ? getWorldNameable(screen, Reflector.GuiBrewingStand_tileBrewingStand)
               : (
                  screen instanceof GuiChest
                     ? getWorldNameable(screen, Reflector.GuiChest_lowerChestInventory)
                     : (
                        screen instanceof GuiDispenser
                           ? ((GuiDispenser)screen).dispenserInventory
                           : (
                              screen instanceof GuiEnchantment
                                 ? getWorldNameable(screen, Reflector.GuiEnchantment_nameable)
                                 : (
                                    screen instanceof GuiFurnace
                                       ? getWorldNameable(screen, Reflector.GuiFurnace_tileFurnace)
                                       : (screen instanceof GuiHopper ? getWorldNameable(screen, Reflector.GuiHopper_hopperInventory) : null)
                                 )
                           )
                     )
               )
         ));
   }

   private static IWorldNameable getWorldNameable(GuiScreen screen, ReflectorField fieldInventory) {
      Object object = Reflector.getFieldValue(screen, fieldInventory);
      return !(object instanceof IWorldNameable) ? null : (IWorldNameable)object;
   }

   private boolean matchesBeacon(BlockPos pos, IBlockAccess blockAccess) {
      TileEntity tileentity = blockAccess.getTileEntity(pos);
      if (!(tileentity instanceof TileEntityBeacon)) {
         return false;
      } else {
         TileEntityBeacon tileentitybeacon = (TileEntityBeacon)tileentity;
         if (this.levels != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            tileentitybeacon.writeToNBT(nbttagcompound);
            int i = nbttagcompound.getInteger("Levels");
            return this.levels.isInRange(i);
         } else {
            return true;
         }
      }
   }

   private boolean matchesChest(BlockPos pos, IBlockAccess blockAccess) {
      TileEntity tileentity = blockAccess.getTileEntity(pos);
      if (tileentity instanceof TileEntityChest) {
         TileEntityChest tileentitychest = (TileEntityChest)tileentity;
         return this.matchesChest(tileentitychest);
      } else {
         return tileentity instanceof TileEntityEnderChest ? this.matchesEnderChest() : false;
      }
   }

   private boolean matchesChest(TileEntityChest tec) {
      boolean flag = tec.adjacentChestXNeg != null || tec.adjacentChestXPos != null || tec.adjacentChestZNeg != null || tec.adjacentChestZPos != null;
      boolean flag1 = tec.getChestType() == 1;
      boolean flag2 = CustomGuis.isChristmas;
      return this.matchesChest(flag, flag1, flag2, false);
   }

   private boolean matchesEnderChest() {
      return this.matchesChest(false, false, false, true);
   }

   private boolean matchesChest(boolean isLarge, boolean isTrapped, boolean isChristmas, boolean isEnder) {
      return (this.large == null || this.large == isLarge)
         && (this.trapped == null || this.trapped == isTrapped)
         && (this.christmas == null || this.christmas == isChristmas)
         && (this.ender == null || this.ender == isEnder);
   }

   private boolean matchesDispenser(BlockPos pos, IBlockAccess blockAccess) {
      TileEntity tileentity = blockAccess.getTileEntity(pos);
      if (!(tileentity instanceof TileEntityDispenser)) {
         return false;
      } else {
         TileEntityDispenser tileentitydispenser = (TileEntityDispenser)tileentity;
         if (this.variants != null) {
            CustomGuiProperties.EnumVariant customguiproperties$enumvariant = this.getDispenserVariant(tileentitydispenser);
            return Config.equalsOne(customguiproperties$enumvariant, this.variants);
         } else {
            return true;
         }
      }
   }

   private CustomGuiProperties.EnumVariant getDispenserVariant(TileEntityDispenser ted) {
      return ted instanceof TileEntityDropper ? CustomGuiProperties.EnumVariant.DROPPER : CustomGuiProperties.EnumVariant.DISPENSER;
   }

   public boolean matchesEntity(CustomGuiProperties.EnumContainer ec, Entity entity, IBlockAccess blockAccess) {
      if (this.matchesGeneral(ec, entity.getPosition(), blockAccess)) {
         return false;
      } else {
         if (this.nbtName != null) {
            String s = entity.getCommandSenderName();
            if (!this.nbtName.matchesValue(s)) {
               return false;
            }
         }

         switch(ec) {
            case HORSE:
               return this.matchesHorse(entity);
            case VILLAGER:
               return this.matchesVillager(entity);
            default:
               return true;
         }
      }
   }

   private boolean matchesVillager(Entity entity) {
      if (!(entity instanceof EntityVillager)) {
         return false;
      } else {
         EntityVillager entityvillager = (EntityVillager)entity;
         if (this.professions == null) {
            return true;
         } else {
            int i = entityvillager.getProfession();
            int j = Reflector.getFieldValueInt(entityvillager, Reflector.EntityVillager_careerId, -1);
            if (j < 0) {
               return false;
            } else {
               boolean flag = false;

               for(VillagerProfession villagerprofession : this.professions) {
                  if (villagerprofession.matches(i, j)) {
                     flag = true;
                     break;
                  }
               }

               return flag;
            }
         }
      }
   }

   private boolean matchesHorse(Entity entity) {
      if (!(entity instanceof EntityHorse)) {
         return false;
      } else {
         EntityHorse entityhorse = (EntityHorse)entity;
         if (this.variants != null) {
            CustomGuiProperties.EnumVariant customguiproperties$enumvariant = this.getHorseVariant(entityhorse);
            return Config.equalsOne(customguiproperties$enumvariant, this.variants);
         } else {
            return true;
         }
      }
   }

   private CustomGuiProperties.EnumVariant getHorseVariant(EntityHorse entity) {
      int i = entity.getHorseType();
      switch(i) {
         case 0:
            return CustomGuiProperties.EnumVariant.HORSE;
         case 1:
            return CustomGuiProperties.EnumVariant.DONKEY;
         case 2:
            return CustomGuiProperties.EnumVariant.MULE;
         default:
            return null;
      }
   }

   public CustomGuiProperties.EnumContainer getContainer() {
      return this.container;
   }

   public ResourceLocation getTextureLocation(ResourceLocation loc) {
      ResourceLocation resourcelocation = this.textureLocations.get(loc);
      return resourcelocation == null ? loc : resourcelocation;
   }

   @Override
   public String toString() {
      return "name: " + this.fileName + ", container: " + this.container + ", textures: " + this.textureLocations;
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

      public static final CustomGuiProperties.EnumContainer[] VALUES = values();
   }

   private static enum EnumVariant {
      HORSE,
      DONKEY,
      MULE,
      LLAMA,
      DISPENSER,
      DROPPER;
   }
}
