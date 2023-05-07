package client.module;

import client.Client;
import client.util.interfaces.InstanceAccess;
import client.value.Value;
import client.value.impl.BooleanValue;
import client.value.impl.ModeValue;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.atteo.classindex.IndexSubclasses;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@IndexSubclasses
public abstract class Module implements InstanceAccess {

    private final String displayName;
    private final List<Value<?>> values = new ArrayList<>();
    private ModuleInfo moduleInfo;
    private boolean hidden;
    private boolean enabled;
    private int keyIndex;

    public Module() {
        if (this.getClass().isAnnotationPresent(ModuleInfo.class)) {
            this.moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);

            this.displayName = this.moduleInfo.name().replace(" ", "");
            this.keyIndex = this.moduleInfo.keyIndex();
            this.hidden = moduleInfo.hidden();
        } else {
            throw new RuntimeException("ModuleInfo annotation not found on " + this.getClass().getSimpleName());
        }
    }

    public Module(final ModuleInfo info) {
        this.moduleInfo = info;

        this.displayName = String.join(" ", StringUtils.splitByCharacterTypeCamelCase(this.moduleInfo.name()));
        this.keyIndex = this.moduleInfo.keyIndex();
    }

    public void toggle() {
        this.setEnabled(!enabled);
    }

    public void setEnabled(final boolean enabled) {
        if (this.enabled == enabled || (!this.moduleInfo.allowDisable() && !enabled)) {
            return;
        }

        this.enabled = enabled;

        if (enabled) {
            superEnable();
        } else {
            superDisable();
        }
    }

    public final void superEnable() {
        Client.INSTANCE.getEventBus().register(this);

        this.values.stream()
                .filter(value -> value instanceof ModeValue)
                .forEach(value -> ((ModeValue) value).getValue().register());

        this.values.stream()
                .filter(value -> value instanceof BooleanValue)
                .forEach(value -> {
                    final BooleanValue booleanValue = (BooleanValue) value;
                    if (booleanValue.getMode() != null && booleanValue.getValue()) {
                        booleanValue.getMode().register();
                    }
                });

        if (mc.thePlayer != null) this.onEnable();
    }

    public final void superDisable() {
        Client.INSTANCE.getEventBus().unregister(this);

        this.values.stream()
                .filter(value -> value instanceof ModeValue)
                .forEach(value -> ((ModeValue) value).getValue().unregister());

        this.values.stream()
                .filter(value -> value instanceof BooleanValue)
                .forEach(value -> {
                    final BooleanValue booleanValue = (BooleanValue) value;
                    if (booleanValue.getMode() != null) {
                        booleanValue.getMode().unregister();
                    }
                });

        if (mc.thePlayer != null) this.onDisable();
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public List<Value<?>> getAllValues() {
        ArrayList<Value<?>> allValues = new ArrayList<>();

        values.forEach(value -> {
            List<Value<?>> subValues = value.getSubValues();

            allValues.add(value);

            if (subValues != null) {
                allValues.addAll(subValues);
            }
        });

        return allValues;
    }
}
