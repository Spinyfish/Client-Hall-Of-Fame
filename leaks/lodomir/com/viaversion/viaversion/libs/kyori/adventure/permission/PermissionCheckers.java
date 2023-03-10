/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.permission;

import com.viaversion.viaversion.libs.kyori.adventure.permission.PermissionChecker;
import com.viaversion.viaversion.libs.kyori.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PermissionCheckers {
    static final PermissionChecker NOT_SET = new Always(TriState.NOT_SET);
    static final PermissionChecker FALSE = new Always(TriState.FALSE);
    static final PermissionChecker TRUE = new Always(TriState.TRUE);

    private PermissionCheckers() {
    }

    private static final class Always
    implements PermissionChecker {
        private final TriState value;

        private Always(TriState value) {
            this.value = value;
        }

        @Override
        @NotNull
        public TriState value(String permission) {
            return this.value;
        }

        public String toString() {
            return PermissionChecker.class.getSimpleName() + ".always(" + (Object)((Object)this.value) + ")";
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            Always always = (Always)other;
            return this.value == always.value;
        }

        public int hashCode() {
            return this.value.hashCode();
        }
    }
}

