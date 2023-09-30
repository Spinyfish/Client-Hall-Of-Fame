package wtf.automn.module.settings;

import com.google.gson.annotations.Expose;
import wtf.automn.module.Module;

import java.util.ArrayList;
import java.util.Arrays;

public class Setting<T> {

    @Expose
    public String id;
    @Expose
    public T value;

    public String display, description;

    public boolean visible = true;

    private Module module;

    public Setting(final String id, final T value, final String display, final Module parent, final String description) {
        this.id = id;
        this.value = value;
        this.display = display;
        this.description = description;
        this.module = parent;
    }

    public static final ArrayList empty() {
        return new ArrayList(Arrays.asList());
    }

    public T getTrueValue() {
        return this.value;
    }

}
