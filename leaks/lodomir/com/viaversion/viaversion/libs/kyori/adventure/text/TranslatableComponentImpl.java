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
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.TranslatableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TranslatableComponentImpl
extends AbstractComponent
implements TranslatableComponent {
    private final String key;
    private final List<Component> args;

    TranslatableComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String key, @NotNull @NotNull ComponentLike @NotNull [] args) {
        this(children, style, key, Arrays.asList(args));
    }

    TranslatableComponentImpl(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String key, @NotNull List<? extends ComponentLike> args) {
        super(children, style);
        this.key = Objects.requireNonNull(key, "key");
        this.args = ComponentLike.asComponents(args);
    }

    @Override
    @NotNull
    public String key() {
        return this.key;
    }

    @Override
    @NotNull
    public TranslatableComponent key(@NotNull String key) {
        if (Objects.equals(this.key, key)) {
            return this;
        }
        return new TranslatableComponentImpl(this.children, this.style, Objects.requireNonNull(key, "key"), this.args);
    }

    @Override
    @NotNull
    public List<Component> args() {
        return this.args;
    }

    @Override
    @NotNull
    public TranslatableComponent args(ComponentLike ... args) {
        return new TranslatableComponentImpl(this.children, this.style, this.key, args);
    }

    @Override
    @NotNull
    public TranslatableComponent args(@NotNull List<? extends ComponentLike> args) {
        return new TranslatableComponentImpl(this.children, this.style, this.key, args);
    }

    @Override
    @NotNull
    public TranslatableComponent children(@NotNull List<? extends ComponentLike> children) {
        return new TranslatableComponentImpl(children, this.style, this.key, this.args);
    }

    @Override
    @NotNull
    public TranslatableComponent style(@NotNull Style style) {
        return new TranslatableComponentImpl(this.children, style, this.key, this.args);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TranslatableComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        TranslatableComponent that = (TranslatableComponent)other;
        return Objects.equals(this.key, that.key()) && Objects.equals(this.args, that.args());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.key.hashCode();
        result = 31 * result + this.args.hashCode();
        return result;
    }

    @Override
    @NotNull
    protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
        return Stream.concat(Stream.of(ExaminableProperty.of("key", this.key), ExaminableProperty.of("args", this.args)), super.examinablePropertiesWithoutChildren());
    }

    @Override
    @NotNull
    public TranslatableComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl
    extends AbstractComponentBuilder<TranslatableComponent, TranslatableComponent.Builder>
    implements TranslatableComponent.Builder {
        @Nullable
        private String key;
        private List<? extends Component> args = Collections.emptyList();

        BuilderImpl() {
        }

        BuilderImpl(@NotNull TranslatableComponent component) {
            super(component);
            this.key = component.key();
            this.args = component.args();
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder key(@NotNull String key) {
            this.key = key;
            return this;
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder args(@NotNull ComponentBuilder<?, ?> arg) {
            return this.args(Collections.singletonList(arg.build()));
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder args(ComponentBuilder<?, ?> ... args) {
            if (args.length == 0) {
                return this.args(Collections.emptyList());
            }
            return this.args(Stream.of(args).map(ComponentBuilder::build).collect(Collectors.toList()));
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder args(@NotNull Component arg) {
            return this.args(Collections.singletonList(arg));
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder args(ComponentLike ... args) {
            if (args.length == 0) {
                return this.args(Collections.emptyList());
            }
            return this.args(Arrays.asList(args));
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder args(@NotNull List<? extends ComponentLike> args) {
            this.args = ComponentLike.asComponents(args);
            return this;
        }

        @Override
        @NotNull
        public TranslatableComponentImpl build() {
            if (this.key == null) {
                throw new IllegalStateException("key must be set");
            }
            return new TranslatableComponentImpl(this.children, this.buildStyle(), this.key, this.args);
        }
    }
}

