package de.fanta.setting.settings;

public class TextField{
    public boolean focused = false;

    public TextField(boolean state){ this.focused = focused; }

    public boolean isFocused() {
        return focused;
    }

    public void setState(boolean focused) {
        this.focused = focused;
    }
}
