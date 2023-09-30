package club.pulsive.impl.event.client;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class KeyPressEvent extends Event {
    private final int key;
}
