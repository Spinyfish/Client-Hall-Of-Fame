package me.kansio.client.event.impl;
import me.kansio.client.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class RenderOverlayEvent extends Event {

    private ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

    public ScaledResolution getSr() {
        return sr;
    }
}