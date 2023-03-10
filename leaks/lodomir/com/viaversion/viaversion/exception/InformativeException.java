/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.exception;

import java.util.HashMap;
import java.util.Map;

public class InformativeException
extends Exception {
    private final Map<String, Object> info = new HashMap<String, Object>();
    private int sources;

    public InformativeException(Throwable cause) {
        super(cause);
    }

    public InformativeException set(String key, Object value) {
        this.info.put(key, value);
        return this;
    }

    public InformativeException addSource(Class<?> sourceClazz) {
        return this.set("Source " + this.sources++, this.getSource(sourceClazz));
    }

    private String getSource(Class<?> sourceClazz) {
        return sourceClazz.isAnonymousClass() ? sourceClazz.getName() + " (Anonymous)" : sourceClazz.getName();
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder("Please post this error to https://github.com/ViaVersion/ViaVersion/issues and follow the issue template\n{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : this.info.entrySet()) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(entry.getKey()).append(": ").append(entry.getValue());
            first = false;
        }
        return builder.append("}\nActual Error: ").toString();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

