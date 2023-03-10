/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.format;

import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.StyleBuilderApplicable;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecorationAndState;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecorationAndStateImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextFormat;
import com.viaversion.viaversion.libs.kyori.adventure.util.Index;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TextDecoration implements StyleBuilderApplicable,
TextFormat
{
    OBFUSCATED("obfuscated"),
    BOLD("bold"),
    STRIKETHROUGH("strikethrough"),
    UNDERLINED("underlined"),
    ITALIC("italic");

    public static final Index<String, TextDecoration> NAMES;
    private final String name;

    private TextDecoration(String name) {
        this.name = name;
    }

    @NotNull
    public final TextDecorationAndState as(boolean state) {
        return this.as(State.byBoolean(state));
    }

    @NotNull
    public final TextDecorationAndState as(@NotNull State state) {
        return new TextDecorationAndStateImpl(this, state);
    }

    @Override
    public void styleApply(@NotNull Style.Builder style) {
        style.decorate(this);
    }

    @NotNull
    public String toString() {
        return this.name;
    }

    static {
        NAMES = Index.create(TextDecoration.class, constant -> constant.name);
    }

    public static enum State {
        NOT_SET("not_set"),
        FALSE("false"),
        TRUE("true");

        private final String name;

        private State(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @NotNull
        public static State byBoolean(boolean flag) {
            return flag ? TRUE : FALSE;
        }

        @NotNull
        public static State byBoolean(@Nullable Boolean flag) {
            return flag == null ? NOT_SET : State.byBoolean((boolean)flag);
        }
    }
}

