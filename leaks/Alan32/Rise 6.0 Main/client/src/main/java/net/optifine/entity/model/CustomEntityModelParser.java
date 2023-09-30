package net.optifine.entity.model;

import com.google.gson.*;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.entity.model.anim.ModelUpdater;
import net.optifine.entity.model.anim.ModelVariableUpdater;
import net.optifine.player.PlayerItemParser;
import net.optifine.util.Json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CustomEntityModelParser {
    public static final String ENTITY = "entity";
    public static final String TEXTURE = "texture";
    public static final String SHADOW_SIZE = "shadowSize";
    public static final String ITEM_TYPE = "type";
    public static final String ITEM_TEXTURE_SIZE = "textureSize";
    public static final String ITEM_USE_PLAYER_TEXTURE = "usePlayerTexture";
    public static final String ITEM_MODELS = "models";
    public static final String ITEM_ANIMATIONS = "animations";
    public static final String MODEL_ID = "id";
    public static final String MODEL_BASE_ID = "baseId";
    public static final String MODEL_MODEL = "model";
    public static final String MODEL_TYPE = "type";
    public static final String MODEL_PART = "part";
    public static final String MODEL_ATTACH = "attach";
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
    public static final String ENTITY_MODEL = "EntityModel";
    public static final String ENTITY_MODEL_PART = "EntityModelPart";

    public static CustomEntityRenderer parseEntityRender(final JsonObject obj, final String path) {
        final ConnectedParser connectedparser = new ConnectedParser("CustomEntityModels");
        final String s = connectedparser.parseName(path);
        final String s1 = connectedparser.parseBasePath(path);
        final String s2 = Json.getString(obj, "texture");
        final int[] aint = Json.parseIntArray(obj.get("textureSize"), 2);
        final float f = Json.getFloat(obj, "shadowSize", -1.0F);
        final JsonArray jsonarray = (JsonArray) obj.get("models");
        checkNull(jsonarray, "Missing models");
        final Map map = new HashMap();
        final List list = new ArrayList();

        for (int i = 0; i < jsonarray.size(); ++i) {
            final JsonObject jsonobject = (JsonObject) jsonarray.get(i);
            processBaseId(jsonobject, map);
            processExternalModel(jsonobject, map, s1);
            processId(jsonobject, map);
            final CustomModelRenderer custommodelrenderer = parseCustomModelRenderer(jsonobject, aint, s1);

            if (custommodelrenderer != null) {
                list.add(custommodelrenderer);
            }
        }

        final CustomModelRenderer[] acustommodelrenderer = (CustomModelRenderer[]) list.toArray(new CustomModelRenderer[list.size()]);
        ResourceLocation resourcelocation = null;

        if (s2 != null) {
            resourcelocation = getResourceLocation(s1, s2, ".png");
        }

        final CustomEntityRenderer customentityrenderer = new CustomEntityRenderer(s, s1, resourcelocation, acustommodelrenderer, f);
        return customentityrenderer;
    }

    private static void processBaseId(final JsonObject elem, final Map mapModelJsons) {
        final String s = Json.getString(elem, "baseId");

        if (s != null) {
            final JsonObject jsonobject = (JsonObject) mapModelJsons.get(s);

            if (jsonobject == null) {
                Config.warn("BaseID not found: " + s);
            } else {
                copyJsonElements(jsonobject, elem);
            }
        }
    }

    private static void processExternalModel(final JsonObject elem, final Map mapModelJsons, final String basePath) {
        final String s = Json.getString(elem, "model");

        if (s != null) {
            final ResourceLocation resourcelocation = getResourceLocation(basePath, s, ".jpm");

            try {
                final JsonObject jsonobject = loadJson(resourcelocation);

                if (jsonobject == null) {
                    Config.warn("Model not found: " + resourcelocation);
                    return;
                }

                copyJsonElements(jsonobject, elem);
            } catch (final IOException ioexception) {
                Config.error("" + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            } catch (final JsonParseException jsonparseexception) {
                Config.error("" + jsonparseexception.getClass().getName() + ": " + jsonparseexception.getMessage());
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private static void copyJsonElements(final JsonObject objFrom, final JsonObject objTo) {
        for (final Entry<String, JsonElement> entry : objFrom.entrySet()) {
            if (!entry.getKey().equals("id") && !objTo.has(entry.getKey())) {
                objTo.add(entry.getKey(), entry.getValue());
            }
        }
    }

    public static ResourceLocation getResourceLocation(final String basePath, String path, final String extension) {
        if (!path.endsWith(extension)) {
            path = path + extension;
        }

        if (!path.contains("/")) {
            path = basePath + "/" + path;
        } else if (path.startsWith("./")) {
            path = basePath + "/" + path.substring(2);
        } else if (path.startsWith("~/")) {
            path = "optifine/" + path.substring(2);
        }

        return new ResourceLocation(path);
    }

    private static void processId(final JsonObject elem, final Map mapModelJsons) {
        final String s = Json.getString(elem, "id");

        if (s != null) {
            if (s.length() < 1) {
                Config.warn("Empty model ID: " + s);
            } else if (mapModelJsons.containsKey(s)) {
                Config.warn("Duplicate model ID: " + s);
            } else {
                mapModelJsons.put(s, elem);
            }
        }
    }

    public static CustomModelRenderer parseCustomModelRenderer(final JsonObject elem, final int[] textureSize, final String basePath) {
        final String s = Json.getString(elem, "part");
        checkNull(s, "Model part not specified, missing \"replace\" or \"attachTo\".");
        final boolean flag = Json.getBoolean(elem, "attach", false);
        final ModelBase modelbase = new CustomEntityModel();

        if (textureSize != null) {
            modelbase.textureWidth = textureSize[0];
            modelbase.textureHeight = textureSize[1];
        }

        ModelUpdater modelupdater = null;
        final JsonArray jsonarray = (JsonArray) elem.get("animations");

        if (jsonarray != null) {
            final List<ModelVariableUpdater> list = new ArrayList();

            for (int i = 0; i < jsonarray.size(); ++i) {
                final JsonObject jsonobject = (JsonObject) jsonarray.get(i);

                for (final Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                    final String s1 = entry.getKey();
                    final String s2 = entry.getValue().getAsString();
                    final ModelVariableUpdater modelvariableupdater = new ModelVariableUpdater(s1, s2);
                    list.add(modelvariableupdater);
                }
            }

            if (list.size() > 0) {
                final ModelVariableUpdater[] amodelvariableupdater = list.toArray(new ModelVariableUpdater[list.size()]);
                modelupdater = new ModelUpdater(amodelvariableupdater);
            }
        }

        final ModelRenderer modelrenderer = PlayerItemParser.parseModelRenderer(elem, modelbase, textureSize, basePath);
        final CustomModelRenderer custommodelrenderer = new CustomModelRenderer(s, flag, modelrenderer, modelupdater);
        return custommodelrenderer;
    }

    private static void checkNull(final Object obj, final String msg) {
        if (obj == null) {
            throw new JsonParseException(msg);
        }
    }

    public static JsonObject loadJson(final ResourceLocation location) throws IOException, JsonParseException {
        final InputStream inputstream = Config.getResourceStream(location);

        if (inputstream == null) {
            return null;
        } else {
            final String s = Config.readInputStream(inputstream, "ASCII");
            inputstream.close();
            final JsonParser jsonparser = new JsonParser();
            final JsonObject jsonobject = (JsonObject) jsonparser.parse(s);
            return jsonobject;
        }
    }
}
