package alos.stella.event.events;

import alos.stella.event.CancellableEvent;

public final class ClickWindowEvent extends CancellableEvent {
    private final int windowId;
    private final int slotId;
    private final int mouseButtonClicked;
    private final int mode;

    public final int getWindowId() {
        return this.windowId;
    }

    public final int getSlotId() {
        return this.slotId;
    }

    public final int getMouseButtonClicked() {
        return this.mouseButtonClicked;
    }

    public final int getMode() {
        return this.mode;
    }

    public ClickWindowEvent(int windowId, int slotId, int mouseButtonClicked, int mode) {
        this.windowId = windowId;
        this.slotId = slotId;
        this.mouseButtonClicked = mouseButtonClicked;
        this.mode = mode;
    }
}