package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class EnumTypeAdapterFactory implements TypeAdapterFactory {
    public <T> TypeAdapter<T> create(final Gson p_create_1_, final TypeToken<T> p_create_2_) {
        final Class<T> oclass = (Class<T>) p_create_2_.getRawType();

        if (!oclass.isEnum()) {
            return null;
        } else {
            final Map<String, T> map = Maps.newHashMap();

            for (final T t : oclass.getEnumConstants()) {
                map.put(this.func_151232_a(t), t);
            }

            return new TypeAdapter<T>() {
                public void write(final JsonWriter p_write_1_, final T p_write_2_) throws IOException {
                    if (p_write_2_ == null) {
                        p_write_1_.nullValue();
                    } else {
                        p_write_1_.value(EnumTypeAdapterFactory.this.func_151232_a(p_write_2_));
                    }
                }

                public T read(final JsonReader p_read_1_) throws IOException {
                    if (p_read_1_.peek() == JsonToken.NULL) {
                        p_read_1_.nextNull();
                        return null;
                    } else {
                        return map.get(p_read_1_.nextString());
                    }
                }
            };
        }
    }

    private String func_151232_a(final Object p_151232_1_) {
        return p_151232_1_ instanceof Enum ? ((Enum) p_151232_1_).name().toLowerCase(Locale.US) : p_151232_1_.toString().toLowerCase(Locale.US);
    }
}
