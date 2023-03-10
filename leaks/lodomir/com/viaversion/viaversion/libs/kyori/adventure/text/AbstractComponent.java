/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Debug$Renderer
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentCompaction;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextReplacementConfig;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextReplacementConfigImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextReplacementRenderer;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import com.viaversion.viaversion.libs.kyori.examination.string.StringExaminer;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Debug.Renderer(text="this.debuggerString()", childrenArray="this.children().toArray()", hasChildren="!this.children().isEmpty()")
public abstract class AbstractComponent
implements Component {
    private static final Predicate<Component> NOT_EMPTY = component -> component != Component.empty();
    protected final List<Component> children;
    protected final Style style;

    protected AbstractComponent(@NotNull List<? extends ComponentLike> children, @NotNull Style style) {
        this.children = ComponentLike.asComponents(children, NOT_EMPTY);
        this.style = style;
    }

    @Override
    @NotNull
    public final List<Component> children() {
        return this.children;
    }

    @Override
    @NotNull
    public final Style style() {
        return this.style;
    }

    @Override
    @NotNull
    public Component replaceText(@NotNull Consumer<TextReplacementConfig.Builder> configurer) {
        Objects.requireNonNull(configurer, "configurer");
        return this.replaceText((TextReplacementConfig)Buildable.configureAndBuild(TextReplacementConfig.builder(), configurer));
    }

    @Override
    @NotNull
    public Component replaceText(@NotNull TextReplacementConfig config) {
        Objects.requireNonNull(config, "replacement");
        if (!(config instanceof TextReplacementConfigImpl)) {
            throw new IllegalArgumentException("Provided replacement was a custom TextReplacementConfig implementation, which is not supported.");
        }
        return TextReplacementRenderer.INSTANCE.render((Component)this, ((TextReplacementConfigImpl)config).createState());
    }

    @Override
    @NotNull
    public Component compact() {
        return ComponentCompaction.compact(this, null);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractComponent)) {
            return false;
        }
        AbstractComponent that = (AbstractComponent)other;
        return Objects.equals(this.children, that.children) && Objects.equals(this.style, that.style);
    }

    public int hashCode() {
        int result = this.children.hashCode();
        result = 31 * result + this.style.hashCode();
        return result;
    }

    private String debuggerString() {
        return (String)StringExaminer.simpleEscaping().examine(this.examinableName(), this.examinablePropertiesWithoutChildren());
    }

    protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
        return Stream.of(ExaminableProperty.of("style", this.style));
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(this.examinablePropertiesWithoutChildren(), Stream.of(ExaminableProperty.of("children", this.children)));
    }

    public String toString() {
        return this.examine(StringExaminer.simpleEscaping());
    }
}

