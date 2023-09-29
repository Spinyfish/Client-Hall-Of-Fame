package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ModelBlock {
    private static final Logger LOGGER = LogManager.getLogger();
    static final Gson SERIALIZER = (new GsonBuilder()).registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).create();
    private final List<BlockPart> elements;
    private final boolean gui3d;
    private final boolean ambientOcclusion;
    private final ItemCameraTransforms cameraTransforms;
    public String name;
    protected final Map<String, String> textures;
    protected ModelBlock parent;
    protected ResourceLocation parentLocation;

    public static ModelBlock deserialize(final Reader p_178307_0_) {
        return SERIALIZER.fromJson(p_178307_0_, ModelBlock.class);
    }

    public static ModelBlock deserialize(final String p_178294_0_) {
        return deserialize(new StringReader(p_178294_0_));
    }

    protected ModelBlock(final List<BlockPart> p_i46225_1_, final Map<String, String> p_i46225_2_, final boolean p_i46225_3_, final boolean p_i46225_4_, final ItemCameraTransforms p_i46225_5_) {
        this(null, p_i46225_1_, p_i46225_2_, p_i46225_3_, p_i46225_4_, p_i46225_5_);
    }

    protected ModelBlock(final ResourceLocation p_i46226_1_, final Map<String, String> p_i46226_2_, final boolean p_i46226_3_, final boolean p_i46226_4_, final ItemCameraTransforms p_i46226_5_) {
        this(p_i46226_1_, Collections.emptyList(), p_i46226_2_, p_i46226_3_, p_i46226_4_, p_i46226_5_);
    }

    private ModelBlock(final ResourceLocation parentLocationIn, final List<BlockPart> elementsIn, final Map<String, String> texturesIn, final boolean ambientOcclusionIn, final boolean gui3dIn, final ItemCameraTransforms cameraTransformsIn) {
        this.name = "";
        this.elements = elementsIn;
        this.ambientOcclusion = ambientOcclusionIn;
        this.gui3d = gui3dIn;
        this.textures = texturesIn;
        this.parentLocation = parentLocationIn;
        this.cameraTransforms = cameraTransformsIn;
    }

    public List<BlockPart> getElements() {
        return this.hasParent() ? this.parent.getElements() : this.elements;
    }

    private boolean hasParent() {
        return this.parent != null;
    }

    public boolean isAmbientOcclusion() {
        return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
    }

    public boolean isGui3d() {
        return this.gui3d;
    }

    public boolean isResolved() {
        return this.parentLocation == null || this.parent != null && this.parent.isResolved();
    }

    public void getParentFromMap(final Map<ResourceLocation, ModelBlock> p_178299_1_) {
        if (this.parentLocation != null) {
            this.parent = p_178299_1_.get(this.parentLocation);
        }
    }

    public boolean isTexturePresent(final String textureName) {
        return !"missingno".equals(this.resolveTextureName(textureName));
    }

    public String resolveTextureName(String textureName) {
        if (!this.startsWithHash(textureName)) {
            textureName = '#' + textureName;
        }

        return this.resolveTextureName(textureName, new ModelBlock.Bookkeep(this));
    }

    private String resolveTextureName(final String textureName, final ModelBlock.Bookkeep p_178302_2_) {
        if (this.startsWithHash(textureName)) {
            if (this == p_178302_2_.modelExt) {
                LOGGER.warn("Unable to resolve texture due to upward reference: " + textureName + " in " + this.name);
                return "missingno";
            } else {
                String s = this.textures.get(textureName.substring(1));

                if (s == null && this.hasParent()) {
                    s = this.parent.resolveTextureName(textureName, p_178302_2_);
                }

                p_178302_2_.modelExt = this;

                if (s != null && this.startsWithHash(s)) {
                    s = p_178302_2_.model.resolveTextureName(s, p_178302_2_);
                }

                return s != null && !this.startsWithHash(s) ? s : "missingno";
            }
        } else {
            return textureName;
        }
    }

    private boolean startsWithHash(final String hash) {
        return hash.charAt(0) == 35;
    }

    public ResourceLocation getParentLocation() {
        return this.parentLocation;
    }

    public ModelBlock getRootModel() {
        return this.hasParent() ? this.parent.getRootModel() : this;
    }

    public ItemCameraTransforms func_181682_g() {
        final ItemTransformVec3f itemtransformvec3f = this.func_181681_a(ItemCameraTransforms.TransformType.THIRD_PERSON);
        final ItemTransformVec3f itemtransformvec3f1 = this.func_181681_a(ItemCameraTransforms.TransformType.FIRST_PERSON);
        final ItemTransformVec3f itemtransformvec3f2 = this.func_181681_a(ItemCameraTransforms.TransformType.HEAD);
        final ItemTransformVec3f itemtransformvec3f3 = this.func_181681_a(ItemCameraTransforms.TransformType.GUI);
        final ItemTransformVec3f itemtransformvec3f4 = this.func_181681_a(ItemCameraTransforms.TransformType.GROUND);
        final ItemTransformVec3f itemtransformvec3f5 = this.func_181681_a(ItemCameraTransforms.TransformType.FIXED);
        return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5);
    }

    private ItemTransformVec3f func_181681_a(final ItemCameraTransforms.TransformType p_181681_1_) {
        return this.parent != null && !this.cameraTransforms.func_181687_c(p_181681_1_) ? this.parent.func_181681_a(p_181681_1_) : this.cameraTransforms.func_181688_b(p_181681_1_);
    }

    public static void checkModelHierarchy(final Map<ResourceLocation, ModelBlock> p_178312_0_) {
        for (final ModelBlock modelblock : p_178312_0_.values()) {
            try {
                ModelBlock modelblock1 = modelblock.parent;

                for (ModelBlock modelblock2 = modelblock1.parent; modelblock1 != modelblock2; modelblock2 = modelblock2.parent.parent) {
                    modelblock1 = modelblock1.parent;
                }

                throw new ModelBlock.LoopException();
            } catch (final NullPointerException var5) {
            }
        }
    }

    static final class Bookkeep {
        public final ModelBlock model;
        public ModelBlock modelExt;

        private Bookkeep(final ModelBlock p_i46223_1_) {
            this.model = p_i46223_1_;
        }
    }

    public static class Deserializer implements JsonDeserializer<ModelBlock> {
        public ModelBlock deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            final List<BlockPart> list = this.getModelElements(p_deserialize_3_, jsonobject);
            final String s = this.getParent(jsonobject);
            final boolean flag = StringUtils.isEmpty(s);
            final boolean flag1 = list.isEmpty();

            if (flag1 && flag) {
                throw new JsonParseException("BlockModel requires either elements or parent, found neither");
            } else if (!flag && !flag1) {
                throw new JsonParseException("BlockModel requires either elements or parent, found both");
            } else {
                final Map<String, String> map = this.getTextures(jsonobject);
                final boolean flag2 = this.getAmbientOcclusionEnabled(jsonobject);
                ItemCameraTransforms itemcameratransforms = ItemCameraTransforms.DEFAULT;

                if (jsonobject.has("display")) {
                    final JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "display");
                    itemcameratransforms = p_deserialize_3_.deserialize(jsonobject1, ItemCameraTransforms.class);
                }

                return flag1 ? new ModelBlock(new ResourceLocation(s), map, flag2, true, itemcameratransforms) : new ModelBlock(list, map, flag2, true, itemcameratransforms);
            }
        }

        private Map<String, String> getTextures(final JsonObject p_178329_1_) {
            final Map<String, String> map = Maps.newHashMap();

            if (p_178329_1_.has("textures")) {
                final JsonObject jsonobject = p_178329_1_.getAsJsonObject("textures");

                for (final Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                    map.put(entry.getKey(), entry.getValue().getAsString());
                }
            }

            return map;
        }

        private String getParent(final JsonObject p_178326_1_) {
            return JsonUtils.getString(p_178326_1_, "parent", "");
        }

        protected boolean getAmbientOcclusionEnabled(final JsonObject p_178328_1_) {
            return JsonUtils.getBoolean(p_178328_1_, "ambientocclusion", true);
        }

        protected List<BlockPart> getModelElements(final JsonDeserializationContext p_178325_1_, final JsonObject p_178325_2_) {
            final List<BlockPart> list = Lists.newArrayList();

            if (p_178325_2_.has("elements")) {
                for (final JsonElement jsonelement : JsonUtils.getJsonArray(p_178325_2_, "elements")) {
                    list.add(p_178325_1_.deserialize(jsonelement, BlockPart.class));
                }
            }

            return list;
        }
    }

    public static class LoopException extends RuntimeException {
    }
}
