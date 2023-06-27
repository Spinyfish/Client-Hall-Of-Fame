package rip.athena.client.utils.input;

import net.minecraft.client.*;
import rip.athena.client.*;
import java.util.stream.*;
import rip.athena.client.modules.*;
import java.util.*;
import rip.athena.client.events.*;
import rip.athena.client.events.types.input.*;
import rip.athena.client.modules.impl.render.*;
import net.minecraft.client.gui.*;

public class KeybindManager
{
    @SubscribeEvent
    public void onKeyUp(final KeyUpEvent event) {
        if (isInvalidScreen(Minecraft.getMinecraft().currentScreen)) {
            return;
        }
        for (final Module module : Athena.INSTANCE.getModuleManager().getModules().stream().filter(entry -> entry.isBound() && entry.getBindType() == BindType.HOLD && entry.getKeyBind() == event.getKey()).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList())) {
            module.setEnabled(false);
        }
    }
    
    @SubscribeEvent
    public void onKeyDown(final KeyDownEvent event) {
        if (isInvalidScreen(Minecraft.getMinecraft().currentScreen)) {
            return;
        }
        for (final Module module : Athena.INSTANCE.getModuleManager().getModules().stream().filter(entry -> entry.isBound() && entry.getKeyBind() == event.getKey()).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList())) {
            if (module instanceof GUIMod) {
                module.setEnabled(true);
            }
            else {
                module.setEnabled(module.getBindType() != BindType.TOGGLE || !module.isToggled());
            }
        }
    }
    
    public static boolean isInvalidScreen(final GuiScreen screen) {
        return screen != null;
    }
}
