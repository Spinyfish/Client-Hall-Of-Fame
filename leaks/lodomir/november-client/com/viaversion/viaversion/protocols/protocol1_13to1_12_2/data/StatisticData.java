/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

public class StatisticData {
    private final int categoryId;
    private final int newId;
    private final int value;

    public StatisticData(int categoryId, int newId, int value) {
        this.categoryId = categoryId;
        this.newId = newId;
        this.value = value;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    public int getNewId() {
        return this.newId;
    }

    public int getValue() {
        return this.value;
    }
}

