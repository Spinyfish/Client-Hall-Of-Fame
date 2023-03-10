/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.bossbar;

import com.viaversion.viaversion.libs.kyori.adventure.bossbar.BossBar;
import com.viaversion.viaversion.libs.kyori.adventure.bossbar.HackyBossBarPlatformBridge;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import com.viaversion.viaversion.libs.kyori.examination.string.StringExaminer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

final class BossBarImpl
extends HackyBossBarPlatformBridge
implements BossBar {
    private static final BiConsumer<BossBarImpl, Set<BossBar.Flag>> FLAGS_ADDED = (bar, flagsAdded) -> bar.forEachListener(listener -> listener.bossBarFlagsChanged((BossBar)bar, (Set<BossBar.Flag>)flagsAdded, Collections.emptySet()));
    private static final BiConsumer<BossBarImpl, Set<BossBar.Flag>> FLAGS_REMOVED = (bar, flagsRemoved) -> bar.forEachListener(listener -> listener.bossBarFlagsChanged((BossBar)bar, Collections.emptySet(), (Set<BossBar.Flag>)flagsRemoved));
    private final List<BossBar.Listener> listeners = new CopyOnWriteArrayList<BossBar.Listener>();
    private Component name;
    private float progress;
    private BossBar.Color color;
    private BossBar.Overlay overlay;
    private final Set<BossBar.Flag> flags = EnumSet.noneOf(BossBar.Flag.class);

    BossBarImpl(@NotNull Component name, float progress, @NotNull BossBar.Color color, @NotNull BossBar.Overlay overlay) {
        this.name = Objects.requireNonNull(name, "name");
        this.progress = progress;
        this.color = Objects.requireNonNull(color, "color");
        this.overlay = Objects.requireNonNull(overlay, "overlay");
    }

    BossBarImpl(@NotNull Component name, float progress, @NotNull BossBar.Color color, @NotNull BossBar.Overlay overlay, @NotNull Set<BossBar.Flag> flags) {
        this(name, progress, color, overlay);
        this.flags.addAll(flags);
    }

    @Override
    @NotNull
    public Component name() {
        return this.name;
    }

    @Override
    @NotNull
    public BossBar name(@NotNull Component newName) {
        Objects.requireNonNull(newName, "name");
        Component oldName = this.name;
        if (!Objects.equals(newName, oldName)) {
            this.name = newName;
            this.forEachListener(listener -> listener.bossBarNameChanged(this, oldName, newName));
        }
        return this;
    }

    @Override
    public float progress() {
        return this.progress;
    }

    @Override
    @NotNull
    public BossBar progress(float newProgress) {
        BossBarImpl.checkProgress(newProgress);
        float oldProgress = this.progress;
        if (newProgress != oldProgress) {
            this.progress = newProgress;
            this.forEachListener(listener -> listener.bossBarProgressChanged(this, oldProgress, newProgress));
        }
        return this;
    }

    static void checkProgress(float progress) {
        if (progress < 0.0f || progress > 1.0f) {
            throw new IllegalArgumentException("progress must be between 0.0 and 1.0, was " + progress);
        }
    }

    @Override
    @NotNull
    public BossBar.Color color() {
        return this.color;
    }

    @Override
    @NotNull
    public BossBar color(@NotNull BossBar.Color newColor) {
        Objects.requireNonNull(newColor, "color");
        BossBar.Color oldColor = this.color;
        if (newColor != oldColor) {
            this.color = newColor;
            this.forEachListener(listener -> listener.bossBarColorChanged(this, oldColor, newColor));
        }
        return this;
    }

    @Override
    @NotNull
    public BossBar.Overlay overlay() {
        return this.overlay;
    }

    @Override
    @NotNull
    public BossBar overlay(@NotNull BossBar.Overlay newOverlay) {
        Objects.requireNonNull(newOverlay, "overlay");
        BossBar.Overlay oldOverlay = this.overlay;
        if (newOverlay != oldOverlay) {
            this.overlay = newOverlay;
            this.forEachListener(listener -> listener.bossBarOverlayChanged(this, oldOverlay, newOverlay));
        }
        return this;
    }

    @Override
    @NotNull
    public Set<BossBar.Flag> flags() {
        return Collections.unmodifiableSet(this.flags);
    }

    @Override
    @NotNull
    public BossBar flags(@NotNull Set<BossBar.Flag> newFlags) {
        if (newFlags.isEmpty()) {
            EnumSet<BossBar.Flag> oldFlags = EnumSet.copyOf(this.flags);
            this.flags.clear();
            this.forEachListener(listener -> listener.bossBarFlagsChanged(this, Collections.emptySet(), oldFlags));
        } else if (!this.flags.equals(newFlags)) {
            EnumSet<BossBar.Flag> oldFlags = EnumSet.copyOf(this.flags);
            this.flags.clear();
            this.flags.addAll(newFlags);
            EnumSet<BossBar.Flag> added = EnumSet.copyOf(newFlags);
            added.removeIf(oldFlags::contains);
            EnumSet<BossBar.Flag> removed = EnumSet.copyOf(oldFlags);
            removed.removeIf(this.flags::contains);
            this.forEachListener(listener -> listener.bossBarFlagsChanged(this, added, removed));
        }
        return this;
    }

    @Override
    public boolean hasFlag(@NotNull BossBar.Flag flag) {
        return this.flags.contains((Object)flag);
    }

    @Override
    @NotNull
    public BossBar addFlag(@NotNull BossBar.Flag flag) {
        return this.editFlags(flag, Set::add, FLAGS_ADDED);
    }

    @Override
    @NotNull
    public BossBar removeFlag(@NotNull BossBar.Flag flag) {
        return this.editFlags(flag, Set::remove, FLAGS_REMOVED);
    }

    @NotNull
    private BossBar editFlags(@NotNull BossBar.Flag flag, @NotNull BiPredicate<Set<BossBar.Flag>, BossBar.Flag> predicate, BiConsumer<BossBarImpl, Set<BossBar.Flag>> onChange) {
        if (predicate.test(this.flags, flag)) {
            onChange.accept(this, Collections.singleton(flag));
        }
        return this;
    }

    @Override
    @NotNull
    public BossBar addFlags(BossBar.Flag ... flags) {
        return this.editFlags(flags, Set::add, FLAGS_ADDED);
    }

    @Override
    @NotNull
    public BossBar removeFlags(BossBar.Flag ... flags) {
        return this.editFlags(flags, Set::remove, FLAGS_REMOVED);
    }

    @NotNull
    private BossBar editFlags(BossBar.Flag[] flags, BiPredicate<Set<BossBar.Flag>, BossBar.Flag> predicate, BiConsumer<BossBarImpl, Set<BossBar.Flag>> onChange) {
        if (flags.length == 0) {
            return this;
        }
        EnumSet<BossBar.Flag> changes = null;
        int length = flags.length;
        for (int i = 0; i < length; ++i) {
            if (!predicate.test(this.flags, flags[i])) continue;
            if (changes == null) {
                changes = EnumSet.noneOf(BossBar.Flag.class);
            }
            changes.add(flags[i]);
        }
        if (changes != null) {
            onChange.accept(this, changes);
        }
        return this;
    }

    @Override
    @NotNull
    public BossBar addFlags(@NotNull Iterable<BossBar.Flag> flags) {
        return this.editFlags(flags, Set::add, FLAGS_ADDED);
    }

    @Override
    @NotNull
    public BossBar removeFlags(@NotNull Iterable<BossBar.Flag> flags) {
        return this.editFlags(flags, Set::remove, FLAGS_REMOVED);
    }

    @NotNull
    private BossBar editFlags(Iterable<BossBar.Flag> flags, BiPredicate<Set<BossBar.Flag>, BossBar.Flag> predicate, BiConsumer<BossBarImpl, Set<BossBar.Flag>> onChange) {
        EnumSet<BossBar.Flag> changes = null;
        for (BossBar.Flag flag : flags) {
            if (!predicate.test(this.flags, flag)) continue;
            if (changes == null) {
                changes = EnumSet.noneOf(BossBar.Flag.class);
            }
            changes.add(flag);
        }
        if (changes != null) {
            onChange.accept(this, changes);
        }
        return this;
    }

    @Override
    @NotNull
    public BossBar addListener(@NotNull BossBar.Listener listener) {
        this.listeners.add(listener);
        return this;
    }

    @Override
    @NotNull
    public BossBar removeListener(@NotNull BossBar.Listener listener) {
        this.listeners.remove(listener);
        return this;
    }

    private void forEachListener(@NotNull Consumer<BossBar.Listener> consumer) {
        for (BossBar.Listener listener : this.listeners) {
            consumer.accept(listener);
        }
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("name", this.name), ExaminableProperty.of("progress", this.progress), ExaminableProperty.of("color", (Object)this.color), ExaminableProperty.of("overlay", (Object)this.overlay), ExaminableProperty.of("flags", this.flags));
    }

    public String toString() {
        return this.examine(StringExaminer.simpleEscaping());
    }
}

