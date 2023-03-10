/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.examination;

import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import com.viaversion.viaversion.libs.kyori.examination.Examiner;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractExaminer<R>
implements Examiner<R> {
    @Override
    @NotNull
    public R examine(@Nullable Object value) {
        if (value == null) {
            return this.nil();
        }
        if (value instanceof String) {
            return this.examine((String)value);
        }
        if (value instanceof Examinable) {
            return this.examine((Examinable)value);
        }
        if (value instanceof Collection) {
            return this.collection((Collection)value);
        }
        if (value instanceof Map) {
            return this.map((Map)value);
        }
        if (value.getClass().isArray()) {
            Class<?> type = value.getClass().getComponentType();
            if (type.isPrimitive()) {
                if (type == Boolean.TYPE) {
                    return this.examine((boolean[])value);
                }
                if (type == Byte.TYPE) {
                    return this.examine((byte[])value);
                }
                if (type == Character.TYPE) {
                    return this.examine((char[])value);
                }
                if (type == Double.TYPE) {
                    return this.examine((double[])value);
                }
                if (type == Float.TYPE) {
                    return this.examine((float[])value);
                }
                if (type == Integer.TYPE) {
                    return this.examine((int[])value);
                }
                if (type == Long.TYPE) {
                    return this.examine((long[])value);
                }
                if (type == Short.TYPE) {
                    return this.examine((short[])value);
                }
            }
            return this.array((Object[])value);
        }
        if (value instanceof Boolean) {
            return this.examine((Boolean)value);
        }
        if (value instanceof Character) {
            return this.examine(((Character)value).charValue());
        }
        if (value instanceof Number) {
            if (value instanceof Byte) {
                return this.examine((Byte)value);
            }
            if (value instanceof Double) {
                return this.examine((Double)value);
            }
            if (value instanceof Float) {
                return this.examine(((Float)value).floatValue());
            }
            if (value instanceof Integer) {
                return this.examine((Integer)value);
            }
            if (value instanceof Long) {
                return this.examine((Long)value);
            }
            if (value instanceof Short) {
                return this.examine((Short)value);
            }
        } else if (value instanceof BaseStream) {
            if (value instanceof Stream) {
                return this.stream((Stream)value);
            }
            if (value instanceof DoubleStream) {
                return this.stream((DoubleStream)value);
            }
            if (value instanceof IntStream) {
                return this.stream((IntStream)value);
            }
            if (value instanceof LongStream) {
                return this.stream((LongStream)value);
            }
        }
        return this.scalar(value);
    }

    @NotNull
    private <E> R array(E @NotNull [] array) {
        return (R)this.array(array, Arrays.stream(array).map(this::examine));
    }

    @NotNull
    protected abstract <E> R array(E @NotNull [] var1, @NotNull Stream<R> var2);

    @NotNull
    private <E> R collection(@NotNull Collection<E> collection) {
        return (R)this.collection(collection, collection.stream().map(this::examine));
    }

    @NotNull
    protected abstract <E> R collection(@NotNull Collection<E> var1, @NotNull Stream<R> var2);

    @Override
    @NotNull
    public R examine(@NotNull String name, @NotNull Stream<? extends ExaminableProperty> properties) {
        return this.examinable(name, properties.map((? super T property) -> new AbstractMap.SimpleImmutableEntry(property.name(), property.examine(this))));
    }

    @NotNull
    protected abstract R examinable(@NotNull String var1, @NotNull Stream<Map.Entry<String, R>> var2);

    @NotNull
    private <K, V> R map(@NotNull Map<K, V> map) {
        return this.map(map, map.entrySet().stream().map((? super T entry) -> new AbstractMap.SimpleImmutableEntry<R, R>(this.examine(entry.getKey()), this.examine(entry.getValue()))));
    }

    @NotNull
    protected abstract <K, V> R map(@NotNull Map<K, V> var1, @NotNull Stream<Map.Entry<R, R>> var2);

    @NotNull
    protected abstract R nil();

    @NotNull
    protected abstract R scalar(@NotNull Object var1);

    @NotNull
    protected abstract <T> R stream(@NotNull Stream<T> var1);

    @NotNull
    protected abstract R stream(@NotNull DoubleStream var1);

    @NotNull
    protected abstract R stream(@NotNull IntStream var1);

    @NotNull
    protected abstract R stream(@NotNull LongStream var1);

    @Override
    @NotNull
    public R examine(boolean @Nullable [] values) {
        if (values == null) {
            return this.nil();
        }
        return (R)this.array(values.length, (int index) -> this.examine(values[index]));
    }

    @Override
    @NotNull
    public R examine(byte @Nullable [] values) {
        if (values == null) {
            return this.nil();
        }
        return (R)this.array(values.length, (int index) -> this.examine(values[index]));
    }

    @Override
    @NotNull
    public R examine(char @Nullable [] values) {
        if (values == null) {
            return this.nil();
        }
        return (R)this.array(values.length, (int index) -> this.examine(values[index]));
    }

    @Override
    @NotNull
    public R examine(double @Nullable [] values) {
        if (values == null) {
            return this.nil();
        }
        return (R)this.array(values.length, (int index) -> this.examine(values[index]));
    }

    @Override
    @NotNull
    public R examine(float @Nullable [] values) {
        if (values == null) {
            return this.nil();
        }
        return (R)this.array(values.length, (int index) -> this.examine(values[index]));
    }

    @Override
    @NotNull
    public R examine(int @Nullable [] values) {
        if (values == null) {
            return this.nil();
        }
        return (R)this.array(values.length, (int index) -> this.examine(values[index]));
    }

    @Override
    @NotNull
    public R examine(long @Nullable [] values) {
        if (values == null) {
            return this.nil();
        }
        return (R)this.array(values.length, (int index) -> this.examine(values[index]));
    }

    @Override
    @NotNull
    public R examine(short @Nullable [] values) {
        if (values == null) {
            return this.nil();
        }
        return (R)this.array(values.length, (int index) -> this.examine(values[index]));
    }

    @NotNull
    protected abstract R array(int var1, IntFunction<R> var2);
}

