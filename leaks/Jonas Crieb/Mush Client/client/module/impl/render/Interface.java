package client.module.impl.render;

import client.Client;
import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.render.Render2DEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.value.Mode;
import client.value.Value;
import client.value.impl.ModeValue;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleInfo(name = "Interface", description = "The clients interface with all information", category = Category.RENDER, autoEnabled = true, allowDisable = false)
public class Interface extends Module {
    private final ModeValue mode = new ModeValue("Mode", this).add(new Mode<Interface>("Chill", this) {}).setDefault("Chill");
    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {
        mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.AQUA + Client.NAME + " (Best MushMC Client)", 4, 4, new Color(255, 255, 255).hashCode());
        AtomicInteger moduleIndex = new AtomicInteger();
        Client.INSTANCE.getModuleManager().getAll().stream().filter(Module::isEnabled).forEach(module -> {
            String modeName = null;
            for (final Value<?> value : module.getAllValues()) {
                if (value instanceof ModeValue) {
                    final ModeValue modeValue = (ModeValue) value;
                    modeName = modeValue.getValue().getName();
                }
            }
            if (modeName == null) {
                mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.YELLOW + module.getModuleInfo().name().toLowerCase(), event.getScaledResolution().getScaledWidth() - mc.fontRendererObj.getStringWidth(module.getModuleInfo().name().toLowerCase()) - 4, 4 + mc.fontRendererObj.FONT_HEIGHT * moduleIndex.get(), new Color(255, 255, 255).hashCode());
            } else {
                mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.YELLOW + module.getModuleInfo().name().toLowerCase() + " " + EnumChatFormatting.UNDERLINE + EnumChatFormatting.AQUA + modeName.toLowerCase(), event.getScaledResolution().getScaledWidth() - mc.fontRendererObj.getStringWidth(module.getModuleInfo().name().toLowerCase() + " " + modeName.toLowerCase()) - 4, 4 + mc.fontRendererObj.FONT_HEIGHT * moduleIndex.get(), new Color(255, 255, 255).hashCode());
            }
            moduleIndex.getAndIncrement();
        });
    };
}