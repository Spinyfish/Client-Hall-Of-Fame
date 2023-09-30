package com.alan.clients.newevent.impl.motion;

import com.alan.clients.newevent.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Strikeless
 * @since 13.03.2022
 */
@Getter
@Setter
@AllArgsConstructor
public class SlowDownEvent extends CancellableEvent {
    private float strafeMultiplier;
    private float forwardMultiplier;
}
