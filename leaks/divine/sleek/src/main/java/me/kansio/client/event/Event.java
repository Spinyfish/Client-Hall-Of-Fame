package me.kansio.client.event;

import lombok.Getter;
import lombok.Setter;

public abstract class Event {

    @Getter @Setter
    private boolean cancelled;


}
