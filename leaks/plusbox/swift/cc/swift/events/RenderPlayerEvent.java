package cc.swift.events;

import lombok.AllArgsConstructor;
import dev.codeman.eventbus.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.entity.AbstractClientPlayer;

import java.awt.*;

@Getter
@Setter
public class RenderPlayerEvent extends Event {
    private AbstractClientPlayer entity;
    private EventState state;

    public RenderPlayerEvent(AbstractClientPlayer entity) {
        this.entity = entity;
    }
}
