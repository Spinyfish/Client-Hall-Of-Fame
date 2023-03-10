/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.StorageNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class StorageNBTComponentImpl
extends NBTComponentImpl<StorageNBTComponent, StorageNBTComponent.Builder>
implements StorageNBTComponent {
    private final Key storage;

    StorageNBTComponentImpl(@NotNull List<? extends ComponentLike> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable ComponentLike separator, Key storage) {
        super(children, style, nbtPath, interpret, separator);
        this.storage = storage;
    }

    @Override
    @NotNull
    public StorageNBTComponent nbtPath(@NotNull String nbtPath) {
        if (Objects.equals(this.nbtPath, nbtPath)) {
            return this;
        }
        return new StorageNBTComponentImpl(this.children, this.style, nbtPath, this.interpret, this.separator, this.storage);
    }

    @Override
    @NotNull
    public StorageNBTComponent interpret(boolean interpret) {
        if (this.interpret == interpret) {
            return this;
        }
        return new StorageNBTComponentImpl(this.children, this.style, this.nbtPath, interpret, this.separator, this.storage);
    }

    @Override
    @Nullable
    public Component separator() {
        return this.separator;
    }

    @Override
    @NotNull
    public StorageNBTComponent separator(@Nullable ComponentLike separator) {
        return new StorageNBTComponentImpl(this.children, this.style, this.nbtPath, this.interpret, separator, this.storage);
    }

    @Override
    @NotNull
    public Key storage() {
        return this.storage;
    }

    @Override
    @NotNull
    public StorageNBTComponent storage(@NotNull Key storage) {
        if (Objects.equals(this.storage, storage)) {
            return this;
        }
        return new StorageNBTComponentImpl(this.children, this.style, this.nbtPath, this.interpret, this.separator, storage);
    }

    @Override
    @NotNull
    public StorageNBTComponent children(@NotNull List<? extends ComponentLike> children) {
        return new StorageNBTComponentImpl(children, this.style, this.nbtPath, this.interpret, this.separator, this.storage);
    }

    @Override
    @NotNull
    public StorageNBTComponent style(@NotNull Style style) {
        return new StorageNBTComponentImpl(this.children, style, this.nbtPath, this.interpret, this.separator, this.storage);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StorageNBTComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        StorageNBTComponentImpl that = (StorageNBTComponentImpl)other;
        return Objects.equals(this.storage, that.storage());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.storage.hashCode();
        return result;
    }

    @Override
    @NotNull
    protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
        return Stream.concat(Stream.of(ExaminableProperty.of("storage", this.storage)), super.examinablePropertiesWithoutChildren());
    }

    @Override
    public @NotNull StorageNBTComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static class BuilderImpl
    extends NBTComponentImpl.BuilderImpl<StorageNBTComponent, StorageNBTComponent.Builder>
    implements StorageNBTComponent.Builder {
        @Nullable
        private Key storage;

        BuilderImpl() {
        }

        BuilderImpl(@NotNull StorageNBTComponent component) {
            super(component);
            this.storage = component.storage();
        }

        @Override
        public @NotNull StorageNBTComponent.Builder storage(@NotNull Key storage) {
            this.storage = storage;
            return this;
        }

        @Override
        @NotNull
        public StorageNBTComponent build() {
            if (this.nbtPath == null) {
                throw new IllegalStateException("nbt path must be set");
            }
            if (this.storage == null) {
                throw new IllegalStateException("storage must be set");
            }
            return new StorageNBTComponentImpl(this.children, this.buildStyle(), this.nbtPath, this.interpret, this.separator, this.storage);
        }
    }
}

