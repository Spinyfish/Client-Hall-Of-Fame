package store.femboy.spring.impl.module.settings.impl;

import store.femboy.spring.impl.module.settings.Settings;

public final class KeySetting extends Settings {
    public int key;

    public KeySetting(int keyCode) {
        this.name = "Bind";
        this.key = keyCode;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
