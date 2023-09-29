package com.alan.clients.newevent.impl.other;


import com.alan.clients.module.Module;
import com.alan.clients.newevent.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class ModuleToggleEvent implements Event {
    private Module module;
}