package dev.africa.pandaware.api.module.event;

import dev.africa.pandaware.api.event.interfaces.EventListenable;
import dev.africa.pandaware.api.module.Module;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class TaskedEventListener<T extends Module> implements EventListenable {
    private final String taskName;
    private final T module;
}

