package me.jinthium.straight.api.setting;

import me.jinthium.straight.impl.settings.BooleanSetting;

import java.util.function.Predicate;

public class ParentAttribute<T extends Setting> {

    public final static Predicate<BooleanSetting> BOOLEAN_CONDITION = BooleanSetting::isEnabled;

    private final T parent;
    private final Predicate<T> condition;

    public ParentAttribute(T parent, Predicate<T> condition) {
        this.parent = parent;
        this.condition = condition;
    }

    public boolean isValid() {
        return condition.test(parent) && parent.getParents().stream().allMatch(ParentAttribute::isValid);
    }

    public T getParent() {
        return parent;
    }

}