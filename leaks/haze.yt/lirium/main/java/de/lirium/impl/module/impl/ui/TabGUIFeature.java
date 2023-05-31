/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 06.07.2022
 */
package de.lirium.impl.module.impl.ui;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.impl.events.KeyEvent;
import de.lirium.impl.events.Render2DEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.impl.module.ModuleManager;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.StencilUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@ModuleFeature.Info(name = "Tab GUI", description = "Tabbing modules and values while playing", category = ModuleFeature.Category.UI)
public class TabGUIFeature extends ModuleFeature {

    public TabGUIFeature() {
        setEnabled(true);
    }

    @EventHandler
    public final Listener<Render2DEvent> render2DEventListener = e -> {
        final ScaledResolution sr = new ScaledResolution(mc);
        Client.INSTANCE.tabGui.renderTabGui(sr.getScaledWidth() - Client.INSTANCE.tabGui.width - 10, Client.INSTANCE.getModuleManager().get(HUDFeature.class).mode.getValue().equalsIgnoreCase("Lirium") ? 30 :  - 10);
    };

    @EventHandler
    public final Listener<KeyEvent> keyEventListener = e -> {
        Client.INSTANCE.tabGui.handleKeys(e.key);
    };
}
