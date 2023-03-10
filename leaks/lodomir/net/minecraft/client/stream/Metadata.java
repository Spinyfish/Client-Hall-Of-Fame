/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 */
package net.minecraft.client.stream;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import java.util.Map;

public class Metadata {
    private static final Gson field_152811_a = new Gson();
    private final String name;
    private String description;
    private Map<String, String> payload;

    public Metadata(String p_i46345_1_, String p_i46345_2_) {
        this.name = p_i46345_1_;
        this.description = p_i46345_2_;
    }

    public Metadata(String p_i1030_1_) {
        this(p_i1030_1_, null);
    }

    public void func_152807_a(String p_152807_1_) {
        this.description = p_152807_1_;
    }

    public String func_152809_a() {
        return this.description == null ? this.name : this.description;
    }

    public void func_152808_a(String p_152808_1_, String p_152808_2_) {
        if (this.payload == null) {
            this.payload = Maps.newHashMap();
        }
        if (this.payload.size() > 50) {
            throw new IllegalArgumentException("Metadata payload is full, cannot add more to it!");
        }
        if (p_152808_1_ == null) {
            throw new IllegalArgumentException("Metadata payload key cannot be null!");
        }
        if (p_152808_1_.length() > 255) {
            throw new IllegalArgumentException("Metadata payload key is too long!");
        }
        if (p_152808_2_ == null) {
            throw new IllegalArgumentException("Metadata payload value cannot be null!");
        }
        if (p_152808_2_.length() > 255) {
            throw new IllegalArgumentException("Metadata payload value is too long!");
        }
        this.payload.put(p_152808_1_, p_152808_2_);
    }

    public String func_152806_b() {
        return this.payload != null && !this.payload.isEmpty() ? field_152811_a.toJson(this.payload) : null;
    }

    public String func_152810_c() {
        return this.name;
    }

    public String toString() {
        return Objects.toStringHelper((Object)this).add("name", (Object)this.name).add("description", (Object)this.description).add("data", (Object)this.func_152806_b()).toString();
    }
}

