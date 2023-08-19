package dev.lemon.api.module;

import dev.lemon.client.Lemon;
import dev.lemon.api.util.IWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Module implements IWrapper {
    private final String name, description;
    private final ModuleCategory category;
    private int key;
    @Setter(AccessLevel.NONE)
    private boolean enabled;

    protected void onEnable() {
        Lemon.INSTANCE.getBus().subscribe(this);
    }

    protected void onDisable() {
        Lemon.INSTANCE.getBus().unsubscribe(this);
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
        this.enabled = !enabled;
    }
}
