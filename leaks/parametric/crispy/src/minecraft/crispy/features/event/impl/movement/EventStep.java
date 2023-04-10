package crispy.features.event.impl.movement;

import crispy.features.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;

@RequiredArgsConstructor
@Getter
public class EventStep extends Event<EventStep> {
    private final Entity entity;
    private final float stepHeight;
}
