package dev.echo.module;

import dev.echo.Echo;
import dev.echo.config.DragManager;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.GameCloseEvent;
import dev.echo.listener.event.impl.game.KeyPressEvent;
import dev.echo.listener.event.impl.game.TickEvent;
import dev.echo.module.impl.render.Statistics;
import dev.echo.ui.mainmenu.CustomMainMenu;
import dev.echo.utils.Utils;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMultiplayer;

public class BackgroundProcess implements Utils {

    @Link
    public final Listener<KeyPressEvent> keyPressEventListener = (event) -> {
        for (Module module : Echo.INSTANCE.getModuleCollection().getModules()) {
            if (module.getKeybind().getCode() == event.getKey()) {
                module.toggle();
            }
        }
    };

    @Link
    public final Listener<GameCloseEvent> gameCloseEventListener = (event) -> {
        Echo.INSTANCE.getConfigManager().saveDefaultConfig();
        DragManager.saveDragData();
    };

    @Link
    public final Listener<TickEvent> tickEventListener = (event) -> {
        if (Statistics.endTime == -1 && ((!mc.isSingleplayer() && mc.getCurrentServerData() == null) || mc.currentScreen instanceof CustomMainMenu || mc.currentScreen instanceof GuiMultiplayer || mc.currentScreen instanceof GuiDisconnected)) {
            Statistics.endTime = System.currentTimeMillis();
        } else if (Statistics.endTime != -1 && (mc.isSingleplayer() || mc.getCurrentServerData() != null)) {
            Statistics.reset();
        }
    };

}
