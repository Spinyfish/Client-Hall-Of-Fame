/**
 * @project Myth
 * @author CodeMan
 * @at 20.08.22, 14:13
 */
package dev.myth.settings;

import dev.myth.api.feature.Feature;
import dev.myth.api.setting.Setting;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ListSetting<T extends Enum<T>> extends Setting<ArrayList<T>>  {

    @Getter public T[] addons;

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public ListSetting(String name, T... values) {
        super(name, new ArrayList<>(Arrays.asList(values)));

        if(values.length == 0)
            throw new RuntimeException("Need at least one default value.");

        addons = (T[]) getValue().get(0).getClass().getEnumConstants();
    }

    public ListSetting<T> setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ListSetting<T> addDependency(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }

    public boolean is(T value) {
        return getValue().contains(value);
    }

    public boolean isEnabled(String mode) {
        return getValue().stream().anyMatch(addon -> addon.toString().equalsIgnoreCase(mode));
    }

    public void toggle(String selected) {
        T addon = getAddonByName(selected);
        if(addon == null) return;

        if(!isEnabled(selected)) {
            getValue().add(addon);
        } else {
            getValue().remove(addon);
        }
    }

    public void setValueByIndex(int index) {
        if(index < 0 || index >= addons.length) return;

        toggle(addons[index].toString());
    }

    public ArrayList<Enum<?>> getSelected() {
        ArrayList<Enum<?>> selected = new ArrayList<>();
        for(Enum<?> addon : addons) {
            if(getValue().contains(addon)) {
                selected.add(addon);
            }
        }
        return selected;
    }

    public T getAddonByName(String name) {
        return Arrays.stream(addons).filter(addon -> addon.toString().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}