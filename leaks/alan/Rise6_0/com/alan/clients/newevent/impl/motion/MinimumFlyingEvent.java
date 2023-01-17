package com.alan.clients.newevent.impl.motion;


import com.alan.clients.newevent.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Alan
 * @since 13.03.2022
 */
@Getter
@Setter
@AllArgsConstructor
public class MinimumFlyingEvent implements Event {
    private double minimum;
}
