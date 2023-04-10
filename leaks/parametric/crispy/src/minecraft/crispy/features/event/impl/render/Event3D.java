package crispy.features.event.impl.render;

import crispy.features.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Event3D extends Event<Event3D> {
    private final float partialTicks;

}
