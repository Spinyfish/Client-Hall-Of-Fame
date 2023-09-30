package club.pulsive.impl.event.player;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WindowClickEvent extends Event {

    private final int windowId, slotId, mouseButton, mode;

}
