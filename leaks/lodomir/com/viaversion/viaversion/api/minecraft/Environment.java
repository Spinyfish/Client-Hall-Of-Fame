/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft;

public enum Environment {
    NORMAL(0),
    NETHER(-1),
    END(1);

    private final int id;

    private Environment(int id) {
        this.id = id;
    }

    public int id() {
        return this.id;
    }

    @Deprecated
    public int getId() {
        return this.id;
    }

    public static Environment getEnvironmentById(int id) {
        switch (id) {
            default: {
                return NETHER;
            }
            case 0: {
                return NORMAL;
            }
            case 1: 
        }
        return END;
    }
}

