package dev.thread.api.module;

import dev.thread.api.setting.Setting;
import dev.thread.api.setting.impl.KeybindSetting;
import dev.thread.client.Thread;
import dev.thread.api.util.IWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Module implements IWrapper {
    private final List<Setting<?>> settings = new ArrayList<>();
    private final String name, description;
    private final ModuleCategory category;
    private Setting<KeybindSetting> key = new Setting<>("Key", "Keybind for the module", new KeybindSetting(0));
    @Setter(AccessLevel.NONE)
    private boolean enabled;

    protected void onEnable() {
        Thread.INSTANCE.getBus().subscribe(this);
    }

    protected void onDisable() {
        Thread.INSTANCE.getBus().unsubscribe(this);
    }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }
}
