package alos.stella.event.events;

import alos.stella.event.Event;

public final class ActionEvent extends Event {
    private boolean sprinting;
    private boolean sneaking;

    public final boolean getSprinting() {
        return this.sprinting;
    }

    public final void setSprinting(boolean var1) {
        this.sprinting = var1;
    }

    public final boolean getSneaking() {
        return this.sneaking;
    }

    public final void setSneaking(boolean var1) {
        this.sneaking = var1;
    }

    public ActionEvent(boolean sprinting, boolean sneaking) {
        this.sprinting = sprinting;
        this.sneaking = sneaking;
    }
}
