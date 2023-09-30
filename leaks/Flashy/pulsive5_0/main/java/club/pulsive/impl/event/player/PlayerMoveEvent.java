package club.pulsive.impl.event.player;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerMoveEvent extends Event {
    private double x, y, z;
}
