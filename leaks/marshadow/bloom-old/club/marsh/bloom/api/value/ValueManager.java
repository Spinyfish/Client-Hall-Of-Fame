package club.marsh.bloom.api.value;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValueManager {
    public ConcurrentHashMap<String,List<Value>> values = new ConcurrentHashMap<>();

    public List<Value> getAllValuesFrom(String name) {
        for (Map.Entry<String, List<Value>> stringListEntry : values.entrySet()) {
            if (stringListEntry.getKey().equalsIgnoreCase(name)) return stringListEntry.getValue();
        }
        return null;
    }


    public ConcurrentHashMap<String, List<Value>> getAllValues() {
        return values;
    }

    public void register(String string, Object object) {
        List<Value> valsues = new ArrayList<>();
        for (final Field field : object.getClass().getDeclaredFields()) {
            if (field.getType().equals(Value.class) || field.getType().equals(BooleanValue.class) || field.getType().equals(ModeValue.class) || field.getType().equals(NumberValue.class)) {
                field.setAccessible(true);
                try {
                    Value value = (Value) field.get(object);
                    valsues.add(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        values.put(string,valsues);
    }

}
