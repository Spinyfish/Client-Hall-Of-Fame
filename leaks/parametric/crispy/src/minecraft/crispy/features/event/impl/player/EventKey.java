package crispy.features.event.impl.player;

import crispy.features.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class EventKey extends Event<EventKey> {
    private final int code;
}
