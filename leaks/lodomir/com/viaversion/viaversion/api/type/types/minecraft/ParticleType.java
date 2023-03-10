/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectArrayMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import io.netty.buffer.ByteBuf;

public class ParticleType
extends Type<Particle> {
    private final Int2ObjectMap<ParticleReader> readers;

    public ParticleType(Int2ObjectMap<ParticleReader> readers) {
        super("Particle", Particle.class);
        this.readers = readers;
    }

    public ParticleType() {
        this(new Int2ObjectArrayMap<ParticleReader>());
    }

    public ParticleTypeFiller filler(Protocol<?, ?, ?, ?> protocol) {
        return this.filler(protocol, true);
    }

    public ParticleTypeFiller filler(Protocol<?, ?, ?, ?> protocol, boolean useMappedNames) {
        return new ParticleTypeFiller(protocol, useMappedNames);
    }

    @Override
    public void write(ByteBuf buffer, Particle object) throws Exception {
        Type.VAR_INT.writePrimitive(buffer, object.getId());
        for (Particle.ParticleData data : object.getArguments()) {
            data.getType().write(buffer, data.getValue());
        }
    }

    @Override
    public Particle read(ByteBuf buffer) throws Exception {
        int type = Type.VAR_INT.readPrimitive(buffer);
        Particle particle = new Particle(type);
        ParticleReader reader = (ParticleReader)this.readers.get(type);
        if (reader != null) {
            reader.read(buffer, particle);
        }
        return particle;
    }

    public static ParticleReader itemHandler(Type<Item> itemType) {
        return (buf, particle) -> particle.add(itemType, (Item)itemType.read(buf));
    }

    @FunctionalInterface
    public static interface ParticleReader {
        public void read(ByteBuf var1, Particle var2) throws Exception;
    }

    public final class ParticleTypeFiller {
        private final ParticleMappings mappings;
        private final boolean useMappedNames;

        private ParticleTypeFiller(Protocol<?, ?, ?, ?> protocol, boolean useMappedNames) {
            this.mappings = protocol.getMappingData().getParticleMappings();
            this.useMappedNames = useMappedNames;
        }

        public ParticleTypeFiller reader(String identifier, ParticleReader reader) {
            ParticleType.this.readers.put(this.useMappedNames ? this.mappings.mappedId(identifier) : this.mappings.id(identifier), reader);
            return this;
        }

        public ParticleTypeFiller reader(int id, ParticleReader reader) {
            ParticleType.this.readers.put(id, reader);
            return this;
        }
    }

    public static final class Readers {
        public static final ParticleReader BLOCK = (buf, particle) -> particle.add(Type.VAR_INT, Type.VAR_INT.readPrimitive(buf));
        public static final ParticleReader ITEM = ParticleType.itemHandler(Type.FLAT_ITEM);
        public static final ParticleReader VAR_INT_ITEM = ParticleType.itemHandler(Type.FLAT_VAR_INT_ITEM);
        public static final ParticleReader DUST = (buf, particle) -> {
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
        };
        public static final ParticleReader DUST_TRANSITION = (buf, particle) -> {
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
            particle.add(Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf)));
        };
        public static final ParticleReader VIBRATION = (buf, particle) -> {
            particle.add(Type.POSITION1_14, (Position)Type.POSITION1_14.read(buf));
            String resourceLocation = (String)Type.STRING.read(buf);
            particle.add(Type.STRING, resourceLocation);
            if (resourceLocation.startsWith("minecraft:")) {
                resourceLocation = resourceLocation.substring(10);
            }
            if (resourceLocation.equals("block")) {
                particle.add(Type.POSITION1_14, (Position)Type.POSITION1_14.read(buf));
            } else if (resourceLocation.equals("entity")) {
                particle.add(Type.VAR_INT, Type.VAR_INT.readPrimitive(buf));
            } else {
                Via.getPlatform().getLogger().warning("Unknown vibration path position source type: " + resourceLocation);
            }
            particle.add(Type.VAR_INT, Type.VAR_INT.readPrimitive(buf));
        };
    }
}

