package net.optifine.reflect;

import net.minecraft.src.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FieldLocatorTypes implements IFieldLocator {
    private Field field = null;

    public FieldLocatorTypes(final Class cls, final Class[] preTypes, final Class type, final Class[] postTypes, final String errorName) {
        final Field[] afield = cls.getDeclaredFields();
        final List<Class> list = new ArrayList();

        for (int i = 0; i < afield.length; ++i) {
            final Field field = afield[i];
            list.add(field.getType());
        }

        final List<Class> list1 = new ArrayList();
        list1.addAll(Arrays.asList(preTypes));
        list1.add(type);
        list1.addAll(Arrays.asList(postTypes));
        final int l = Collections.indexOfSubList(list, list1);

        if (l < 0) {
            Config.log("(Reflector) Field not found: " + errorName);
        } else {
            final int j = Collections.indexOfSubList(list.subList(l + 1, list.size()), list1);

            if (j >= 0) {
                Config.log("(Reflector) More than one match found for field: " + errorName);
            } else {
                final int k = l + preTypes.length;
                this.field = afield[k];
            }
        }
    }

    public Field getField() {
        return this.field;
    }
}
