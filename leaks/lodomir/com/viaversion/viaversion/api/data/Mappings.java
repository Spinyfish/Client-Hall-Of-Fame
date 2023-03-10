/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.Arrays;

public interface Mappings {
    public int getNewId(int var1);

    public void setNewId(int var1, int var2);

    public int size();

    public int mappedSize();

    public static <T extends Mappings> Builder<T> builder(MappingsSupplier<T> supplier) {
        return new Builder<T>(supplier);
    }

    public static class Builder<T extends Mappings> {
        protected final MappingsSupplier<T> supplier;
        protected JsonElement unmapped;
        protected JsonElement mapped;
        protected JsonObject diffMappings;
        protected int mappedSize = -1;
        protected int size = -1;
        protected boolean warnOnMissing = true;

        protected Builder(MappingsSupplier<T> supplier) {
            this.supplier = supplier;
        }

        public Builder<T> customEntrySize(int size) {
            this.size = size;
            return this;
        }

        public Builder<T> customMappedSize(int size) {
            this.mappedSize = size;
            return this;
        }

        public Builder<T> warnOnMissing(boolean warnOnMissing) {
            this.warnOnMissing = warnOnMissing;
            return this;
        }

        public Builder<T> unmapped(JsonArray unmappedArray) {
            this.unmapped = unmappedArray;
            return this;
        }

        public Builder<T> unmapped(JsonObject unmappedObject) {
            this.unmapped = unmappedObject;
            return this;
        }

        public Builder<T> mapped(JsonArray mappedArray) {
            this.mapped = mappedArray;
            return this;
        }

        public Builder<T> mapped(JsonObject mappedObject) {
            this.mapped = mappedObject;
            return this;
        }

        public Builder<T> diffMappings(JsonObject diffMappings) {
            this.diffMappings = diffMappings;
            return this;
        }

        public T build() {
            int size = this.size != -1 ? this.size : this.size(this.unmapped);
            int mappedSize = this.mappedSize != -1 ? this.mappedSize : this.size(this.mapped);
            int[] mappings = new int[size];
            Arrays.fill(mappings, -1);
            if (this.unmapped.isJsonArray()) {
                if (this.mapped.isJsonObject()) {
                    MappingDataLoader.mapIdentifiers(mappings, this.toJsonObject(this.unmapped.getAsJsonArray()), this.mapped.getAsJsonObject(), this.diffMappings, this.warnOnMissing);
                } else {
                    MappingDataLoader.mapIdentifiers(mappings, this.unmapped.getAsJsonArray(), this.mapped.getAsJsonArray(), this.diffMappings, this.warnOnMissing);
                }
            } else if (this.mapped.isJsonArray()) {
                MappingDataLoader.mapIdentifiers(mappings, this.unmapped.getAsJsonObject(), this.toJsonObject(this.mapped.getAsJsonArray()), this.diffMappings, this.warnOnMissing);
            } else {
                MappingDataLoader.mapIdentifiers(mappings, this.unmapped.getAsJsonObject(), this.mapped.getAsJsonObject(), this.diffMappings, this.warnOnMissing);
            }
            return this.supplier.supply(mappings, mappedSize);
        }

        protected int size(JsonElement element) {
            return element.isJsonObject() ? element.getAsJsonObject().size() : element.getAsJsonArray().size();
        }

        protected JsonObject toJsonObject(JsonArray array) {
            JsonObject object = new JsonObject();
            for (int i = 0; i < array.size(); ++i) {
                JsonElement element = array.get(i);
                object.add(Integer.toString(i), element);
            }
            return object;
        }
    }

    @FunctionalInterface
    public static interface MappingsSupplier<T extends Mappings> {
        public T supply(int[] var1, int var2);
    }
}

