/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.api.data;

import com.viaversion.viabackwards.api.data.VBMappingDataLoader;
import com.viaversion.viaversion.api.data.IntArrayMappings;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.data.Mappings;
import java.util.Arrays;

public final class VBMappings
extends IntArrayMappings {
    private VBMappings(int[] oldToNew, int mappedIds) {
        super(oldToNew, mappedIds);
    }

    public static Mappings.Builder<VBMappings> vbBuilder() {
        return new Builder(VBMappings::new);
    }

    public static final class Builder
    extends Mappings.Builder<VBMappings> {
        private Builder(Mappings.MappingsSupplier<VBMappings> supplier) {
            super(supplier);
        }

        @Override
        public VBMappings build() {
            int size = this.size != -1 ? this.size : this.size(this.unmapped);
            int mappedSize = this.mappedSize != -1 ? this.mappedSize : this.size(this.mapped);
            int[] mappings = new int[size];
            Arrays.fill(mappings, -1);
            if (this.unmapped.isJsonArray()) {
                if (this.mapped.isJsonObject()) {
                    VBMappingDataLoader.mapIdentifiers(mappings, this.toJsonObject(this.unmapped.getAsJsonArray()), this.mapped.getAsJsonObject(), this.diffMappings, this.warnOnMissing);
                } else {
                    MappingDataLoader.mapIdentifiers(mappings, this.unmapped.getAsJsonArray(), this.mapped.getAsJsonArray(), this.diffMappings, this.warnOnMissing);
                }
            } else if (this.mapped.isJsonArray()) {
                VBMappingDataLoader.mapIdentifiers(mappings, this.unmapped.getAsJsonObject(), this.toJsonObject(this.mapped.getAsJsonArray()), this.diffMappings, this.warnOnMissing);
            } else {
                VBMappingDataLoader.mapIdentifiers(mappings, this.unmapped.getAsJsonObject(), this.mapped.getAsJsonObject(), this.diffMappings, this.warnOnMissing);
            }
            return (VBMappings)this.supplier.supply(mappings, mappedSize);
        }
    }
}

