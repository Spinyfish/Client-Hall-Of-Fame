/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonParser
 */
package optifine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.Json;
import optifine.ModelPlayerItem;
import optifine.PlayerItemModel;
import optifine.PlayerItemRenderer;

public class PlayerItemParser {
    private static JsonParser jsonParser = new JsonParser();
    public static final String ITEM_TYPE = "type";
    public static final String ITEM_TEXTURE_SIZE = "textureSize";
    public static final String ITEM_USE_PLAYER_TEXTURE = "usePlayerTexture";
    public static final String ITEM_MODELS = "models";
    public static final String MODEL_ID = "id";
    public static final String MODEL_BASE_ID = "baseId";
    public static final String MODEL_TYPE = "type";
    public static final String MODEL_ATTACH_TO = "attachTo";
    public static final String MODEL_INVERT_AXIS = "invertAxis";
    public static final String MODEL_MIRROR_TEXTURE = "mirrorTexture";
    public static final String MODEL_TRANSLATE = "translate";
    public static final String MODEL_ROTATE = "rotate";
    public static final String MODEL_SCALE = "scale";
    public static final String MODEL_BOXES = "boxes";
    public static final String MODEL_SPRITES = "sprites";
    public static final String MODEL_SUBMODEL = "submodel";
    public static final String MODEL_SUBMODELS = "submodels";
    public static final String BOX_TEXTURE_OFFSET = "textureOffset";
    public static final String BOX_COORDINATES = "coordinates";
    public static final String BOX_SIZE_ADD = "sizeAdd";
    public static final String ITEM_TYPE_MODEL = "PlayerItem";
    public static final String MODEL_TYPE_BOX = "ModelBox";

    public static PlayerItemModel parseItemModel(JsonObject p_parseItemModel_0_) {
        String s = Json.getString(p_parseItemModel_0_, "type");
        if (!Config.equals(s, ITEM_TYPE_MODEL)) {
            throw new JsonParseException("Unknown model type: " + s);
        }
        int[] aint = Json.parseIntArray(p_parseItemModel_0_.get(ITEM_TEXTURE_SIZE), 2);
        PlayerItemParser.checkNull(aint, "Missing texture size");
        Dimension dimension = new Dimension(aint[0], aint[1]);
        boolean flag = Json.getBoolean(p_parseItemModel_0_, ITEM_USE_PLAYER_TEXTURE, false);
        JsonArray jsonarray = (JsonArray)p_parseItemModel_0_.get(ITEM_MODELS);
        PlayerItemParser.checkNull(jsonarray, "Missing elements");
        HashMap<String, JsonObject> map = new HashMap<String, JsonObject>();
        ArrayList<PlayerItemRenderer> list = new ArrayList<PlayerItemRenderer>();
        new ArrayList();
        for (int i = 0; i < jsonarray.size(); ++i) {
            PlayerItemRenderer playeritemrenderer;
            String s2;
            JsonObject jsonobject = (JsonObject)jsonarray.get(i);
            String s1 = Json.getString(jsonobject, MODEL_BASE_ID);
            if (s1 != null) {
                JsonObject jsonobject1 = (JsonObject)map.get(s1);
                if (jsonobject1 == null) {
                    Config.warn("BaseID not found: " + s1);
                    continue;
                }
                for (Map.Entry entry : jsonobject1.entrySet()) {
                    if (jsonobject.has((String)entry.getKey())) continue;
                    jsonobject.add((String)entry.getKey(), (JsonElement)entry.getValue());
                }
            }
            if ((s2 = Json.getString(jsonobject, MODEL_ID)) != null) {
                if (!map.containsKey(s2)) {
                    map.put(s2, jsonobject);
                } else {
                    Config.warn("Duplicate model ID: " + s2);
                }
            }
            if ((playeritemrenderer = PlayerItemParser.parseItemRenderer(jsonobject, dimension)) == null) continue;
            list.add(playeritemrenderer);
        }
        PlayerItemRenderer[] aplayeritemrenderer = list.toArray(new PlayerItemRenderer[list.size()]);
        return new PlayerItemModel(dimension, flag, aplayeritemrenderer);
    }

    private static void checkNull(Object p_checkNull_0_, String p_checkNull_1_) {
        if (p_checkNull_0_ == null) {
            throw new JsonParseException(p_checkNull_1_);
        }
    }

    private static ResourceLocation makeResourceLocation(String p_makeResourceLocation_0_) {
        int i = p_makeResourceLocation_0_.indexOf(58);
        if (i < 0) {
            return new ResourceLocation(p_makeResourceLocation_0_);
        }
        String s = p_makeResourceLocation_0_.substring(0, i);
        String s1 = p_makeResourceLocation_0_.substring(i + 1);
        return new ResourceLocation(s, s1);
    }

    private static int parseAttachModel(String p_parseAttachModel_0_) {
        if (p_parseAttachModel_0_ == null) {
            return 0;
        }
        if (p_parseAttachModel_0_.equals("body")) {
            return 0;
        }
        if (p_parseAttachModel_0_.equals("head")) {
            return 1;
        }
        if (p_parseAttachModel_0_.equals("leftArm")) {
            return 2;
        }
        if (p_parseAttachModel_0_.equals("rightArm")) {
            return 3;
        }
        if (p_parseAttachModel_0_.equals("leftLeg")) {
            return 4;
        }
        if (p_parseAttachModel_0_.equals("rightLeg")) {
            return 5;
        }
        if (p_parseAttachModel_0_.equals("cape")) {
            return 6;
        }
        Config.warn("Unknown attachModel: " + p_parseAttachModel_0_);
        return 0;
    }

    private static PlayerItemRenderer parseItemRenderer(JsonObject p_parseItemRenderer_0_, Dimension p_parseItemRenderer_1_) {
        String s = Json.getString(p_parseItemRenderer_0_, "type");
        if (!Config.equals(s, MODEL_TYPE_BOX)) {
            Config.warn("Unknown model type: " + s);
            return null;
        }
        String s1 = Json.getString(p_parseItemRenderer_0_, MODEL_ATTACH_TO);
        int i = PlayerItemParser.parseAttachModel(s1);
        float f = Json.getFloat(p_parseItemRenderer_0_, MODEL_SCALE, 1.0f);
        ModelPlayerItem modelbase = new ModelPlayerItem();
        modelbase.textureWidth = p_parseItemRenderer_1_.width;
        modelbase.textureHeight = p_parseItemRenderer_1_.height;
        ModelRenderer modelrenderer = PlayerItemParser.parseModelRenderer(p_parseItemRenderer_0_, modelbase);
        PlayerItemRenderer playeritemrenderer = new PlayerItemRenderer(i, f, modelrenderer);
        return playeritemrenderer;
    }

    private static ModelRenderer parseModelRenderer(JsonObject p_parseModelRenderer_0_, ModelBase p_parseModelRenderer_1_) {
        JsonArray jsonarray2;
        JsonObject jsonobject1;
        JsonArray jsonarray1;
        JsonArray jsonarray;
        ModelRenderer modelrenderer = new ModelRenderer(p_parseModelRenderer_1_);
        String s = Json.getString(p_parseModelRenderer_0_, MODEL_INVERT_AXIS, "").toLowerCase();
        boolean flag = s.contains("x");
        boolean flag1 = s.contains("y");
        boolean flag2 = s.contains("z");
        float[] afloat = Json.parseFloatArray(p_parseModelRenderer_0_.get(MODEL_TRANSLATE), 3, new float[3]);
        if (flag) {
            afloat[0] = -afloat[0];
        }
        if (flag1) {
            afloat[1] = -afloat[1];
        }
        if (flag2) {
            afloat[2] = -afloat[2];
        }
        float[] afloat1 = Json.parseFloatArray(p_parseModelRenderer_0_.get(MODEL_ROTATE), 3, new float[3]);
        for (int i = 0; i < afloat1.length; ++i) {
            afloat1[i] = afloat1[i] / 180.0f * (float)Math.PI;
        }
        if (flag) {
            afloat1[0] = -afloat1[0];
        }
        if (flag1) {
            afloat1[1] = -afloat1[1];
        }
        if (flag2) {
            afloat1[2] = -afloat1[2];
        }
        modelrenderer.setRotationPoint(afloat[0], afloat[1], afloat[2]);
        modelrenderer.rotateAngleX = afloat1[0];
        modelrenderer.rotateAngleY = afloat1[1];
        modelrenderer.rotateAngleZ = afloat1[2];
        String s1 = Json.getString(p_parseModelRenderer_0_, MODEL_MIRROR_TEXTURE, "").toLowerCase();
        boolean flag3 = s1.contains("u");
        boolean flag4 = s1.contains("v");
        if (flag3) {
            modelrenderer.mirror = true;
        }
        if (flag4) {
            modelrenderer.mirrorV = true;
        }
        if ((jsonarray = p_parseModelRenderer_0_.getAsJsonArray(MODEL_BOXES)) != null) {
            for (int j = 0; j < jsonarray.size(); ++j) {
                JsonObject jsonobject = jsonarray.get(j).getAsJsonObject();
                int[] aint = Json.parseIntArray(jsonobject.get(BOX_TEXTURE_OFFSET), 2);
                if (aint == null) {
                    throw new JsonParseException("Texture offset not specified");
                }
                float[] afloat2 = Json.parseFloatArray(jsonobject.get(BOX_COORDINATES), 6);
                if (afloat2 == null) {
                    throw new JsonParseException("Coordinates not specified");
                }
                if (flag) {
                    afloat2[0] = -afloat2[0] - afloat2[3];
                }
                if (flag1) {
                    afloat2[1] = -afloat2[1] - afloat2[4];
                }
                if (flag2) {
                    afloat2[2] = -afloat2[2] - afloat2[5];
                }
                float f = Json.getFloat(jsonobject, BOX_SIZE_ADD, 0.0f);
                modelrenderer.setTextureOffset(aint[0], aint[1]);
                modelrenderer.addBox(afloat2[0], afloat2[1], afloat2[2], (int)afloat2[3], (int)afloat2[4], (int)afloat2[5], f);
            }
        }
        if ((jsonarray1 = p_parseModelRenderer_0_.getAsJsonArray(MODEL_SPRITES)) != null) {
            for (int k = 0; k < jsonarray1.size(); ++k) {
                JsonObject jsonobject2 = jsonarray1.get(k).getAsJsonObject();
                int[] aint1 = Json.parseIntArray(jsonobject2.get(BOX_TEXTURE_OFFSET), 2);
                if (aint1 == null) {
                    throw new JsonParseException("Texture offset not specified");
                }
                float[] afloat3 = Json.parseFloatArray(jsonobject2.get(BOX_COORDINATES), 6);
                if (afloat3 == null) {
                    throw new JsonParseException("Coordinates not specified");
                }
                if (flag) {
                    afloat3[0] = -afloat3[0] - afloat3[3];
                }
                if (flag1) {
                    afloat3[1] = -afloat3[1] - afloat3[4];
                }
                if (flag2) {
                    afloat3[2] = -afloat3[2] - afloat3[5];
                }
                float f1 = Json.getFloat(jsonobject2, BOX_SIZE_ADD, 0.0f);
                modelrenderer.setTextureOffset(aint1[0], aint1[1]);
                modelrenderer.addSprite(afloat3[0], afloat3[1], afloat3[2], (int)afloat3[3], (int)afloat3[4], (int)afloat3[5], f1);
            }
        }
        if ((jsonobject1 = (JsonObject)p_parseModelRenderer_0_.get(MODEL_SUBMODEL)) != null) {
            ModelRenderer modelrenderer1 = PlayerItemParser.parseModelRenderer(jsonobject1, p_parseModelRenderer_1_);
            modelrenderer.addChild(modelrenderer1);
        }
        if ((jsonarray2 = (JsonArray)p_parseModelRenderer_0_.get(MODEL_SUBMODELS)) != null) {
            for (int l = 0; l < jsonarray2.size(); ++l) {
                JsonObject jsonobject3 = (JsonObject)jsonarray2.get(l);
                ModelRenderer modelrenderer2 = PlayerItemParser.parseModelRenderer(jsonobject3, p_parseModelRenderer_1_);
                modelrenderer.addChild(modelrenderer2);
            }
        }
        return modelrenderer;
    }
}

