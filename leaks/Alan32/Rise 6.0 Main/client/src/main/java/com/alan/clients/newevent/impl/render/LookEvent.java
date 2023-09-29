package com.alan.clients.newevent.impl.render;

import com.alan.clients.newevent.Event;
import com.alan.clients.util.vector.Vector2f;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class LookEvent implements Event {
    private Vector2f rotation;
}
