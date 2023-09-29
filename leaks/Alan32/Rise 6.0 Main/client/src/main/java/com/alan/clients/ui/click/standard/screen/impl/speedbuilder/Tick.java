package com.alan.clients.ui.click.standard.screen.impl.speedbuilder;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Author: Alan
 * Date: 28/03/2022
 */

@Getter
@Setter
public class Tick {
    public int offGroundTick;
    ConcurrentLinkedQueue<Modifier> modifiers = new ConcurrentLinkedQueue<>();

    public Tick(final int offGroundTick) {
        this.offGroundTick = offGroundTick;
    }
}
