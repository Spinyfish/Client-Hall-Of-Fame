/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.module.value.Value
 */
package vip.astroline.client.service.module.value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import vip.astroline.client.service.module.value.Value;

public class ValueManager {
    private static final ArrayList<Value> values = new ArrayList();
    public static final ArrayList<Value> apiValues = new ArrayList();

    public static void addValue(Value value) {
        values.add(value);
    }

    public static void removeValue(Value value) {
        values.remove(value);
    }

    public static ArrayList<Value> getValues() {
        ArrayList<Value> list = new ArrayList<Value>(values);
        list.addAll(apiValues);
        return list;
    }

    public static Value getValue(String name) {
        Value value;
        Iterator<Value> iterator = ValueManager.getValues().iterator();
        do {
            if (!iterator.hasNext()) return null;
        } while (!(value = iterator.next()).getKey().equalsIgnoreCase(name));
        return value;
    }

    public static List<Value> getValueByModName(String modName) {
        ArrayList<Value> result = new ArrayList<Value>();
        Iterator<Value> iterator = ValueManager.getValues().iterator();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            if (!value.getGroup().equalsIgnoreCase(modName)) continue;
            result.add(value);
        }
        return result;
    }

    public static Value getValueByGroupAndKey(String group, String key) {
        Value value;
        Iterator<Value> iterator = ValueManager.getValues().iterator();
        do {
            if (!iterator.hasNext()) return null;
        } while (!(value = iterator.next()).getGroup().equalsIgnoreCase(group) || !value.getKey().equals(key));
        return value;
    }

    public static List<Value> getValueByModNameForRender(String modName) {
        ArrayList<Value> result = new ArrayList<Value>();
        Iterator<Value> iterator = ValueManager.getValues().iterator();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            if (!value.getGroup().equals(modName)) continue;
            result.add(value);
        }
        return result;
    }
}
