package tech.dort.dortware.impl.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tech.dort.dortware.api.event.Event;

public class RenderHUDEvent extends Event {

    private ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

    public ScaledResolution getSr() {
        return sr;
    }
}
