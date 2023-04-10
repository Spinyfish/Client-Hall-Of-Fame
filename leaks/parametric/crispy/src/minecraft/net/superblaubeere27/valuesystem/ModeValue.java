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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import crispy.util.animation.Translate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ModeValue extends Value<Integer> {
    private String[] modes;
    @Getter
    private final HashMap<NumberValue, Integer> numberComp = new HashMap<>();
    @Getter
    private final HashMap<BooleanValue, Integer> booleanComp = new HashMap<>();
    @Getter
    private final HashMap<ModeValue, Integer> modeComp = new HashMap<>();

    @Getter @Setter
    private boolean extended;

    @Getter
    private final Translate translate = new Translate(0, 0);
    public ModeValue(String name, String defaultVal, String... modes) {
        this(name, defaultVal, null, () -> true, modes);
    }
    public ModeValue(String name, String defaultVal, Supplier<Boolean> visible, String... modes) {
        this(name, defaultVal, null, visible, modes);
    }

    public ModeValue(String name, String defaultVal, Predicate<Integer> validator, Supplier<Boolean> visible, String... modes) {
        super(name, 0, validator, visible);
        this.modes = modes;

        setObject(defaultVal);
    }



    public String[] getModes() {
        return modes;
    }

    public void setObject(String s) {
        int object = -1;

        for (int i = 0; i < modes.length; i++) {
            String mode = modes[i];

            if (mode.equalsIgnoreCase(s)) object = i;
        }
        if (object == -1) throw new IllegalArgumentException("Value '" + object + "' wasn't found");

        setObject(object);
    }

    public String getMode() {
        return modes[getObject()];
    }

    @Override
    public boolean setObject(Integer object) {
        if (object < 0 || modes.length <= object)
            throw new IllegalArgumentException(object + " is not valid (max: " + (modes.length - 1) + ")");

        return super.setObject(object);
    }
    public void cycle() {
        int index = getObject();
        if (index < modes.length - 1) {
            index++;
            setObject(index);
        } else if (index >= modes.length - 1) {
            index = 0;
            setObject(index);
        }
    }
    public void cycleReverse() {
        int index = getObject();
        if (index - 1 >= 0) {
            index--;
            setObject(index);
        } else {
            index = modes.length - 1;
            setObject(index);
        }
    }
    @Override
    public void addToJsonObject( JsonObject obj) {
        obj.addProperty(getName(), getObject());
    }

    @Override
    public void fromJsonObject( JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isNumber()) {
                setObject(element.getAsInt());
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have '" + getName() + "'");
        }
    }

    public void addComponent(NumberValue value, int validator) {
        numberComp.put(value, validator);
    }

    public void addComponent(BooleanValue value, int validator) {
        booleanComp.put(value, validator);
    }
    public void addComponent(ModeValue value, int validator) {
        modeComp.put(value, validator);
    }
}