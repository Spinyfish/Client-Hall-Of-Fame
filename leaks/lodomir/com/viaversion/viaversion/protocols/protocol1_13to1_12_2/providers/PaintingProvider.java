/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers;

import com.viaversion.viaversion.api.platform.providers.Provider;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class PaintingProvider
implements Provider {
    private final Map<String, Integer> paintings = new HashMap<String, Integer>();

    public PaintingProvider() {
        this.add("kebab");
        this.add("aztec");
        this.add("alban");
        this.add("aztec2");
        this.add("bomb");
        this.add("plant");
        this.add("wasteland");
        this.add("pool");
        this.add("courbet");
        this.add("sea");
        this.add("sunset");
        this.add("creebet");
        this.add("wanderer");
        this.add("graham");
        this.add("match");
        this.add("bust");
        this.add("stage");
        this.add("void");
        this.add("skullandroses");
        this.add("wither");
        this.add("fighters");
        this.add("pointer");
        this.add("pigscene");
        this.add("burningskull");
        this.add("skeleton");
        this.add("donkeykong");
    }

    private void add(String motive) {
        this.paintings.put("minecraft:" + motive, this.paintings.size());
    }

    public Optional<Integer> getIntByIdentifier(String motive) {
        if (!motive.startsWith("minecraft:")) {
            motive = "minecraft:" + motive.toLowerCase(Locale.ROOT);
        }
        return Optional.ofNullable(this.paintings.get(motive));
    }
}

