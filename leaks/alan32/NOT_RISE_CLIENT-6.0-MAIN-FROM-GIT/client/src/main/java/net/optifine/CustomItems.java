package net.optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.NbtTagValue;
import net.optifine.render.Blender;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CustomItems {
    private static CustomItemProperties[][] itemProperties = null;
    private static CustomItemProperties[][] enchantmentProperties = null;
    private static Map mapPotionIds = null;
    private static final ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
    private static boolean useGlint = true;
    private static final boolean renderOffHand = false;
    public static final int MASK_POTION_SPLASH = 16384;
    public static final int MASK_POTION_NAME = 63;
    public static final int MASK_POTION_EXTENDED = 64;
    public static final String KEY_TEXTURE_OVERLAY = "texture.potion_overlay";
    public static final String KEY_TEXTURE_SPLASH = "texture.potion_bottle_splash";
    public static final String KEY_TEXTURE_DRINKABLE = "texture.potion_bottle_drinkable";
    public static final String DEFAULT_TEXTURE_OVERLAY = "items/potion_overlay";
    public static final String DEFAULT_TEXTURE_SPLASH = "items/potion_bottle_splash";
    public static final String DEFAULT_TEXTURE_DRINKABLE = "items/potion_bottle_drinkable";
    private static final int[][] EMPTY_INT2_ARRAY = new int[0][];
    private static final String TYPE_POTION_NORMAL = "normal";
    private static final String TYPE_POTION_SPLASH = "splash";
    private static final String TYPE_POTION_LINGER = "linger";

    public static void update() {
        itemProperties = null;
        enchantmentProperties = null;
        useGlint = true;

        if (Config.isCustomItems()) {
            readCitProperties("mcpatcher/cit.properties");
            final IResourcePack[] airesourcepack = Config.getResourcePacks();

            for (int i = airesourcepack.length - 1; i >= 0; --i) {
                final IResourcePack iresourcepack = airesourcepack[i];
                update(iresourcepack);
            }

            update(Config.getDefaultResourcePack());

            if (itemProperties.length <= 0) {
                itemProperties = null;
            }

            if (enchantmentProperties.length <= 0) {
                enchantmentProperties = null;
            }
        }
    }

    private static void readCitProperties(final String fileName) {
        try {
            final ResourceLocation resourcelocation = new ResourceLocation(fileName);
            final InputStream inputstream = Config.getResourceStream(resourcelocation);

            if (inputstream == null) {
                return;
            }

            Config.dbg("CustomItems: Loading " + fileName);
            final Properties properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            useGlint = Config.parseBoolean(properties.getProperty("useGlint"), true);
        } catch (final FileNotFoundException var4) {
            return;
        } catch (final IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private static void update(final IResourcePack rp) {
        String[] astring = ResUtils.collectFiles(rp, "mcpatcher/cit/", ".properties", null);
        final Map map = makeAutoImageProperties(rp);

        if (map.size() > 0) {
            final Set set = map.keySet();
            final String[] astring1 = (String[]) set.toArray(new String[set.size()]);
            astring = (String[]) Config.addObjectsToArray(astring, astring1);
        }

        Arrays.sort(astring);
        final List list = makePropertyList(itemProperties);
        final List list1 = makePropertyList(enchantmentProperties);

        for (int i = 0; i < astring.length; ++i) {
            final String s = astring[i];
            Config.dbg("CustomItems: " + s);

            try {
                CustomItemProperties customitemproperties = null;

                if (map.containsKey(s)) {
                    customitemproperties = (CustomItemProperties) map.get(s);
                }

                if (customitemproperties == null) {
                    final ResourceLocation resourcelocation = new ResourceLocation(s);
                    final InputStream inputstream = rp.getInputStream(resourcelocation);

                    if (inputstream == null) {
                        Config.warn("CustomItems file not found: " + s);
                        continue;
                    }

                    final Properties properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    customitemproperties = new CustomItemProperties(properties, s);
                }

                if (customitemproperties.isValid(s)) {
                    addToItemList(customitemproperties, list);
                    addToEnchantmentList(customitemproperties, list1);
                }
            } catch (final FileNotFoundException var11) {
                Config.warn("CustomItems file not found: " + s);
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }

        itemProperties = propertyListToArray(list);
        enchantmentProperties = propertyListToArray(list1);
        final Comparator comparator = getPropertiesComparator();

        for (int j = 0; j < itemProperties.length; ++j) {
            final CustomItemProperties[] acustomitemproperties = itemProperties[j];

            if (acustomitemproperties != null) {
                Arrays.sort(acustomitemproperties, comparator);
            }
        }

        for (int k = 0; k < enchantmentProperties.length; ++k) {
            final CustomItemProperties[] acustomitemproperties1 = enchantmentProperties[k];

            if (acustomitemproperties1 != null) {
                Arrays.sort(acustomitemproperties1, comparator);
            }
        }
    }

    private static Comparator getPropertiesComparator() {
        final Comparator comparator = new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final CustomItemProperties customitemproperties = (CustomItemProperties) o1;
                final CustomItemProperties customitemproperties1 = (CustomItemProperties) o2;
                return customitemproperties.layer != customitemproperties1.layer ? customitemproperties.layer - customitemproperties1.layer : (customitemproperties.weight != customitemproperties1.weight ? customitemproperties1.weight - customitemproperties.weight : (!customitemproperties.basePath.equals(customitemproperties1.basePath) ? customitemproperties.basePath.compareTo(customitemproperties1.basePath) : customitemproperties.name.compareTo(customitemproperties1.name)));
            }
        };
        return comparator;
    }

    public static void updateIcons(final TextureMap textureMap) {
        for (final CustomItemProperties customitemproperties : getAllProperties()) {
            customitemproperties.updateIcons(textureMap);
        }
    }

    public static void loadModels(final ModelBakery modelBakery) {
        for (final CustomItemProperties customitemproperties : getAllProperties()) {
            customitemproperties.loadModels(modelBakery);
        }
    }

    public static void updateModels() {
        for (final CustomItemProperties customitemproperties : getAllProperties()) {
            if (customitemproperties.type == 1) {
                final TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
                customitemproperties.updateModelTexture(texturemap, itemModelGenerator);
                customitemproperties.updateModelsFull();
            }
        }
    }

    private static List<CustomItemProperties> getAllProperties() {
        final List<CustomItemProperties> list = new ArrayList();
        addAll(itemProperties, list);
        addAll(enchantmentProperties, list);
        return list;
    }

    private static void addAll(final CustomItemProperties[][] cipsArr, final List<CustomItemProperties> list) {
        if (cipsArr != null) {
            for (int i = 0; i < cipsArr.length; ++i) {
                final CustomItemProperties[] acustomitemproperties = cipsArr[i];

                if (acustomitemproperties != null) {
                    for (int j = 0; j < acustomitemproperties.length; ++j) {
                        final CustomItemProperties customitemproperties = acustomitemproperties[j];

                        if (customitemproperties != null) {
                            list.add(customitemproperties);
                        }
                    }
                }
            }
        }
    }

    private static Map makeAutoImageProperties(final IResourcePack rp) {
        final Map map = new HashMap();
        map.putAll(makePotionImageProperties(rp, "normal", Item.getIdFromItem(Items.potionitem)));
        map.putAll(makePotionImageProperties(rp, "splash", Item.getIdFromItem(Items.potionitem)));
        map.putAll(makePotionImageProperties(rp, "linger", Item.getIdFromItem(Items.potionitem)));
        return map;
    }

    private static Map makePotionImageProperties(final IResourcePack rp, final String type, final int itemId) {
        final Map map = new HashMap();
        final String s = type + "/";
        final String[] astring = new String[]{"mcpatcher/cit/potion/" + s, "mcpatcher/cit/Potion/" + s};
        final String[] astring1 = new String[]{".png"};
        final String[] astring2 = ResUtils.collectFiles(rp, astring, astring1);

        for (int i = 0; i < astring2.length; ++i) {
            final String s1 = astring2[i];
            final String name = StrUtils.removePrefixSuffix(s1, astring, astring1);
            final Properties properties = makePotionProperties(name, type, itemId, s1);

            if (properties != null) {
                final String s3 = StrUtils.removeSuffix(s1, astring1) + ".properties";
                final CustomItemProperties customitemproperties = new CustomItemProperties(properties, s3);
                map.put(s3, customitemproperties);
            }
        }

        return map;
    }

    private static Properties makePotionProperties(final String name, final String type, int itemId, final String path) {
        if (StrUtils.endsWith(name, new String[]{"_n", "_s"})) {
            return null;
        } else if (name.equals("empty") && type.equals("normal")) {
            itemId = Item.getIdFromItem(Items.glass_bottle);
            final Properties properties = new PropertiesOrdered();
            properties.put("type", "item");
            properties.put("items", "" + itemId);
            return properties;
        } else {
            final int[] aint = (int[]) getMapPotionIds().get(name);

            if (aint == null) {
                Config.warn("Potion not found for image: " + path);
                return null;
            } else {
                final StringBuffer stringbuffer = new StringBuffer();

                for (int i = 0; i < aint.length; ++i) {
                    int j = aint[i];

                    if (type.equals("splash")) {
                        j |= 16384;
                    }

                    if (i > 0) {
                        stringbuffer.append(" ");
                    }

                    stringbuffer.append(j);
                }

                int k = 16447;

                if (name.equals("water") || name.equals("mundane")) {
                    k |= 64;
                }

                final Properties properties1 = new PropertiesOrdered();
                properties1.put("type", "item");
                properties1.put("items", "" + itemId);
                properties1.put("damage", "" + stringbuffer);
                properties1.put("damageMask", "" + k);

                if (type.equals("splash")) {
                    properties1.put("texture.potion_bottle_splash", name);
                } else {
                    properties1.put("texture.potion_bottle_drinkable", name);
                }

                return properties1;
            }
        }
    }

    private static Map getMapPotionIds() {
        if (mapPotionIds == null) {
            mapPotionIds = new LinkedHashMap();
            mapPotionIds.put("water", getPotionId(0, 0));
            mapPotionIds.put("awkward", getPotionId(0, 1));
            mapPotionIds.put("thick", getPotionId(0, 2));
            mapPotionIds.put("potent", getPotionId(0, 3));
            mapPotionIds.put("regeneration", getPotionIds(1));
            mapPotionIds.put("movespeed", getPotionIds(2));
            mapPotionIds.put("fireresistance", getPotionIds(3));
            mapPotionIds.put("poison", getPotionIds(4));
            mapPotionIds.put("heal", getPotionIds(5));
            mapPotionIds.put("nightvision", getPotionIds(6));
            mapPotionIds.put("clear", getPotionId(7, 0));
            mapPotionIds.put("bungling", getPotionId(7, 1));
            mapPotionIds.put("charming", getPotionId(7, 2));
            mapPotionIds.put("rank", getPotionId(7, 3));
            mapPotionIds.put("weakness", getPotionIds(8));
            mapPotionIds.put("damageboost", getPotionIds(9));
            mapPotionIds.put("moveslowdown", getPotionIds(10));
            mapPotionIds.put("leaping", getPotionIds(11));
            mapPotionIds.put("harm", getPotionIds(12));
            mapPotionIds.put("waterbreathing", getPotionIds(13));
            mapPotionIds.put("invisibility", getPotionIds(14));
            mapPotionIds.put("thin", getPotionId(15, 0));
            mapPotionIds.put("debonair", getPotionId(15, 1));
            mapPotionIds.put("sparkling", getPotionId(15, 2));
            mapPotionIds.put("stinky", getPotionId(15, 3));
            mapPotionIds.put("mundane", getPotionId(0, 4));
            mapPotionIds.put("speed", mapPotionIds.get("movespeed"));
            mapPotionIds.put("fire_resistance", mapPotionIds.get("fireresistance"));
            mapPotionIds.put("instant_health", mapPotionIds.get("heal"));
            mapPotionIds.put("night_vision", mapPotionIds.get("nightvision"));
            mapPotionIds.put("strength", mapPotionIds.get("damageboost"));
            mapPotionIds.put("slowness", mapPotionIds.get("moveslowdown"));
            mapPotionIds.put("instant_damage", mapPotionIds.get("harm"));
            mapPotionIds.put("water_breathing", mapPotionIds.get("waterbreathing"));
        }

        return mapPotionIds;
    }

    private static int[] getPotionIds(final int baseId) {
        return new int[]{baseId, baseId + 16, baseId + 32, baseId + 48};
    }

    private static int[] getPotionId(final int baseId, final int subId) {
        return new int[]{baseId + subId * 16};
    }

    private static int getPotionNameDamage(final String name) {
        final String s = "potion." + name;
        final Potion[] apotion = Potion.potionTypes;

        for (int i = 0; i < apotion.length; ++i) {
            final Potion potion = apotion[i];

            if (potion != null) {
                final String s1 = potion.getName();

                if (s.equals(s1)) {
                    return potion.getId();
                }
            }
        }

        return -1;
    }

    private static List makePropertyList(final CustomItemProperties[][] propsArr) {
        final List list = new ArrayList();

        if (propsArr != null) {
            for (int i = 0; i < propsArr.length; ++i) {
                final CustomItemProperties[] acustomitemproperties = propsArr[i];
                List list1 = null;

                if (acustomitemproperties != null) {
                    list1 = new ArrayList(Arrays.asList(acustomitemproperties));
                }

                list.add(list1);
            }
        }

        return list;
    }

    private static CustomItemProperties[][] propertyListToArray(final List lists) {
        final CustomItemProperties[][] acustomitemproperties = new CustomItemProperties[lists.size()][];

        for (int i = 0; i < lists.size(); ++i) {
            final List list = (List) lists.get(i);

            if (list != null) {
                final CustomItemProperties[] acustomitemproperties1 = (CustomItemProperties[]) list.toArray(new CustomItemProperties[list.size()]);
                Arrays.sort(acustomitemproperties1, new CustomItemsComparator());
                acustomitemproperties[i] = acustomitemproperties1;
            }
        }

        return acustomitemproperties;
    }

    private static void addToItemList(final CustomItemProperties cp, final List itemList) {
        if (cp.items != null) {
            for (int i = 0; i < cp.items.length; ++i) {
                final int j = cp.items[i];

                if (j <= 0) {
                    Config.warn("Invalid item ID: " + j);
                } else {
                    addToList(cp, itemList, j);
                }
            }
        }
    }

    private static void addToEnchantmentList(final CustomItemProperties cp, final List enchantmentList) {
        if (cp.type == 2) {
            if (cp.enchantmentIds != null) {
                for (int i = 0; i < 256; ++i) {
                    if (cp.enchantmentIds.isInRange(i)) {
                        addToList(cp, enchantmentList, i);
                    }
                }
            }
        }
    }

    private static void addToList(final CustomItemProperties cp, final List lists, final int id) {
        while (id >= lists.size()) {
            lists.add(null);
        }

        List list = (List) lists.get(id);

        if (list == null) {
            list = new ArrayList();
            list.set(id, list);
        }

        list.add(cp);
    }

    public static IBakedModel getCustomItemModel(final ItemStack itemStack, final IBakedModel model, final ResourceLocation modelLocation, final boolean fullModel) {
        if (!fullModel && model.isGui3d()) {
            return model;
        } else if (itemProperties == null) {
            return model;
        } else {
            final CustomItemProperties customitemproperties = getCustomItemProperties(itemStack, 1);

            if (customitemproperties == null) {
                return model;
            } else {
                final IBakedModel ibakedmodel = customitemproperties.getBakedModel(modelLocation, fullModel);
                return ibakedmodel != null ? ibakedmodel : model;
            }
        }
    }

    public static boolean bindCustomArmorTexture(final ItemStack itemStack, final int layer, final String overlay) {
        if (itemProperties == null) {
            return false;
        } else {
            final ResourceLocation resourcelocation = getCustomArmorLocation(itemStack, layer, overlay);

            if (resourcelocation == null) {
                return false;
            } else {
                Config.getTextureManager().bindTexture(resourcelocation);
                return true;
            }
        }
    }

    private static ResourceLocation getCustomArmorLocation(final ItemStack itemStack, final int layer, final String overlay) {
        final CustomItemProperties customitemproperties = getCustomItemProperties(itemStack, 3);

        if (customitemproperties == null) {
            return null;
        } else if (customitemproperties.mapTextureLocations == null) {
            return customitemproperties.textureLocation;
        } else {
            final Item item = itemStack.getItem();

            if (!(item instanceof ItemArmor)) {
                return null;
            } else {
                final ItemArmor itemarmor = (ItemArmor) item;
                final String s = itemarmor.getArmorMaterial().getName();
                final StringBuffer stringbuffer = new StringBuffer();
                stringbuffer.append("texture.");
                stringbuffer.append(s);
                stringbuffer.append("_layer_");
                stringbuffer.append(layer);

                if (overlay != null) {
                    stringbuffer.append("_");
                    stringbuffer.append(overlay);
                }

                final String s1 = stringbuffer.toString();
                final ResourceLocation resourcelocation = (ResourceLocation) customitemproperties.mapTextureLocations.get(s1);
                return resourcelocation == null ? customitemproperties.textureLocation : resourcelocation;
            }
        }
    }

    private static CustomItemProperties getCustomItemProperties(final ItemStack itemStack, final int type) {
        if (itemProperties == null) {
            return null;
        } else if (itemStack == null) {
            return null;
        } else {
            final Item item = itemStack.getItem();
            final int i = Item.getIdFromItem(item);

            if (i >= 0 && i < itemProperties.length) {
                final CustomItemProperties[] acustomitemproperties = itemProperties[i];

                if (acustomitemproperties != null) {
                    for (int j = 0; j < acustomitemproperties.length; ++j) {
                        final CustomItemProperties customitemproperties = acustomitemproperties[j];

                        if (customitemproperties.type == type && matchesProperties(customitemproperties, itemStack, null)) {
                            return customitemproperties;
                        }
                    }
                }
            }

            return null;
        }
    }

    private static boolean matchesProperties(final CustomItemProperties cip, final ItemStack itemStack, final int[][] enchantmentIdLevels) {
        final Item item = itemStack.getItem();

        if (cip.damage != null) {
            int i = itemStack.getItemDamage();

            if (cip.damageMask != 0) {
                i &= cip.damageMask;
            }

            if (cip.damagePercent) {
                final int j = item.getMaxDamage();
                i = (int) ((double) (i * 100) / (double) j);
            }

            if (!cip.damage.isInRange(i)) {
                return false;
            }
        }

        if (cip.stackSize != null && !cip.stackSize.isInRange(itemStack.stackSize)) {
            return false;
        } else {
            int[][] aint = enchantmentIdLevels;

            if (cip.enchantmentIds != null) {
                if (enchantmentIdLevels == null) {
                    aint = getEnchantmentIdLevels(itemStack);
                }

                boolean flag = false;

                for (int k = 0; k < aint.length; ++k) {
                    final int l = aint[k][0];

                    if (cip.enchantmentIds.isInRange(l)) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    return false;
                }
            }

            if (cip.enchantmentLevels != null) {
                if (aint == null) {
                    aint = getEnchantmentIdLevels(itemStack);
                }

                boolean flag1 = false;

                for (int i1 = 0; i1 < aint.length; ++i1) {
                    final int k1 = aint[i1][1];

                    if (cip.enchantmentLevels.isInRange(k1)) {
                        flag1 = true;
                        break;
                    }
                }

                if (!flag1) {
                    return false;
                }
            }

            if (cip.nbtTagValues != null) {
                final NBTTagCompound nbttagcompound = itemStack.getTagCompound();

                for (int j1 = 0; j1 < cip.nbtTagValues.length; ++j1) {
                    final NbtTagValue nbttagvalue = cip.nbtTagValues[j1];

                    if (!nbttagvalue.matches(nbttagcompound)) {
                        return false;
                    }
                }
            }

            if (cip.hand != 0) {
                if (cip.hand == 1 && renderOffHand) {
                    return false;
                }

                return cip.hand != 2 || renderOffHand;
            }

            return true;
        }
    }

    private static int[][] getEnchantmentIdLevels(final ItemStack itemStack) {
        final Item item = itemStack.getItem();
        final NBTTagList nbttaglist = item == Items.enchanted_book ? Items.enchanted_book.getEnchantments(itemStack) : itemStack.getEnchantmentTagList();

        if (nbttaglist != null && nbttaglist.tagCount() > 0) {
            final int[][] aint = new int[nbttaglist.tagCount()][2];

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                final NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                final int j = nbttagcompound.getShort("id");
                final int k = nbttagcompound.getShort("lvl");
                aint[i][0] = j;
                aint[i][1] = k;
            }

            return aint;
        } else {
            return EMPTY_INT2_ARRAY;
        }
    }

    public static boolean renderCustomEffect(final RenderItem renderItem, final ItemStack itemStack, final IBakedModel model) {
        if (enchantmentProperties == null) {
            return false;
        } else if (itemStack == null) {
            return false;
        } else {
            final int[][] aint = getEnchantmentIdLevels(itemStack);

            if (aint.length <= 0) {
                return false;
            } else {
                Set set = null;
                boolean flag = false;
                final TextureManager texturemanager = Config.getTextureManager();

                for (int i = 0; i < aint.length; ++i) {
                    final int j = aint[i][0];

                    if (j >= 0 && j < enchantmentProperties.length) {
                        final CustomItemProperties[] acustomitemproperties = enchantmentProperties[j];

                        if (acustomitemproperties != null) {
                            for (int k = 0; k < acustomitemproperties.length; ++k) {
                                final CustomItemProperties customitemproperties = acustomitemproperties[k];

                                if (set == null) {
                                    set = new HashSet();
                                }

                                if (set.add(Integer.valueOf(j)) && matchesProperties(customitemproperties, itemStack, aint) && customitemproperties.textureLocation != null) {
                                    texturemanager.bindTexture(customitemproperties.textureLocation);
                                    final float f = customitemproperties.getTextureWidth(texturemanager);

                                    if (!flag) {
                                        flag = true;
                                        GlStateManager.depthMask(false);
                                        GlStateManager.depthFunc(514);
                                        GlStateManager.disableLighting();
                                        GlStateManager.matrixMode(5890);
                                    }

                                    Blender.setupBlend(customitemproperties.blend, 1.0F);
                                    GlStateManager.pushMatrix();
                                    GlStateManager.scale(f / 2.0F, f / 2.0F, f / 2.0F);
                                    final float f1 = customitemproperties.speed * (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                                    GlStateManager.translate(f1, 0.0F, 0.0F);
                                    GlStateManager.rotate(customitemproperties.rotation, 0.0F, 0.0F, 1.0F);
                                    renderItem.renderModel(model, -1);
                                    GlStateManager.popMatrix();
                                }
                            }
                        }
                    }
                }

                if (flag) {
                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(770, 771);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.matrixMode(5888);
                    GlStateManager.enableLighting();
                    GlStateManager.depthFunc(515);
                    GlStateManager.depthMask(true);
                    texturemanager.bindTexture(TextureMap.locationBlocksTexture);
                }

                return flag;
            }
        }
    }

    public static boolean renderCustomArmorEffect(final EntityLivingBase entity, final ItemStack itemStack, final ModelBase model, final float limbSwing, final float prevLimbSwing, final float partialTicks, final float timeLimbSwing, final float yaw, final float pitch, final float scale) {
        if (enchantmentProperties == null) {
            return false;
        } else if (Config.isShaders() && Shaders.isShadowPass) {
            return false;
        } else if (itemStack == null) {
            return false;
        } else {
            final int[][] aint = getEnchantmentIdLevels(itemStack);

            if (aint.length <= 0) {
                return false;
            } else {
                Set set = null;
                boolean flag = false;
                final TextureManager texturemanager = Config.getTextureManager();

                for (int i = 0; i < aint.length; ++i) {
                    final int j = aint[i][0];

                    if (j >= 0 && j < enchantmentProperties.length) {
                        final CustomItemProperties[] acustomitemproperties = enchantmentProperties[j];

                        if (acustomitemproperties != null) {
                            for (int k = 0; k < acustomitemproperties.length; ++k) {
                                final CustomItemProperties customitemproperties = acustomitemproperties[k];

                                if (set == null) {
                                    set = new HashSet();
                                }

                                if (set.add(Integer.valueOf(j)) && matchesProperties(customitemproperties, itemStack, aint) && customitemproperties.textureLocation != null) {
                                    texturemanager.bindTexture(customitemproperties.textureLocation);
                                    final float f = customitemproperties.getTextureWidth(texturemanager);

                                    if (!flag) {
                                        flag = true;

                                        if (Config.isShaders()) {
                                            ShadersRender.renderEnchantedGlintBegin();
                                        }

                                        GlStateManager.enableBlend();
                                        GlStateManager.depthFunc(514);
                                        GlStateManager.depthMask(false);
                                    }

                                    Blender.setupBlend(customitemproperties.blend, 1.0F);
                                    GlStateManager.disableLighting();
                                    GlStateManager.matrixMode(5890);
                                    GlStateManager.loadIdentity();
                                    GlStateManager.rotate(customitemproperties.rotation, 0.0F, 0.0F, 1.0F);
                                    final float f1 = f / 8.0F;
                                    GlStateManager.scale(f1, f1 / 2.0F, f1);
                                    final float f2 = customitemproperties.speed * (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                                    GlStateManager.translate(0.0F, f2, 0.0F);
                                    GlStateManager.matrixMode(5888);
                                    model.render(entity, limbSwing, prevLimbSwing, timeLimbSwing, yaw, pitch, scale);
                                }
                            }
                        }
                    }
                }

                if (flag) {
                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(770, 771);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.loadIdentity();
                    GlStateManager.matrixMode(5888);
                    GlStateManager.enableLighting();
                    GlStateManager.depthMask(true);
                    GlStateManager.depthFunc(515);
                    GlStateManager.disableBlend();

                    if (Config.isShaders()) {
                        ShadersRender.renderEnchantedGlintEnd();
                    }
                }

                return flag;
            }
        }
    }

    public static boolean isUseGlint() {
        return useGlint;
    }
}
