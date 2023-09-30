package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ModelBlockDefinition {
    static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ModelBlockDefinition.class, new ModelBlockDefinition.Deserializer()).registerTypeAdapter(ModelBlockDefinition.Variant.class, new ModelBlockDefinition.Variant.Deserializer()).create();
    private final Map<String, ModelBlockDefinition.Variants> mapVariants = Maps.newHashMap();

    public static ModelBlockDefinition parseFromReader(final Reader p_178331_0_) {
        return GSON.fromJson(p_178331_0_, ModelBlockDefinition.class);
    }

    public ModelBlockDefinition(final Collection<ModelBlockDefinition.Variants> p_i46221_1_) {
        for (final ModelBlockDefinition.Variants modelblockdefinition$variants : p_i46221_1_) {
            this.mapVariants.put(modelblockdefinition$variants.name, modelblockdefinition$variants);
        }
    }

    public ModelBlockDefinition(final List<ModelBlockDefinition> p_i46222_1_) {
        for (final ModelBlockDefinition modelblockdefinition : p_i46222_1_) {
            this.mapVariants.putAll(modelblockdefinition.mapVariants);
        }
    }

    public ModelBlockDefinition.Variants getVariants(final String p_178330_1_) {
        final ModelBlockDefinition.Variants modelblockdefinition$variants = this.mapVariants.get(p_178330_1_);

        if (modelblockdefinition$variants == null) {
            throw new ModelBlockDefinition.MissingVariantException();
        } else {
            return modelblockdefinition$variants;
        }
    }

    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ instanceof ModelBlockDefinition) {
            final ModelBlockDefinition modelblockdefinition = (ModelBlockDefinition) p_equals_1_;
            return this.mapVariants.equals(modelblockdefinition.mapVariants);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.mapVariants.hashCode();
    }

    public static class Deserializer implements JsonDeserializer<ModelBlockDefinition> {
        public ModelBlockDefinition deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            final List<ModelBlockDefinition.Variants> list = this.parseVariantsList(p_deserialize_3_, jsonobject);
            return new ModelBlockDefinition(list);
        }

        protected List<ModelBlockDefinition.Variants> parseVariantsList(final JsonDeserializationContext p_178334_1_, final JsonObject p_178334_2_) {
            final JsonObject jsonobject = JsonUtils.getJsonObject(p_178334_2_, "variants");
            final List<ModelBlockDefinition.Variants> list = Lists.newArrayList();

            for (final Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                list.add(this.parseVariants(p_178334_1_, entry));
            }

            return list;
        }

        protected ModelBlockDefinition.Variants parseVariants(final JsonDeserializationContext p_178335_1_, final Entry<String, JsonElement> p_178335_2_) {
            final String s = p_178335_2_.getKey();
            final List<ModelBlockDefinition.Variant> list = Lists.newArrayList();
            final JsonElement jsonelement = p_178335_2_.getValue();

            if (jsonelement.isJsonArray()) {
                for (final JsonElement jsonelement1 : jsonelement.getAsJsonArray()) {
                    list.add(p_178335_1_.deserialize(jsonelement1, Variant.class));
                }
            } else {
                list.add(p_178335_1_.deserialize(jsonelement, Variant.class));
            }

            return new ModelBlockDefinition.Variants(s, list);
        }
    }

    public class MissingVariantException extends RuntimeException {
    }

    public static class Variant {
        private final ResourceLocation modelLocation;
        private final ModelRotation modelRotation;
        private final boolean uvLock;
        private final int weight;

        public Variant(final ResourceLocation modelLocationIn, final ModelRotation modelRotationIn, final boolean uvLockIn, final int weightIn) {
            this.modelLocation = modelLocationIn;
            this.modelRotation = modelRotationIn;
            this.uvLock = uvLockIn;
            this.weight = weightIn;
        }

        public ResourceLocation getModelLocation() {
            return this.modelLocation;
        }

        public ModelRotation getRotation() {
            return this.modelRotation;
        }

        public boolean isUvLocked() {
            return this.uvLock;
        }

        public int getWeight() {
            return this.weight;
        }

        public boolean equals(final Object p_equals_1_) {
            if (this == p_equals_1_) {
                return true;
            } else if (!(p_equals_1_ instanceof ModelBlockDefinition.Variant)) {
                return false;
            } else {
                final ModelBlockDefinition.Variant modelblockdefinition$variant = (ModelBlockDefinition.Variant) p_equals_1_;
                return this.modelLocation.equals(modelblockdefinition$variant.modelLocation) && this.modelRotation == modelblockdefinition$variant.modelRotation && this.uvLock == modelblockdefinition$variant.uvLock;
            }
        }

        public int hashCode() {
            int i = this.modelLocation.hashCode();
            i = 31 * i + (this.modelRotation != null ? this.modelRotation.hashCode() : 0);
            i = 31 * i + (this.uvLock ? 1 : 0);
            return i;
        }

        public static class Deserializer implements JsonDeserializer<ModelBlockDefinition.Variant> {
            public ModelBlockDefinition.Variant deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
                final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                final String s = this.parseModel(jsonobject);
                final ModelRotation modelrotation = this.parseRotation(jsonobject);
                final boolean flag = this.parseUvLock(jsonobject);
                final int i = this.parseWeight(jsonobject);
                return new ModelBlockDefinition.Variant(this.makeModelLocation(s), modelrotation, flag, i);
            }

            private ResourceLocation makeModelLocation(final String p_178426_1_) {
                ResourceLocation resourcelocation = new ResourceLocation(p_178426_1_);
                resourcelocation = new ResourceLocation(resourcelocation.getResourceDomain(), "block/" + resourcelocation.getResourcePath());
                return resourcelocation;
            }

            private boolean parseUvLock(final JsonObject p_178429_1_) {
                return JsonUtils.getBoolean(p_178429_1_, "uvlock", false);
            }

            protected ModelRotation parseRotation(final JsonObject p_178428_1_) {
                final int i = JsonUtils.getInt(p_178428_1_, "x", 0);
                final int j = JsonUtils.getInt(p_178428_1_, "y", 0);
                final ModelRotation modelrotation = ModelRotation.getModelRotation(i, j);

                if (modelrotation == null) {
                    throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
                } else {
                    return modelrotation;
                }
            }

            protected String parseModel(final JsonObject p_178424_1_) {
                return JsonUtils.getString(p_178424_1_, "model");
            }

            protected int parseWeight(final JsonObject p_178427_1_) {
                return JsonUtils.getInt(p_178427_1_, "weight", 1);
            }
        }
    }

    public static class Variants {
        private final String name;
        private final List<ModelBlockDefinition.Variant> listVariants;

        public Variants(final String nameIn, final List<ModelBlockDefinition.Variant> listVariantsIn) {
            this.name = nameIn;
            this.listVariants = listVariantsIn;
        }

        public List<ModelBlockDefinition.Variant> getVariants() {
            return this.listVariants;
        }

        public boolean equals(final Object p_equals_1_) {
            if (this == p_equals_1_) {
                return true;
            } else if (!(p_equals_1_ instanceof ModelBlockDefinition.Variants)) {
                return false;
            } else {
                final ModelBlockDefinition.Variants modelblockdefinition$variants = (ModelBlockDefinition.Variants) p_equals_1_;
                return this.name.equals(modelblockdefinition$variants.name) && this.listVariants.equals(modelblockdefinition$variants.listVariants);
            }
        }

        public int hashCode() {
            int i = this.name.hashCode();
            i = 31 * i + this.listVariants.hashCode();
            return i;
        }
    }
}
