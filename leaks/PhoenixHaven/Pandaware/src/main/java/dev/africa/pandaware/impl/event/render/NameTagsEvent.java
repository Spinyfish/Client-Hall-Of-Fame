package dev.africa.pandaware.impl.event.render;

import dev.africa.pandaware.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;

@Getter
@AllArgsConstructor
public class NameTagsEvent extends Event {
    private final Entity entity;
}
