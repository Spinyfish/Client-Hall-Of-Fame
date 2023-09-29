package de.lirium.base.setting;

import com.github.drapostolos.typeparser.TypeParser;
import de.lirium.impl.module.ModuleFeature;
import god.buddy.aot.BCompiler;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
public class ISetting<T> {
    private final ArrayList<Dependency<?>> dependencies = new ArrayList<>();
    private final ArrayList<ChangeListener<T>> changeListeners = new ArrayList<>();
    public T value;
    public T defaultValue;

    private boolean visual;

    protected ISetting(T value, Dependency<?>... dependencies) {
        this.defaultValue = value;
        this.value = value;
        this.dependencies.addAll(Arrays.asList(dependencies));
    }

    protected ISetting(T value) {
        this.defaultValue = value;
        this.value = value;
    }

    protected ISetting(Dependency<?>... dependencies) {
        this.dependencies.addAll(Arrays.asList(dependencies));
    }

    private ModuleFeature module;
    public String name;
    private String displayName;

    public boolean isVisible() {
        if (!dependencies.isEmpty()) {
            for (Dependency<?> dependency : dependencies) {
                if (!dependency.setting.isVisible())
                    return false;
                boolean anyMatch = false;
                for(Object value : dependency.neededValues) {
                    if(dependency.setting.value.equals(value)) {
                        anyMatch = true;
                        break;
                    }
                }
                if(!anyMatch)
                    return false;
            }
        }
        return true;
    }

    public HashMap<String, Object> getFields() {
        final HashMap<String, Object> fields = new HashMap<>();
        Arrays.stream(this.getClass().getFields()).forEach(field -> {
            try {
                field.setAccessible(true);
                final Object value = field.get(this);
                fields.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return fields;
    }

    public Object getField(String name) {
        final AtomicReference<Object> returnValue = new AtomicReference<>(null);
        getFields().forEach((field, value) -> {
            if (field.equalsIgnoreCase(name)) {
                returnValue.set(value);
            }
        });
        return returnValue.get();
    }

    public void tryChangeField(String name, String value) {
        final T changed = castString(name, value);
        if (changed != null)
            changeField(name, changed);
        else {
            System.out.println("Cant change " + name + " to " + value);
            System.out.println(getFields());
        }
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private T castString(String name, String value) {
        AtomicReference<T> returnValue = new AtomicReference<>(null);
        getFields().forEach((fieldName, current) -> {
            if (fieldName.equalsIgnoreCase(name)) {
                try {
                    final Field field = this.getClass().getField(fieldName);
                    field.setAccessible(true);
                    final Object setting = field.get(this);
                    final Class<?> clazz = Class.forName(setting.getClass().getTypeName());
                    final TypeParser parser = TypeParser.newBuilder().build();
                    returnValue.set((T) parser.parse(value, clazz));
                } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return returnValue.get();
    }

    public void changeField(String name, T value) {
        getFields().forEach((fieldName, current) -> {
            if (fieldName.equalsIgnoreCase(name)) {
                try {
                    final Field field = this.getClass().getField(fieldName);
                    field.setAccessible(true);
                    field.set(this, value);
                    this.callChangeListeners();
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getDisplay() {
        int index = 0;
        for (ISetting<?> setting : SettingRegistry.getValues().get(module)) {
            if (setting == this)
                break;
            if (!setting.isVisible())
                continue;
            ;
            if (setting.getName().equalsIgnoreCase(getName()))
                index++;
        }
        return getName() + (index > 0 ? "$" + index : "");
    }

    public String getName() {
        return this.displayName.equals("") ? this.name : this.displayName;
    }

    private void callChangeListeners() {
        for (final ChangeListener<T> listener : changeListeners)
            listener.onChange(this);
    }

    public void reset() {
        this.value = this.defaultValue;
    }

    public void setValue(T value) {
        this.value = value;
        this.callChangeListeners();
    }

    public ISetting<T> onChange(ChangeListener<T> listener) {
        changeListeners.add(listener);
        return this;
    }

    public void depend(Dependency<?> dependency) {
        this.dependencies.add(dependency);
    }

    public interface ChangeListener<T> {
        void onChange(ISetting<T> setting);
    }
}
