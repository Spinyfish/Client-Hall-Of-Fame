/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.src.Config;
import net.optifine.reflect.IFieldLocator;

public class FieldLocatorTypes
implements IFieldLocator {
    private Field field = null;

    public FieldLocatorTypes(Class cls, Class[] preTypes, Class type, Class[] postTypes, String errorName) {
        Field[] afield = cls.getDeclaredFields();
        ArrayList list = new ArrayList();
        for (int i = 0; i < afield.length; ++i) {
            Field field = afield[i];
            list.add(field.getType());
        }
        ArrayList<Class> list1 = new ArrayList<Class>();
        list1.addAll(Arrays.asList(preTypes));
        list1.add(type);
        list1.addAll(Arrays.asList(postTypes));
        int l = Collections.indexOfSubList(list, list1);
        if (l < 0) {
            Config.log("(Reflector) Field not found: " + errorName);
        } else {
            int j = Collections.indexOfSubList(list.subList(l + 1, list.size()), list1);
            if (j >= 0) {
                Config.log("(Reflector) More than one match found for field: " + errorName);
            } else {
                int k = l + preTypes.length;
                this.field = afield[k];
            }
        }
    }

    @Override
    public Field getField() {
        return this.field;
    }
}

