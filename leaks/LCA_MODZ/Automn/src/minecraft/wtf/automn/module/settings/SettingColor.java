package wtf.automn.module.settings;

import wtf.automn.module.Module;

public class SettingColor extends Setting<Number> {

    public SettingColor(final String id, final Integer value, final String display, Module parent, final String description) {
        super(id, value, display, parent, description);
        parent.getSettings().add(this);
    }

    @Override
    public Integer getTrueValue() {
        if (this.value instanceof Double) {
            final Double d = (Double) this.value;
            return (int) Math.round(d);
        } else if (this.value instanceof Integer) {
            final Integer i = (Integer) this.value;
            return Math.round(i);
        } else return 1;
    }

}
