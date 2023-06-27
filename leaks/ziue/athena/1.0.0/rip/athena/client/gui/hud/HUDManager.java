package rip.athena.client.gui.hud;

import rip.athena.client.*;
import rip.athena.client.modules.*;
import java.util.*;
import rip.athena.client.events.types.render.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import rip.athena.client.events.*;

public class HUDManager
{
    private List<HUDElement> elements;
    
    public HUDManager() {
        this.elements = new ArrayList<HUDElement>();
        for (final Module mod : Athena.INSTANCE.getModuleManager().getModules()) {
            this.elements.addAll(mod.getHUDElements());
        }
    }
    
    @SubscribeEvent
    public void onRender(final RenderEvent event) {
        if (event.getRenderType() != RenderType.INGAME_OVERLAY) {
            return;
        }
        GlStateManager.pushMatrix();
        final float value = 2.0f / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        GlStateManager.scale(value, value, value);
        for (final HUDElement element : this.elements) {
            if (!element.isVisible()) {
                continue;
            }
            if (!element.getParent().isToggled()) {
                continue;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)element.getX(), (float)element.getY(), 0.0f);
            GlStateManager.scale(element.getScale(), element.getScale(), element.getScale());
            GlStateManager.translate((float)(-element.getX()), (float)(-element.getY()), 0.0f);
            element.onRender();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
    
    public List<HUDElement> getElements() {
        return this.elements;
    }
}
