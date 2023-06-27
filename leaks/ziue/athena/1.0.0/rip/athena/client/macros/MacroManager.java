package rip.athena.client.macros;

import java.util.concurrent.*;
import net.minecraft.client.*;
import java.util.*;
import rip.athena.client.events.*;
import rip.athena.client.events.types.input.*;

public class MacroManager
{
    private List<Macro> macros;
    
    public MacroManager() {
        this.macros = new CopyOnWriteArrayList<Macro>();
    }
    
    @SubscribeEvent
    public void onMouseDown(final MouseDownEvent event) {
        if (Minecraft.getMinecraft().currentScreen != null) {
            return;
        }
        for (final Macro macro : this.macros) {
            if (!macro.isEnabled()) {
                continue;
            }
            if (macro.getKey() != event.getButton() - 500) {
                continue;
            }
            Minecraft.getMinecraft().thePlayer.sendChatMessage(macro.getCommand());
        }
    }
    
    @SubscribeEvent
    public void onKeyDown(final KeyDownEvent event) {
        if (Minecraft.getMinecraft().currentScreen != null) {
            return;
        }
        for (final Macro macro : this.macros) {
            if (!macro.isEnabled()) {
                continue;
            }
            if (macro.getKey() != event.getKey() || event.getKey() <= 0) {
                continue;
            }
            Minecraft.getMinecraft().thePlayer.sendChatMessage(macro.getCommand());
        }
    }
    
    public List<Macro> getMacros() {
        return this.macros;
    }
    
    public void setMacros(final List<Macro> macros) {
        this.macros = macros;
    }
}
