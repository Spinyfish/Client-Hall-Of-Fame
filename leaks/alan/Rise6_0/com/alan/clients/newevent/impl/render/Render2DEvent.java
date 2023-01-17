package com.alan.clients.newevent.impl.render;


import com.alan.clients.newevent.Event;
import com.alan.clients.script.api.wrapper.impl.event.impl.ScriptRender2DEvent;
import com.alan.clients.script.api.wrapper.impl.event.ScriptEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@AllArgsConstructor
public final class Render2DEvent implements Event {

    private final ScaledResolution scaledResolution;
    private final float partialTicks;

}
