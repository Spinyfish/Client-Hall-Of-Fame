package cc.swift.module.impl.render;

import cc.swift.events.EventState;
import cc.swift.events.RenderOverlayEvent;
import cc.swift.events.RenderPlayerEvent;
import cc.swift.events.RenderWorldEvent;
import cc.swift.module.Module;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import org.lwjgl.opengl.GL11;

public class ChamsModule extends Module {
    public ChamsModule() {
        super("Chams", Category.RENDER);
    }

    @Handler
    public final Listener<RenderPlayerEvent> renderPlayerEventListener = event -> {
        if(event.getState() == EventState.PRE){
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, -1000000.0F);
        }
        else{
            GL11.glDisable(32823);
            GL11.glPolygonOffset(1.0F, 1000000.0F);
        }
    };
}
