package com.alan.clients.newevent.impl.render;


import com.alan.clients.newevent.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class Render3DEvent implements Event {

    private final float partialTicks;
}
