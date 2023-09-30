/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.thealtening.api.response;

import com.google.gson.annotations.SerializedName;

public class AccountDetails {
    @SerializedName(value="hypixel.lvl")
    private int hypixelLevel;
    @SerializedName(value="hypixel.rank")
    private String hypixelRank;
    @SerializedName(value="mineplex.lvl")
    private int mineplexLevel;
    @SerializedName(value="mineplex.rank")
    private String mineplexRank;
    @SerializedName(value="labymod.cape")
    private boolean labymodCape;
    @SerializedName(value="5zig.cape")
    private boolean fiveZigCape;

    public int getHypixelLevel() {
        return this.hypixelLevel;
    }

    public String getHypixelRank() {
        return this.hypixelRank;
    }

    public int getMineplexLevel() {
        return this.mineplexLevel;
    }

    public String getMineplexRank() {
        return this.mineplexRank;
    }

    public boolean hasLabyModCape() {
        return this.labymodCape;
    }

    public boolean hasFiveZigCape() {
        return this.fiveZigCape;
    }

    public String toString() {
        return String.format("AccountDetails[%s:%s:%s:%s:%s:%s]", this.hypixelLevel, this.hypixelRank, this.mineplexLevel, this.mineplexRank, this.labymodCape, this.fiveZigCape);
    }
}

