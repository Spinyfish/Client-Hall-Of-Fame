package de.lirium.base.setting;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dependency<T> {
    public final ISetting<?> setting;
    public final List<T> neededValues = new ArrayList<>();

    @SafeVarargs
    public Dependency(ISetting<?> setting, T... neededValue) {
        this.setting = setting;
        this.neededValues.addAll(Arrays.asList(neededValue));
    }
}
