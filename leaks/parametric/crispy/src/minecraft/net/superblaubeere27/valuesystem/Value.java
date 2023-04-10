/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.superblaubeere27.valuesystem;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Getter;

import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Value<T> {
    private String name;
    private T object;
    private T defaultVal;
    @Getter
    private final Supplier<Boolean> visible;

    /**
     * The validator which is called every time the value is changed
     */
    private Predicate<T> validator;

    Value(String name, T defaultVal, Predicate<T> validator, Supplier<Boolean> visible) {
        this.name = name;
        this.object = defaultVal;
        this.defaultVal = defaultVal;
        this.validator = validator;
        this.visible = visible;
    }

    public abstract void addToJsonObject(JsonObject obj);

    public abstract void fromJsonObject(JsonObject obj);

    public String getName() {
        return name;
    }

    public T getObject() {
        return object;
    }

    public boolean setObject(T object) {
        if (validator != null && !validator.test(object)) return false;

        this.object = object;

        return true;
    }

    public void setValidator(Predicate<T> validator) {
        this.validator = validator;
    }

    public Object getDefault() {
        return defaultVal;
    }

    public boolean isVisible() {
        return visible.get();
    }

}
