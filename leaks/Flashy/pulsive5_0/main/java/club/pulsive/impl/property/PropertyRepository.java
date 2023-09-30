package club.pulsive.impl.property;

import java.lang.reflect.Field;
import java.util.*;

public class PropertyRepository {

    private final Map<Object, List<Property>> PROPERTIES = new HashMap<>();

    public void register(Object object) {
        Class clazz = object.getClass();
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (Property.class.isAssignableFrom(declaredField.getType())) {
                if (!declaredField.isAccessible()) declaredField.setAccessible(true);
                try {
                    if (PROPERTIES.containsKey(clazz)) {
                        List<Property> properties = new ArrayList<>(PROPERTIES.get(clazz));
                        properties.add((Property) declaredField.get(object));
                        PROPERTIES.put(clazz, properties);
                    } else PROPERTIES.put(clazz, Collections.singletonList((Property) declaredField.get(object)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public <T extends Property> T propertyBy(Class owner, String name) {
        return (T) PROPERTIES.get(owner).stream().filter(property -> property.getLabel().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Property> propertiesBy(Class owner) {
        return PROPERTIES.get(owner) == null ? new ArrayList<>() : PROPERTIES.get(owner);
    }

    public Map<Object, List<Property>> properties() {
        return PROPERTIES;
    }
}
