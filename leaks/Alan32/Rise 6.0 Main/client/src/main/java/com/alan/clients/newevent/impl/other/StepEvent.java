package com.alan.clients.newevent.impl.other;

import com.alan.clients.newevent.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class StepEvent implements Event {

    private double height;
}
