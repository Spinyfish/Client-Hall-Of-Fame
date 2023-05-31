package de.lirium.impl.events;

import best.azura.eventbus.core.Event;

public class OverlayEvent implements Event {
    public boolean renderFire = true;
    public float fireAlpha = 1F, fireHeight = 1F, fireSize = 1F, fireRotation;
}
