package com.alan.clients.newevent.impl.other;

import com.alan.clients.newevent.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public final class ServerKickEvent implements Event {
    public List<String> message;
}