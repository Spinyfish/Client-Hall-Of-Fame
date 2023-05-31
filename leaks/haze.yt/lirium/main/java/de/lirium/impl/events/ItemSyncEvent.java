package de.lirium.impl.events;

import best.azura.eventbus.events.CancellableEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ItemSyncEvent extends CancellableEvent {
    public final int slot;
}
