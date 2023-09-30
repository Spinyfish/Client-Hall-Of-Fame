/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.optifine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.IParserInt;
import net.optifine.config.NbtTagValue;
import net.optifine.config.ParserEnchantmentId;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.reflect.Reflector;
import net.optifine.render.Blender;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

public class CustomItemProperties {
    public String name = null;
    public String basePath = null;
    public int type = 1;
    public int[] items = null;
    public String texture = null;
    public Map<String, String> mapTextures = null;
    public String model = null;
    public Map<String, String> mapModels = null;
    public RangeListInt damage = null;
    public boolean damagePercent = false;
    public int damageMask = 0;
    public RangeListInt stackSize = null;
    public RangeListInt enchantmentIds = null;
    public RangeListInt enchantmentLevels = null;
    public NbtTagValue[] nbtTagValues = null;
    public int hand = 0;
    public int blend = 1;
    public float speed = 0.0f;
    public float rotation = 0.0f;
    public int layer = 0;
    public float duration = 1.0f;
    public int weight = 0;
    public ResourceLocation textureLocation = null;
    public Map mapTextureLocations = null;
    public TextureAtlasSprite sprite = null;
    public Map mapSprites = null;
    public IBakedModel bakedModelTexture = null;
    public Map<String, IBakedModel> mapBakedModelsTexture = null;
    public IBakedModel bakedModelFull = null;
    public Map<String, IBakedModel> mapBakedModelsFull = null;
    private int textureWidth = 0;
    private int textureHeight = 0;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_ENCHANTMENT = 2;
    public static final int TYPE_ARMOR = 3;
    public static final int HAND_ANY = 0;
    public static final int HAND_MAIN = 1;
    public static final int HAND_OFF = 2;
    public static final String INVENTORY = "inventory";

    public CustomItemProperties(Properties props, String path) {
        this.name = CustomItemProperties.parseName(path);
        this.basePath = CustomItemProperties.parseBasePath(path);
        this.type = this.parseType(props.getProperty("type"));
        this.items = this.parseItems(props.getProperty("items"), props.getProperty("matchItems"));
        this.mapModels = CustomItemProperties.parseModels(props, this.basePath);
        this.model = CustomItemProperties.parseModel(props.getProperty("model"), path, this.basePath, this.type, this.mapModels);
        this.mapTextures = CustomItemProperties.parseTextures(props, this.basePath);
        boolean flag = this.mapModels == null && this.model == null;
        this.texture = CustomItemProperties.parseTexture(props.getProperty("texture"), props.getProperty("tile"), props.getProperty("source"), path, this.basePath, this.type, this.mapTextures, flag);
        String s = props.getProperty("damage");
        if (s != null) {
            this.damagePercent = s.contains("%");
            s = s.replace("%", "");
            this.damage = this.parseRangeListInt(s);
            this.damageMask = this.parseInt(props.getProperty("damageMask"), 0);
        }
        this.stackSize = this.parseRangeListInt(props.getProperty("stackSize"));
        this.enchantmentIds = this.parseRangeListInt(props.getProperty("enchantmentIDs"), new ParserEnchantmentId());
        this.enchantmentLevels = this.parseRangeListInt(props.getProperty("enchantmentLevels"));
        this.nbtTagValues = this.parseNbtTagValues(props);
        this.hand = this.parseHand(props.getProperty("hand"));
        this.blend = Blender.parseBlend(props.getProperty("blend"));
        this.speed = this.parseFloat(props.getProperty("speed"), 0.0f);
        this.rotation = this.parseFloat(props.getProperty("rotation"), 0.0f);
        this.layer = this.parseInt(props.getProperty("layer"), 0);
        this.weight = this.parseInt(props.getProperty("weight"), 0);
        this.duration = this.parseFloat(props.getProperty("duration"), 1.0f);
    }

    private static String parseName(String path) {
        int j;
        String s = path;
        int i = path.lastIndexOf(47);
        if (i >= 0) {
            s = path.substring(i + 1);
        }
        if ((j = s.lastIndexOf(46)) >= 0) {
            s = s.substring(0, j);
        }
        return s;
    }

    private static String parseBasePath(String path) {
        int i = path.lastIndexOf(47);
        return i < 0 ? "" : path.substring(0, i);
    }

    private int parseType(String str) {
        if (str == null) {
            return 1;
        }
        if (str.equals("item")) {
            return 1;
        }
        if (str.equals("enchantment")) {
            return 2;
        }
        if (str.equals("armor")) {
            return 3;
        }
        Config.warn("Unknown method: " + str);
        return 0;
    }

    private int[] parseItems(String str, String str2) {
        if (str == null) {
            str = str2;
        }
        if (str == null) {
            return null;
        }
        str = str.trim();
        TreeSet<Integer> set = new TreeSet<Integer>();
        String[] astring = Config.tokenize(str, " ");
        for (int i = 0; i < astring.length; ++i) {
            Item item;
            String[] astring1;
            String s = astring[i];
            int j = Config.parseInt(s, -1);
            if (j >= 0) {
                set.add(new Integer(j));
                continue;
            }
            if (s.contains("-") && (astring1 = Config.tokenize(s, "-")).length == 2) {
                int k = Config.parseInt(astring1[0], -1);
                int l = Config.parseInt(astring1[1], -1);
                if (k >= 0 && l >= 0) {
                    int i1 = Math.min(k, l);
                    int j1 = Math.max(k, l);
                    for (int k1 = i1; k1 <= j1; ++k1) {
                        set.add(new Integer(k1));
                    }
                    continue;
                }
            }
            if ((item = Item.getByNameOrId(s)) == null) {
                Config.warn("Item not found: " + s);
                continue;
            }
            int i2 = Item.getIdFromItem(item);
            if (i2 <= 0) {
                Config.warn("Item not found: " + s);
                continue;
            }
            set.add(new Integer(i2));
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = new int[ainteger.length];
        for (int l1 = 0; l1 < aint.length; ++l1) {
            aint[l1] = ainteger[l1];
        }
        return aint;
    }

    private static String parseTexture(String texStr, String texStr2, String texStr3, String path, String basePath, int type, Map<String, String> mapTexs, boolean textureFromPath) {
        int j;
        String s;
        if (texStr == null) {
            texStr = texStr2;
        }
        if (texStr == null) {
            texStr = texStr3;
        }
        if (texStr != null) {
            String s2 = ".png";
            if (texStr.endsWith(s2)) {
                texStr = texStr.substring(0, texStr.length() - s2.length());
            }
            texStr = CustomItemProperties.fixTextureName(texStr, basePath);
            return texStr;
        }
        if (type == 3) {
            return null;
        }
        if (mapTexs != null && (s = mapTexs.get("texture.bow_standby")) != null) {
            return s;
        }
        if (!textureFromPath) {
            return null;
        }
        String s1 = path;
        int i = path.lastIndexOf(47);
        if (i >= 0) {
            s1 = path.substring(i + 1);
        }
        if ((j = s1.lastIndexOf(46)) >= 0) {
            s1 = s1.substring(0, j);
        }
        s1 = CustomItemProperties.fixTextureName(s1, basePath);
        return s1;
    }

    private static Map parseTextures(Properties props, String basePath) {
        String s = "texture.";
        Map map = CustomItemProperties.getMatchingProperties(props, s);
        if (map.size() <= 0) {
            return null;
        }
        Set set = map.keySet();
        LinkedHashMap<String, String> map1 = new LinkedHashMap<String, String>();
        for (Object e : set) {
            String s1 = (String)e;
            String s2 = (String)map.get(s1);
            s2 = CustomItemProperties.fixTextureName(s2, basePath);
            map1.put(s1, s2);
        }
        return map1;
    }

    private static String fixTextureName(String iconName, String basePath) {
        if (!((iconName = TextureUtils.fixResourcePath(iconName, basePath)).startsWith(basePath) || iconName.startsWith("textures/") || iconName.startsWith("mcpatcher/"))) {
            iconName = basePath + "/" + iconName;
        }
        if (iconName.endsWith(".png")) {
            iconName = iconName.substring(0, iconName.length() - 4);
        }
        if (iconName.startsWith("/")) {
            iconName = iconName.substring(1);
        }
        return iconName;
    }

    private static String parseModel(String modelStr, String path, String basePath, int type, Map<String, String> mapModelNames) {
        String s;
        if (modelStr != null) {
            String s1 = ".json";
            if (modelStr.endsWith(s1)) {
                modelStr = modelStr.substring(0, modelStr.length() - s1.length());
            }
            modelStr = CustomItemProperties.fixModelName(modelStr, basePath);
            return modelStr;
        }
        if (type == 3) {
            return null;
        }
        if (mapModelNames != null && (s = mapModelNames.get("model.bow_standby")) != null) {
            return s;
        }
        return modelStr;
    }

    private static Map parseModels(Properties props, String basePath) {
        String s = "model.";
        Map map = CustomItemProperties.getMatchingProperties(props, s);
        if (map.size() <= 0) {
            return null;
        }
        Set set = map.keySet();
        LinkedHashMap<String, String> map1 = new LinkedHashMap<String, String>();
        for (Object e : set) {
            String s1 = (String)e;
            String s2 = (String)map.get(s1);
            s2 = CustomItemProperties.fixModelName(s2, basePath);
            map1.put(s1, s2);
        }
        return map1;
    }

    private static String fixModelName(String modelName, String basePath) {
        String s;
        boolean flag;
        boolean bl = flag = (modelName = TextureUtils.fixResourcePath(modelName, basePath)).startsWith("block/") || modelName.startsWith("item/");
        if (!(modelName.startsWith(basePath) || flag || modelName.startsWith("mcpatcher/"))) {
            modelName = basePath + "/" + modelName;
        }
        if (modelName.endsWith(s = ".json")) {
            modelName = modelName.substring(0, modelName.length() - s.length());
        }
        if (modelName.startsWith("/")) {
            modelName = modelName.substring(1);
        }
        return modelName;
    }

    private int parseInt(String str, int defVal) {
        if (str == null) {
            return defVal;
        }
        int i = Config.parseInt(str = str.trim(), Integer.MIN_VALUE);
        if (i == Integer.MIN_VALUE) {
            Config.warn("Invalid integer: " + str);
            return defVal;
        }
        return i;
    }

    private float parseFloat(String str, float defVal) {
        if (str == null) {
            return defVal;
        }
        float f = Config.parseFloat(str = str.trim(), Float.MIN_VALUE);
        if (f == Float.MIN_VALUE) {
            Config.warn("Invalid float: " + str);
            return defVal;
        }
        return f;
    }

    private RangeListInt parseRangeListInt(String str) {
        return this.parseRangeListInt(str, null);
    }

    private RangeListInt parseRangeListInt(String str, IParserInt parser) {
        if (str == null) {
            return null;
        }
        String[] astring = Config.tokenize(str, " ");
        RangeListInt rangelistint = new RangeListInt();
        for (int i = 0; i < astring.length; ++i) {
            int j;
            String s = astring[i];
            if (parser != null && (j = parser.parse(s, Integer.MIN_VALUE)) != Integer.MIN_VALUE) {
                rangelistint.addRange(new RangeInt(j, j));
                continue;
            }
            RangeInt rangeint = this.parseRangeInt(s);
            if (rangeint == null) {
                Config.warn("Invalid range list: " + str);
                return null;
            }
            rangelistint.addRange(rangeint);
        }
        return rangelistint;
    }

    private RangeInt parseRangeInt(String str) {
        if (str == null) {
            return null;
        }
        int i = (str = str.trim()).length() - str.replace("-", "").length();
        if (i > 1) {
            Config.warn("Invalid range: " + str);
            return null;
        }
        String[] astring = Config.tokenize(str, "- ");
        int[] aint = new int[astring.length];
        for (int j = 0; j < astring.length; ++j) {
            String s = astring[j];
            int k = Config.parseInt(s, -1);
            if (k < 0) {
                Config.warn("Invalid range: " + str);
                return null;
            }
            aint[j] = k;
        }
        if (aint.length == 1) {
            int i1 = aint[0];
            if (str.startsWith("-")) {
                return new RangeInt(0, i1);
            }
            if (str.endsWith("-")) {
                return new RangeInt(i1, 65535);
            }
            return new RangeInt(i1, i1);
        }
        if (aint.length == 2) {
            int l = Math.min(aint[0], aint[1]);
            int j1 = Math.max(aint[0], aint[1]);
            return new RangeInt(l, j1);
        }
        Config.warn("Invalid range: " + str);
        return null;
    }

    private NbtTagValue[] parseNbtTagValues(Properties props) {
        String s = "nbt.";
        Map map = CustomItemProperties.getMatchingProperties(props, s);
        if (map.size() <= 0) {
            return null;
        }
        ArrayList<NbtTagValue> list = new ArrayList<NbtTagValue>();
        for (Object e : map.keySet()) {
            String s1 = (String)e;
            String s2 = (String)map.get(s1);
            String s3 = s1.substring(s.length());
            NbtTagValue nbttagvalue = new NbtTagValue(s3, s2);
            list.add(nbttagvalue);
        }
        NbtTagValue[] anbttagvalue = list.toArray(new NbtTagValue[list.size()]);
        return anbttagvalue;
    }

    private static Map getMatchingProperties(Properties props, String keyPrefix) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        for (Object e : props.keySet()) {
            String s = (String)e;
            String s1 = props.getProperty(s);
            if (!s.startsWith(keyPrefix)) continue;
            map.put(s, s1);
        }
        return map;
    }

    private int parseHand(String str) {
        if (str == null) {
            return 0;
        }
        if ((str = str.toLowerCase()).equals("any")) {
            return 0;
        }
        if (str.equals("main")) {
            return 1;
        }
        if (str.equals("off")) {
            return 2;
        }
        Config.warn("Invalid hand: " + str);
        return 0;
    }

    public boolean isValid(String path) {
        if (this.name != null && this.name.length() > 0) {
            if (this.basePath == null) {
                Config.warn("No base path found: " + path);
                return false;
            }
            if (this.type == 0) {
                Config.warn("No type defined: " + path);
                return false;
            }
            if (this.type == 1 || this.type == 3) {
                if (this.items == null) {
                    this.items = this.detectItems();
                }
                if (this.items == null) {
                    Config.warn("No items defined: " + path);
                    return false;
                }
            }
            if (this.texture == null && this.mapTextures == null && this.model == null && this.mapModels == null) {
                Config.warn("No texture or model specified: " + path);
                return false;
            }
            if (this.type == 2 && this.enchantmentIds == null) {
                Config.warn("No enchantmentIDs specified: " + path);
                return false;
            }
            return true;
        }
        Config.warn("No name found: " + path);
        return false;
    }

    private int[] detectItems() {
        int[] nArray;
        Item item = Item.getByNameOrId(this.name);
        if (item == null) {
            return null;
        }
        int i = Item.getIdFromItem(item);
        if (i <= 0) {
            nArray = null;
        } else {
            int[] nArray2 = new int[1];
            nArray = nArray2;
            nArray2[0] = i;
        }
        return nArray;
    }

    public void updateIcons(TextureMap textureMap) {
        if (this.texture != null) {
            this.textureLocation = this.getTextureLocation(this.texture);
            if (this.type == 1) {
                ResourceLocation resourcelocation = this.getSpriteLocation(this.textureLocation);
                this.sprite = textureMap.registerSprite(resourcelocation);
            }
        }
        if (this.mapTextures != null) {
            this.mapTextureLocations = new HashMap();
            this.mapSprites = new HashMap();
            for (String s : this.mapTextures.keySet()) {
                String s1 = this.mapTextures.get(s);
                ResourceLocation resourcelocation1 = this.getTextureLocation(s1);
                this.mapTextureLocations.put(s, resourcelocation1);
                if (this.type != 1) continue;
                ResourceLocation resourcelocation2 = this.getSpriteLocation(resourcelocation1);
                TextureAtlasSprite textureatlassprite = textureMap.registerSprite(resourcelocation2);
                this.mapSprites.put(s, textureatlassprite);
            }
        }
    }

    private ResourceLocation getTextureLocation(String texName) {
        String s2;
        ResourceLocation resourcelocation1;
        boolean flag;
        if (texName == null) {
            return null;
        }
        ResourceLocation resourcelocation = new ResourceLocation(texName);
        String s = resourcelocation.getResourceDomain();
        String s1 = resourcelocation.getResourcePath();
        if (!s1.contains("/")) {
            s1 = "textures/items/" + s1;
        }
        if (!(flag = Config.hasResource(resourcelocation1 = new ResourceLocation(s, s2 = s1 + ".png")))) {
            Config.warn("File not found: " + s2);
        }
        return resourcelocation1;
    }

    private ResourceLocation getSpriteLocation(ResourceLocation resLoc) {
        String s = resLoc.getResourcePath();
        s = StrUtils.removePrefix(s, "textures/");
        s = StrUtils.removeSuffix(s, ".png");
        ResourceLocation resourcelocation = new ResourceLocation(resLoc.getResourceDomain(), s);
        return resourcelocation;
    }

    public void updateModelTexture(TextureMap textureMap, ItemModelGenerator itemModelGenerator) {
        if (this.texture != null || this.mapTextures != null) {
            String[] astring = this.getModelTextures();
            boolean flag = this.isUseTint();
            this.bakedModelTexture = CustomItemProperties.makeBakedModel(textureMap, itemModelGenerator, astring, flag);
            if (this.type == 1 && this.mapTextures != null) {
                for (String s : this.mapTextures.keySet()) {
                    String s1 = this.mapTextures.get(s);
                    String s2 = StrUtils.removePrefix(s, "texture.");
                    if (!s2.startsWith("bow") && !s2.startsWith("fishing_rod") && !s2.startsWith("shield")) continue;
                    String[] astring1 = new String[]{s1};
                    IBakedModel ibakedmodel = CustomItemProperties.makeBakedModel(textureMap, itemModelGenerator, astring1, flag);
                    if (this.mapBakedModelsTexture == null) {
                        this.mapBakedModelsTexture = new HashMap<String, IBakedModel>();
                    }
                    String s3 = "item/" + s2;
                    this.mapBakedModelsTexture.put(s3, ibakedmodel);
                }
            }
        }
    }

    private boolean isUseTint() {
        return true;
    }

    private static IBakedModel makeBakedModel(TextureMap textureMap, ItemModelGenerator itemModelGenerator, String[] textures, boolean useTint) {
        String[] astring = new String[textures.length];
        for (int i = 0; i < astring.length; ++i) {
            String s = textures[i];
            astring[i] = StrUtils.removePrefix(s, "textures/");
        }
        ModelBlock modelblock = CustomItemProperties.makeModelBlock(astring);
        ModelBlock modelblock1 = itemModelGenerator.makeItemModel(textureMap, modelblock);
        IBakedModel ibakedmodel = CustomItemProperties.bakeModel(textureMap, modelblock1, useTint);
        return ibakedmodel;
    }

    private String[] getModelTextures() {
        if (this.type == 1 && this.items.length == 1) {
            ItemArmor itemarmor;
            Item item = Item.getItemById(this.items[0]);
            if (item == Items.potionitem && this.damage != null && this.damage.getCountRanges() > 0) {
                RangeInt rangeint = this.damage.getRange(0);
                int i = rangeint.getMin();
                boolean flag = (i & 0x4000) != 0;
                String s5 = this.getMapTexture(this.mapTextures, "texture.potion_overlay", "items/potion_overlay");
                String s6 = null;
                s6 = flag ? this.getMapTexture(this.mapTextures, "texture.potion_bottle_splash", "items/potion_bottle_splash") : this.getMapTexture(this.mapTextures, "texture.potion_bottle_drinkable", "items/potion_bottle_drinkable");
                return new String[]{s5, s6};
            }
            if (item instanceof ItemArmor && (itemarmor = (ItemArmor)item).getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                String s = "leather";
                String s1 = "helmet";
                if (itemarmor.armorType == 0) {
                    s1 = "helmet";
                }
                if (itemarmor.armorType == 1) {
                    s1 = "chestplate";
                }
                if (itemarmor.armorType == 2) {
                    s1 = "leggings";
                }
                if (itemarmor.armorType == 3) {
                    s1 = "boots";
                }
                String s2 = s + "_" + s1;
                String s3 = this.getMapTexture(this.mapTextures, "texture." + s2, "items/" + s2);
                String s4 = this.getMapTexture(this.mapTextures, "texture." + s2 + "_overlay", "items/" + s2 + "_overlay");
                return new String[]{s3, s4};
            }
        }
        return new String[]{this.texture};
    }

    private String getMapTexture(Map<String, String> map, String key, String def) {
        if (map == null) {
            return def;
        }
        String s = map.get(key);
        return s == null ? def : s;
    }

    private static ModelBlock makeModelBlock(String[] modelTextures) {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("{\"parent\": \"builtin/generated\",\"textures\": {");
        for (int i = 0; i < modelTextures.length; ++i) {
            String s = modelTextures[i];
            if (i > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append("\"layer" + i + "\": \"" + s + "\"");
        }
        stringbuffer.append("}}");
        String s1 = stringbuffer.toString();
        ModelBlock modelblock = ModelBlock.deserialize(s1);
        return modelblock;
    }

    private static IBakedModel bakeModel(TextureMap textureMap, ModelBlock modelBlockIn, boolean useTint) {
        ModelRotation modelrotation = ModelRotation.X0_Y0;
        boolean flag = false;
        String s = modelBlockIn.resolveTextureName("particle");
        TextureAtlasSprite textureatlassprite = textureMap.getAtlasSprite(new ResourceLocation(s).toString());
        SimpleBakedModel.Builder simplebakedmodel$builder = new SimpleBakedModel.Builder(modelBlockIn).setTexture(textureatlassprite);
        for (BlockPart blockpart : modelBlockIn.getElements()) {
            for (EnumFacing enumfacing : blockpart.mapFaces.keySet()) {
                BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);
                if (!useTint) {
                    blockpartface = new BlockPartFace(blockpartface.cullFace, -1, blockpartface.texture, blockpartface.blockFaceUV);
                }
                String s1 = modelBlockIn.resolveTextureName(blockpartface.texture);
                TextureAtlasSprite textureatlassprite1 = textureMap.getAtlasSprite(new ResourceLocation(s1).toString());
                BakedQuad bakedquad = CustomItemProperties.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing, modelrotation, flag);
                if (blockpartface.cullFace == null) {
                    simplebakedmodel$builder.addGeneralQuad(bakedquad);
                    continue;
                }
                simplebakedmodel$builder.addFaceQuad(modelrotation.rotateFace(blockpartface.cullFace), bakedquad);
            }
        }
        return simplebakedmodel$builder.makeBakedModel();
    }

    private static BakedQuad makeBakedQuad(BlockPart blockPart, BlockPartFace blockPartFace, TextureAtlasSprite textureAtlasSprite, EnumFacing enumFacing, ModelRotation modelRotation, boolean uvLocked) {
        FaceBakery facebakery = new FaceBakery();
        return facebakery.makeBakedQuad(blockPart.positionFrom, blockPart.positionTo, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, blockPart.partRotation, uvLocked, blockPart.shade);
    }

    public String toString() {
        return "" + this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
    }

    public float getTextureWidth(TextureManager textureManager) {
        if (this.textureWidth <= 0) {
            if (this.textureLocation != null) {
                ITextureObject itextureobject = textureManager.getTexture(this.textureLocation);
                int i = itextureobject.getGlTextureId();
                int j = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(i);
                this.textureWidth = GL11.glGetTexLevelParameteri((int)3553, (int)0, (int)4096);
                GlStateManager.bindTexture(j);
            }
            if (this.textureWidth <= 0) {
                this.textureWidth = 16;
            }
        }
        return this.textureWidth;
    }

    public float getTextureHeight(TextureManager textureManager) {
        if (this.textureHeight <= 0) {
            if (this.textureLocation != null) {
                ITextureObject itextureobject = textureManager.getTexture(this.textureLocation);
                int i = itextureobject.getGlTextureId();
                int j = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(i);
                this.textureHeight = GL11.glGetTexLevelParameteri((int)3553, (int)0, (int)4097);
                GlStateManager.bindTexture(j);
            }
            if (this.textureHeight <= 0) {
                this.textureHeight = 16;
            }
        }
        return this.textureHeight;
    }

    public IBakedModel getBakedModel(ResourceLocation modelLocation, boolean fullModel) {
        String s;
        IBakedModel ibakedmodel1;
        Map<String, IBakedModel> map;
        IBakedModel ibakedmodel;
        if (fullModel) {
            ibakedmodel = this.bakedModelFull;
            map = this.mapBakedModelsFull;
        } else {
            ibakedmodel = this.bakedModelTexture;
            map = this.mapBakedModelsTexture;
        }
        if (modelLocation != null && map != null && (ibakedmodel1 = map.get(s = modelLocation.getResourcePath())) != null) {
            return ibakedmodel1;
        }
        return ibakedmodel;
    }

    public void loadModels(ModelBakery modelBakery) {
        if (this.model != null) {
            CustomItemProperties.loadItemModel(modelBakery, this.model);
        }
        if (this.type == 1 && this.mapModels != null) {
            for (String s : this.mapModels.keySet()) {
                String s1 = this.mapModels.get(s);
                String s2 = StrUtils.removePrefix(s, "model.");
                if (!s2.startsWith("bow") && !s2.startsWith("fishing_rod") && !s2.startsWith("shield")) continue;
                CustomItemProperties.loadItemModel(modelBakery, s1);
            }
        }
    }

    public void updateModelsFull() {
        ModelManager modelmanager = Config.getModelManager();
        IBakedModel ibakedmodel = modelmanager.getMissingModel();
        if (this.model != null) {
            ResourceLocation resourcelocation = CustomItemProperties.getModelLocation(this.model);
            ModelResourceLocation modelresourcelocation = new ModelResourceLocation(resourcelocation, INVENTORY);
            this.bakedModelFull = modelmanager.getModel(modelresourcelocation);
            if (this.bakedModelFull == ibakedmodel) {
                Config.warn("Custom Items: Model not found " + modelresourcelocation.getResourcePath());
                this.bakedModelFull = null;
            }
        }
        if (this.type == 1 && this.mapModels != null) {
            for (String s : this.mapModels.keySet()) {
                String s1 = this.mapModels.get(s);
                String s2 = StrUtils.removePrefix(s, "model.");
                if (!s2.startsWith("bow") && !s2.startsWith("fishing_rod") && !s2.startsWith("shield")) continue;
                ResourceLocation resourcelocation1 = CustomItemProperties.getModelLocation(s1);
                ModelResourceLocation modelresourcelocation1 = new ModelResourceLocation(resourcelocation1, INVENTORY);
                IBakedModel ibakedmodel1 = modelmanager.getModel(modelresourcelocation1);
                if (ibakedmodel1 == ibakedmodel) {
                    Config.warn("Custom Items: Model not found " + modelresourcelocation1.getResourcePath());
                    continue;
                }
                if (this.mapBakedModelsFull == null) {
                    this.mapBakedModelsFull = new HashMap<String, IBakedModel>();
                }
                String s3 = "item/" + s2;
                this.mapBakedModelsFull.put(s3, ibakedmodel1);
            }
        }
    }

    private static void loadItemModel(ModelBakery modelBakery, String model) {
        ResourceLocation resourcelocation = CustomItemProperties.getModelLocation(model);
        ModelResourceLocation modelresourcelocation = new ModelResourceLocation(resourcelocation, INVENTORY);
        if (Reflector.ModelLoader.exists()) {
            try {
                Object object = Reflector.ModelLoader_VanillaLoader_INSTANCE.getValue();
                CustomItemProperties.checkNull(object, "vanillaLoader is null");
                Object object1 = Reflector.call(object, Reflector.ModelLoader_VanillaLoader_loadModel, modelresourcelocation);
                CustomItemProperties.checkNull(object1, "iModel is null");
                Map map = (Map)Reflector.getFieldValue(modelBakery, Reflector.ModelLoader_stateModels);
                CustomItemProperties.checkNull(map, "stateModels is null");
                map.put(modelresourcelocation, object1);
                Set set = (Set)Reflector.getFieldValue(modelBakery, Reflector.ModelLoader_textures);
                CustomItemProperties.checkNull(set, "registryTextures is null");
                Collection collection = (Collection)Reflector.call(object1, Reflector.IModel_getTextures, new Object[0]);
                CustomItemProperties.checkNull(collection, "modelTextures is null");
                set.addAll(collection);
            }
            catch (Exception exception) {
                Config.warn("Error registering model with ModelLoader: " + modelresourcelocation + ", " + exception.getClass().getName() + ": " + exception.getMessage());
            }
        } else {
            modelBakery.loadItemModel(resourcelocation.toString(), modelresourcelocation, resourcelocation);
        }
    }

    private static void checkNull(Object obj, String msg) throws NullPointerException {
        if (obj == null) {
            throw new NullPointerException(msg);
        }
    }

    private static ResourceLocation getModelLocation(String modelName) {
        return Reflector.ModelLoader.exists() && !modelName.startsWith("mcpatcher/") && !modelName.startsWith("optifine/") ? new ResourceLocation("models/" + modelName) : new ResourceLocation(modelName);
    }
}

