/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package Reality.Realii.mods.modules.render;

import org.lwjgl.opengl.GL11;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventPostRenderPlayer;
import Reality.Realii.event.events.rendering.EventPreRenderPlayer;
import Reality.Realii.event.value.Mode;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;


public class Chams
        extends Module {
    public Mode mode = new Mode("Mode", "mode", new String[]{"Normal", "Textured"}, "Textured");

    public Chams() {
        super("Chams", ModuleType.Render);
        this.addValues(this.mode);
    }

    @EventHandler
    private void preRenderPlayer(EventPreRenderPlayer e) {
        GL11.glEnable((int) 32823);
        GL11.glPolygonOffset((float) 1.0f, (float) -1100000.0f);
    }

    @EventHandler
    private void postRenderPlayer(EventPostRenderPlayer e) {
        GL11.glDisable((int) 32823);
        GL11.glPolygonOffset((float) 1.0f, (float) 1100000.0f);
    }


}

