package com.alan.clients.ui.click.standard.screen;

import com.alan.clients.util.interfaces.InstanceAccess;

public abstract class Screen implements InstanceAccess {
    public Screen() {

    }

    public void onRender(final int mouseX, final int mouseY, final float partialTicks) {
    }

    public void onKey(final char typedChar, final int keyCode) {
    }

    public void onClick(final int mouseX, final int mouseY, final int mouseButton) {
    }

    public void onMouseRelease() {
    }

    public void onBloom() {
    }

    public void onInit() {
    }

    public boolean automaticSearchSwitching() {
        return true;
    }
}
