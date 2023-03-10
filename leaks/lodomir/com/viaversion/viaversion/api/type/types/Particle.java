/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import java.util.ArrayList;
import java.util.List;

public class Particle {
    private List<ParticleData> arguments = new ArrayList<ParticleData>(4);
    private int id;

    public Particle(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ParticleData> getArguments() {
        return this.arguments;
    }

    @Deprecated
    public void setArguments(List<ParticleData> arguments) {
        this.arguments = arguments;
    }

    public <T> void add(Type<T> type, T value) {
        this.arguments.add(new ParticleData(type, value));
    }

    public static class ParticleData {
        private Type type;
        private Object value;

        public ParticleData(Type type, Object value) {
            this.type = type;
            this.value = value;
        }

        public Type getType() {
            return this.type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public Object getValue() {
            return this.value;
        }

        public <T> T get() {
            return (T)this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}

