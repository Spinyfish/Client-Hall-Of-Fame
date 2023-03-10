/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.AbstractComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.AbstractComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class NBTComponentImpl<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>>
extends AbstractComponent
implements NBTComponent<C, B> {
    static final boolean INTERPRET_DEFAULT = false;
    final String nbtPath;
    final boolean interpret;
    @Nullable
    final Component separator;

    NBTComponentImpl(@NotNull List<? extends ComponentLike> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable ComponentLike separator) {
        super(children, style);
        this.nbtPath = nbtPath;
        this.interpret = interpret;
        this.separator = ComponentLike.unbox(separator);
    }

    @Override
    @NotNull
    public String nbtPath() {
        return this.nbtPath;
    }

    @Override
    public boolean interpret() {
        return this.interpret;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NBTComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        NBTComponent that = (NBTComponent)other;
        return Objects.equals(this.nbtPath, that.nbtPath()) && this.interpret == that.interpret() && Objects.equals(this.separator, that.separator());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.nbtPath.hashCode();
        result = 31 * result + Boolean.hashCode(this.interpret);
        result = 31 * result + Objects.hashCode(this.separator);
        return result;
    }

    @Override
    @NotNull
    protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
        return Stream.concat(Stream.of(ExaminableProperty.of("nbtPath", this.nbtPath), ExaminableProperty.of("interpret", this.interpret), ExaminableProperty.of("separator", this.separator)), super.examinablePropertiesWithoutChildren());
    }

    static abstract class BuilderImpl<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>>
    extends AbstractComponentBuilder<C, B>
    implements NBTComponentBuilder<C, B> {
        @Nullable
        protected String nbtPath;
        protected boolean interpret = false;
        @Nullable
        protected Component separator;

        BuilderImpl() {
        }

        BuilderImpl(@NotNull C component) {
            super(component);
            this.nbtPath = component.nbtPath();
            this.interpret = component.interpret();
        }

        @Override
        @NotNull
        public B nbtPath(@NotNull String nbtPath) {
            this.nbtPath = nbtPath;
            return (B)this;
        }

        @Override
        @NotNull
        public B interpret(boolean interpret) {
            this.interpret = interpret;
            return (B)this;
        }

        @Override
        @NotNull
        public B separator(@Nullable ComponentLike separator) {
            this.separator = ComponentLike.unbox(separator);
            return (B)this;
        }
    }
}

