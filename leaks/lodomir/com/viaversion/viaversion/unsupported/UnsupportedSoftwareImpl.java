/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.viaversion.viaversion.unsupported;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.unsupported.UnsupportedMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public final class UnsupportedSoftwareImpl
implements UnsupportedSoftware {
    private final String name;
    private final List<String> classNames;
    private final List<UnsupportedMethods> methods;
    private final String reason;

    public UnsupportedSoftwareImpl(String name, List<String> classNames, List<UnsupportedMethods> methods, String reason) {
        this.name = name;
        this.classNames = Collections.unmodifiableList(classNames);
        this.methods = Collections.unmodifiableList(methods);
        this.reason = reason;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public boolean findMatch() {
        for (String className : this.classNames) {
            try {
                Class.forName(className);
                return true;
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
        }
        for (UnsupportedMethods method : this.methods) {
            if (!method.findMatch()) continue;
            return true;
        }
        return false;
    }

    public static final class Reason {
        public static final String DANGEROUS_SERVER_SOFTWARE = "You are using server software that - outside of possibly breaking ViaVersion - can also cause severe damage to your server's integrity as a whole.";
    }

    public static final class Builder {
        private final List<String> classNames = new ArrayList<String>();
        private final List<UnsupportedMethods> methods = new ArrayList<UnsupportedMethods>();
        private String name;
        private String reason;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder addMethod(String className, String methodName) {
            this.methods.add(new UnsupportedMethods(className, Collections.singleton(methodName)));
            return this;
        }

        public Builder addMethods(String className, String ... methodNames) {
            this.methods.add(new UnsupportedMethods(className, new HashSet<String>(Arrays.asList(methodNames))));
            return this;
        }

        public Builder addClassName(String className) {
            this.classNames.add(className);
            return this;
        }

        public UnsupportedSoftware build() {
            Preconditions.checkNotNull((Object)this.name);
            Preconditions.checkNotNull((Object)this.reason);
            return new UnsupportedSoftwareImpl(this.name, this.classNames, this.methods, this.reason);
        }
    }
}

