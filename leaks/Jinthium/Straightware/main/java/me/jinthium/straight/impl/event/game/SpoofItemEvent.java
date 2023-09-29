package me.jinthium.straight.impl.event.game;

import me.jinthium.straight.api.event.Event;

public class SpoofItemEvent extends Event {
    private int currentItem;

    public SpoofItemEvent(int currentItem){
        this.currentItem = currentItem;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }
}