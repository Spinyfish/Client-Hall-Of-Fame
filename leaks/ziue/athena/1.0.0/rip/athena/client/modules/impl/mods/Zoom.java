package rip.athena.client.modules.impl.mods;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;
import rip.athena.client.events.types.client.*;
import optifine.*;
import org.lwjgl.input.*;
import rip.athena.client.events.*;

public class Zoom extends Module
{
    @ConfigValue.Boolean(name = "Smooth Zoom")
    public boolean smoothZoom;
    @ConfigValue.Boolean(name = "Scroll Zoom")
    public boolean scrollZoom;
    @ConfigValue.Boolean(name = "Inverted")
    private boolean inverted;
    @ConfigValue.Float(name = "Zoom Factor", min = 0.1f, max = 10.0f)
    private float zoomFactor;
    public float scrollAmount;
    
    public Zoom() {
        super("Zoom", Category.MODS, "Athena/gui/mods/zoom.png");
        this.smoothZoom = false;
        this.scrollZoom = false;
        this.inverted = false;
        this.zoomFactor = 1.0f;
        this.scrollAmount = 0.0f;
    }
    
    @SubscribeEvent
    public void onTick(final ClientTickEvent event) {
        if (Config.zoomMode) {
            if (this.scrollZoom) {
                while (Mouse.next()) {
                    if (Mouse.getEventDWheel() != 0.0) {
                        final float scroll = (float)Mouse.getEventDWheel();
                        if (this.inverted) {
                            if (scroll > 0.0) {
                                this.scrollAmount += 1.0f * this.zoomFactor;
                            }
                            else {
                                this.scrollAmount -= 1.0f * this.zoomFactor;
                            }
                        }
                        else if (scroll > 0.0) {
                            this.scrollAmount -= 1.0f * this.zoomFactor;
                        }
                        else {
                            this.scrollAmount += 1.0f * this.zoomFactor;
                        }
                    }
                }
            }
            else {
                this.scrollAmount = 10.0f - this.zoomFactor * 1.0f;
            }
        }
    }
}
