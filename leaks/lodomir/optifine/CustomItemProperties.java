/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package optifine;

import java.util.ArrayList;
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
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import optifine.Blender;
import optifine.Config;
import optifine.NbtTagValue;
import optifine.RangeInt;
import optifine.RangeListInt;
import optifine.StrUtils;
import optifine.TextureUtils;
import org.lwjgl.opengl.GL11;

public class CustomItemProperties {
    public String name = null;
    public String basePath = null;
    public int type = 1;
    public int[] items = null;
    public String texture = null;
    public Map<String, String> mapTextures = null;
    public RangeListInt damage = null;
    public boolean damagePercent = false;
    public int damageMask = 0;
    public RangeListInt stackSize = null;
    public RangeListInt enchantmentIds = null;
    public RangeListInt enchantmentLevels = null;
    public NbtTagValue[] nbtTagValues = null;
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
    public IBakedModel model = null;
    public Map<String, IBakedModel> mapModels = null;
    private int textureWidth = 0;
    private int textureHeight = 0;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_ENCHANTMENT = 2;
    public static final int TYPE_ARMOR = 3;

    public CustomItemProperties(Properties p_i34_1_, String p_i34_2_) {
        this.name = CustomItemProperties.parseName(p_i34_2_);
        this.basePath = CustomItemProperties.parseBasePath(p_i34_2_);
        this.type = this.parseType(p_i34_1_.getProperty("type"));
        this.items = this.parseItems(p_i34_1_.getProperty("items"), p_i34_1_.getProperty("matchItems"));
        this.mapTextures = CustomItemProperties.parseTextures(p_i34_1_, this.basePath);
        this.texture = CustomItemProperties.parseTexture(p_i34_1_.getProperty("texture"), p_i34_1_.getProperty("tile"), p_i34_1_.getProperty("source"), p_i34_2_, this.basePath, this.type, this.mapTextures);
        String s = p_i34_1_.getProperty("damage");
        if (s != null) {
            this.damagePercent = s.contains("%");
            s.replace("%", "");
            this.damage = this.parseRangeListInt(s);
            this.damageMask = this.parseInt(p_i34_1_.getProperty("damageMask"), 0);
        }
        this.stackSize = this.parseRangeListInt(p_i34_1_.getProperty("stackSize"));
        this.enchantmentIds = this.parseRangeListInt(p_i34_1_.getProperty("enchantmentIDs"));
        this.enchantmentLevels = this.parseRangeListInt(p_i34_1_.getProperty("enchantmentLevels"));
        this.nbtTagValues = this.parseNbtTagValues(p_i34_1_);
        this.blend = Blender.parseBlend(p_i34_1_.getProperty("blend"));
        this.speed = this.parseFloat(p_i34_1_.getProperty("speed"), 0.0f);
        this.rotation = this.parseFloat(p_i34_1_.getProperty("rotation"), 0.0f);
        this.layer = this.parseInt(p_i34_1_.getProperty("layer"), 0);
        this.weight = this.parseInt(p_i34_1_.getProperty("weight"), 0);
        this.duration = this.parseFloat(p_i34_1_.getProperty("duration"), 1.0f);
    }

    private static String parseName(String p_parseName_0_) {
        int j;
        String s = p_parseName_0_;
        int i = p_parseName_0_.lastIndexOf(47);
        if (i >= 0) {
            s = p_parseName_0_.substring(i + 1);
        }
        if ((j = s.lastIndexOf(46)) >= 0) {
            s = s.substring(0, j);
        }
        return s;
    }

    private static String parseBasePath(String p_parseBasePath_0_) {
        int i = p_parseBasePath_0_.lastIndexOf(47);
        return i < 0 ? "" : p_parseBasePath_0_.substring(0, i);
    }

    private int parseType(String p_parseType_1_) {
        if (p_parseType_1_ == null) {
            return 1;
        }
        if (p_parseType_1_.equals("item")) {
            return 1;
        }
        if (p_parseType_1_.equals("enchantment")) {
            return 2;
        }
        if (p_parseType_1_.equals("armor")) {
            return 3;
        }
        Config.warn("Unknown method: " + p_parseType_1_);
        return 0;
    }

    private int[] parseItems(String p_parseItems_1_, String p_parseItems_2_) {
        if (p_parseItems_1_ == null) {
            p_parseItems_1_ = p_parseItems_2_;
        }
        if (p_parseItems_1_ == null) {
            return null;
        }
        p_parseItems_1_ = p_parseItems_1_.trim();
        TreeSet<Integer> set = new TreeSet<Integer>();
        String[] astring = Config.tokenize(p_parseItems_1_, " ");
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
            if (i2 < 0) {
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

    private static String parseTexture(String p_parseTexture_0_, String p_parseTexture_1_, String p_parseTexture_2_, String p_parseTexture_3_, String p_parseTexture_4_, int p_parseTexture_5_, Map<String, String> p_parseTexture_6_) {
        int j;
        String s;
        if (p_parseTexture_0_ == null) {
            p_parseTexture_0_ = p_parseTexture_1_;
        }
        if (p_parseTexture_0_ == null) {
            p_parseTexture_0_ = p_parseTexture_2_;
        }
        if (p_parseTexture_0_ != null) {
            String s2 = ".png";
            if (p_parseTexture_0_.endsWith(s2)) {
                p_parseTexture_0_ = p_parseTexture_0_.substring(0, p_parseTexture_0_.length() - s2.length());
            }
            p_parseTexture_0_ = CustomItemProperties.fixTextureName(p_parseTexture_0_, p_parseTexture_4_);
            return p_parseTexture_0_;
        }
        if (p_parseTexture_5_ == 3) {
            return null;
        }
        if (p_parseTexture_6_ != null && (s = p_parseTexture_6_.get("texture.bow_standby")) != null) {
            return s;
        }
        String s1 = p_parseTexture_3_;
        int i = p_parseTexture_3_.lastIndexOf(47);
        if (i >= 0) {
            s1 = p_parseTexture_3_.substring(i + 1);
        }
        if ((j = s1.lastIndexOf(46)) >= 0) {
            s1 = s1.substring(0, j);
        }
        s1 = CustomItemProperties.fixTextureName(s1, p_parseTexture_4_);
        return s1;
    }

    private static Map parseTextures(Properties p_parseTextures_0_, String p_parseTextures_1_) {
        String s = "texture.";
        Map map = CustomItemProperties.getMatchingProperties(p_parseTextures_0_, s);
        if (map.size() <= 0) {
            return null;
        }
        Set set = map.keySet();
        LinkedHashMap map1 = new LinkedHashMap();
        for (Object s1 : set) {
            String s2 = (String)map.get(s1);
            s2 = CustomItemProperties.fixTextureName(s2, p_parseTextures_1_);
            map1.put(s1, s2);
        }
        return map1;
    }

    private static String fixTextureName(String p_fixTextureName_0_, String p_fixTextureName_1_) {
        String s;
        if (!((p_fixTextureName_0_ = TextureUtils.fixResourcePath(p_fixTextureName_0_, p_fixTextureName_1_)).startsWith(p_fixTextureName_1_) || p_fixTextureName_0_.startsWith("textures/") || p_fixTextureName_0_.startsWith("mcpatcher/"))) {
            p_fixTextureName_0_ = p_fixTextureName_1_ + "/" + p_fixTextureName_0_;
        }
        if (p_fixTextureName_0_.endsWith(".png")) {
            p_fixTextureName_0_ = p_fixTextureName_0_.substring(0, p_fixTextureName_0_.length() - 4);
        }
        if (p_fixTextureName_0_.startsWith(s = "textures/blocks/")) {
            p_fixTextureName_0_ = p_fixTextureName_0_.substring(s.length());
        }
        if (p_fixTextureName_0_.startsWith("/")) {
            p_fixTextureName_0_ = p_fixTextureName_0_.substring(1);
        }
        return p_fixTextureName_0_;
    }

    private int parseInt(String p_parseInt_1_, int p_parseInt_2_) {
        if (p_parseInt_1_ == null) {
            return p_parseInt_2_;
        }
        int i = Config.parseInt(p_parseInt_1_ = p_parseInt_1_.trim(), Integer.MIN_VALUE);
        if (i == Integer.MIN_VALUE) {
            Config.warn("Invalid integer: " + p_parseInt_1_);
            return p_parseInt_2_;
        }
        return i;
    }

    private float parseFloat(String p_parseFloat_1_, float p_parseFloat_2_) {
        if (p_parseFloat_1_ == null) {
            return p_parseFloat_2_;
        }
        float f = Config.parseFloat(p_parseFloat_1_ = p_parseFloat_1_.trim(), Float.MIN_VALUE);
        if (f == Float.MIN_VALUE) {
            Config.warn("Invalid float: " + p_parseFloat_1_);
            return p_parseFloat_2_;
        }
        return f;
    }

    private RangeListInt parseRangeListInt(String p_parseRangeListInt_1_) {
        if (p_parseRangeListInt_1_ == null) {
            return null;
        }
        String[] astring = Config.tokenize(p_parseRangeListInt_1_, " ");
        RangeListInt rangelistint = new RangeListInt();
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            RangeInt rangeint = this.parseRangeInt(s);
            if (rangeint == null) {
                Config.warn("Invalid range list: " + p_parseRangeListInt_1_);
                return null;
            }
            rangelistint.addRange(rangeint);
        }
        return rangelistint;
    }

    private RangeInt parseRangeInt(String p_parseRangeInt_1_) {
        if (p_parseRangeInt_1_ == null) {
            return null;
        }
        int i = (p_parseRangeInt_1_ = p_parseRangeInt_1_.trim()).length() - p_parseRangeInt_1_.replace("-", "").length();
        if (i > 1) {
            Config.warn("Invalid range: " + p_parseRangeInt_1_);
            return null;
        }
        String[] astring = Config.tokenize(p_parseRangeInt_1_, "- ");
        int[] aint = new int[astring.length];
        for (int j = 0; j < astring.length; ++j) {
            String s = astring[j];
            int k = Config.parseInt(s, -1);
            if (k < 0) {
                Config.warn("Invalid range: " + p_parseRangeInt_1_);
                return null;
            }
            aint[j] = k;
        }
        if (aint.length == 1) {
            int i1 = aint[0];
            if (p_parseRangeInt_1_.startsWith("-")) {
                return new RangeInt(0, i1);
            }
            if (p_parseRangeInt_1_.endsWith("-")) {
                return new RangeInt(i1, 255);
            }
            return new RangeInt(i1, i1);
        }
        if (aint.length == 2) {
            int l = Math.min(aint[0], aint[1]);
            int j1 = Math.max(aint[0], aint[1]);
            return new RangeInt(l, j1);
        }
        Config.warn("Invalid range: " + p_parseRangeInt_1_);
        return null;
    }

    private NbtTagValue[] parseNbtTagValues(Properties p_parseNbtTagValues_1_) {
        String s = "nbt.";
        Map map = CustomItemProperties.getMatchingProperties(p_parseNbtTagValues_1_, s);
        if (map.size() <= 0) {
            return null;
        }
        ArrayList<NbtTagValue> list = new ArrayList<NbtTagValue>();
        for (Object s1 : map.keySet()) {
            String s2 = (String)map.get(s1);
            String s3 = ((String)s1).substring(s.length());
            NbtTagValue nbttagvalue = new NbtTagValue(s3, s2);
            list.add(nbttagvalue);
        }
        NbtTagValue[] anbttagvalue = list.toArray(new NbtTagValue[list.size()]);
        return anbttagvalue;
    }

    private static Map getMatchingProperties(Properties p_getMatchingProperties_0_, String p_getMatchingProperties_1_) {
        LinkedHashMap<Object, String> map = new LinkedHashMap<Object, String>();
        for (Object s : p_getMatchingProperties_0_.keySet()) {
            String s1 = p_getMatchingProperties_0_.getProperty((String)s);
            if (!((String)s).startsWith(p_getMatchingProperties_1_)) continue;
            map.put(s, s1);
        }
        return map;
    }

    public boolean isValid(String p_isValid_1_) {
        if (this.name != null && this.name.length() > 0) {
            if (this.basePath == null) {
                Config.warn("No base path found: " + p_isValid_1_);
                return false;
            }
            if (this.type == 0) {
                Config.warn("No type defined: " + p_isValid_1_);
                return false;
            }
            if ((this.type == 1 || this.type == 3) && this.items == null) {
                Config.warn("No items defined: " + p_isValid_1_);
                return false;
            }
            if (this.texture == null && this.mapTextures == null) {
                Config.warn("No texture specified: " + p_isValid_1_);
                return false;
            }
            if (this.type == 2 && this.enchantmentIds == null) {
                Config.warn("No enchantmentIDs specified: " + p_isValid_1_);
                return false;
            }
            return true;
        }
        Config.warn("No name found: " + p_isValid_1_);
        return false;
    }

    public void updateIcons(TextureMap p_updateIcons_1_) {
        if (this.texture != null) {
            this.textureLocation = this.getTextureLocation(this.texture);
            if (this.type == 1) {
                ResourceLocation resourcelocation = this.getSpriteLocation(this.textureLocation);
                this.sprite = p_updateIcons_1_.registerSprite(resourcelocation);
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
                TextureAtlasSprite textureatlassprite = p_updateIcons_1_.registerSprite(resourcelocation2);
                this.mapSprites.put(s, textureatlassprite);
            }
        }
    }

    private ResourceLocation getTextureLocation(String p_getTextureLocation_1_) {
        String s2;
        ResourceLocation resourcelocation1;
        boolean flag;
        if (p_getTextureLocation_1_ == null) {
            return null;
        }
        ResourceLocation resourcelocation = new ResourceLocation(p_getTextureLocation_1_);
        String s = resourcelocation.getResourceDomain();
        String s1 = resourcelocation.getResourcePath();
        if (!s1.contains("/")) {
            s1 = "textures/blocks/" + s1;
        }
        if (!(flag = Config.hasResource(resourcelocation1 = new ResourceLocation(s, s2 = s1 + ".png")))) {
            Config.warn("File not found: " + s2);
        }
        return resourcelocation1;
    }

    private ResourceLocation getSpriteLocation(ResourceLocation p_getSpriteLocation_1_) {
        String s = p_getSpriteLocation_1_.getResourcePath();
        s = StrUtils.removePrefix(s, "textures/");
        s = StrUtils.removeSuffix(s, ".png");
        ResourceLocation resourcelocation = new ResourceLocation(p_getSpriteLocation_1_.getResourceDomain(), s);
        return resourcelocation;
    }

    public void updateModel(TextureMap p_updateModel_1_, ItemModelGenerator p_updateModel_2_) {
        String[] astring = this.getModelTextures();
        boolean flag = this.isUseTint();
        this.model = CustomItemProperties.makeBakedModel(p_updateModel_1_, p_updateModel_2_, astring, flag);
        if (this.type == 1 && this.mapTextures != null) {
            for (String s : this.mapTextures.keySet()) {
                String s1 = this.mapTextures.get(s);
                String s2 = StrUtils.removePrefix(s, "texture.");
                if (!s2.startsWith("bow") && !s2.startsWith("fishing_rod")) continue;
                String[] astring1 = new String[]{s1};
                IBakedModel ibakedmodel = CustomItemProperties.makeBakedModel(p_updateModel_1_, p_updateModel_2_, astring1, flag);
                if (this.mapModels == null) {
                    this.mapModels = new HashMap<String, IBakedModel>();
                }
                this.mapModels.put(s2, ibakedmodel);
            }
        }
    }

    private boolean isUseTint() {
        return true;
    }

    private static IBakedModel makeBakedModel(TextureMap p_makeBakedModel_0_, ItemModelGenerator p_makeBakedModel_1_, String[] p_makeBakedModel_2_, boolean p_makeBakedModel_3_) {
        ModelBlock modelblock = CustomItemProperties.makeModelBlock(p_makeBakedModel_2_);
        ModelBlock modelblock1 = p_makeBakedModel_1_.makeItemModel(p_makeBakedModel_0_, modelblock);
        IBakedModel ibakedmodel = CustomItemProperties.bakeModel(p_makeBakedModel_0_, modelblock1, p_makeBakedModel_3_);
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

    private String getMapTexture(Map<String, String> p_getMapTexture_1_, String p_getMapTexture_2_, String p_getMapTexture_3_) {
        if (p_getMapTexture_1_ == null) {
            return p_getMapTexture_3_;
        }
        String s = p_getMapTexture_1_.get(p_getMapTexture_2_);
        return s == null ? p_getMapTexture_3_ : s;
    }

    private static ModelBlock makeModelBlock(String[] p_makeModelBlock_0_) {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("{\"parent\": \"builtin/generated\",\"textures\": {");
        for (int i = 0; i < p_makeModelBlock_0_.length; ++i) {
            String s = p_makeModelBlock_0_[i];
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

    private static IBakedModel bakeModel(TextureMap p_bakeModel_0_, ModelBlock p_bakeModel_1_, boolean p_bakeModel_2_) {
        ModelRotation modelrotation = ModelRotation.X0_Y0;
        boolean flag = false;
        TextureAtlasSprite textureatlassprite = p_bakeModel_0_.getSpriteSafe(p_bakeModel_1_.resolveTextureName("particle"));
        SimpleBakedModel.Builder simplebakedmodel$builder = new SimpleBakedModel.Builder(p_bakeModel_1_).setTexture(textureatlassprite);
        for (BlockPart blockpart : p_bakeModel_1_.getElements()) {
            for (EnumFacing enumfacing : blockpart.mapFaces.keySet()) {
                BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);
                if (!p_bakeModel_2_) {
                    blockpartface = new BlockPartFace(blockpartface.cullFace, -1, blockpartface.texture, blockpartface.blockFaceUV);
                }
                TextureAtlasSprite textureatlassprite1 = p_bakeModel_0_.getSpriteSafe(p_bakeModel_1_.resolveTextureName(blockpartface.texture));
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

    private static BakedQuad makeBakedQuad(BlockPart p_makeBakedQuad_0_, BlockPartFace p_makeBakedQuad_1_, TextureAtlasSprite p_makeBakedQuad_2_, EnumFacing p_makeBakedQuad_3_, ModelRotation p_makeBakedQuad_4_, boolean p_makeBakedQuad_5_) {
        FaceBakery facebakery = new FaceBakery();
        return facebakery.makeBakedQuad(p_makeBakedQuad_0_.positionFrom, p_makeBakedQuad_0_.positionTo, p_makeBakedQuad_1_, p_makeBakedQuad_2_, p_makeBakedQuad_3_, p_makeBakedQuad_4_, p_makeBakedQuad_0_.partRotation, p_makeBakedQuad_5_, p_makeBakedQuad_0_.shade);
    }

    public String toString() {
        return "" + this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
    }

    public float getTextureWidth(TextureManager p_getTextureWidth_1_) {
        if (this.textureWidth <= 0) {
            if (this.textureLocation != null) {
                ITextureObject itextureobject = p_getTextureWidth_1_.getTexture(this.textureLocation);
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

    public float getTextureHeight(TextureManager p_getTextureHeight_1_) {
        if (this.textureHeight <= 0) {
            if (this.textureLocation != null) {
                ITextureObject itextureobject = p_getTextureHeight_1_.getTexture(this.textureLocation);
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

    public IBakedModel getModel(ModelResourceLocation p_getModel_1_) {
        if (p_getModel_1_ != null && this.mapTextures != null) {
            IBakedModel ibakedmodel;
            String s = p_getModel_1_.getResourcePath();
            if (this.mapModels != null && (ibakedmodel = this.mapModels.get(s)) != null) {
                return ibakedmodel;
            }
        }
        return this.model;
    }
}

