package com.alan.clients.newevent.impl.inventory;


import com.alan.clients.newevent.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SyncCurrentItemEvent implements Event {
    private int slot;
}