package com.alan.clients.newevent.impl.render;


import com.alan.clients.newevent.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@AllArgsConstructor
public final class RenderHungerEvent implements Event {

    private final ScaledResolution scaledResolution;

}
