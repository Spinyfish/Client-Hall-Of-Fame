package dev.echo;

import dev.echo.commands.CommandHandler;
import dev.echo.config.ConfigManager;
import dev.echo.config.DragManager;
import dev.echo.other.intent.cloud.CloudDataManager;
import dev.echo.listener.handler.EventHandler;
import dev.echo.module.Module;
import dev.echo.module.ModuleCollection;
import dev.echo.ui.altmanager.GuiAltManager;
import dev.echo.ui.altmanager.helpers.KingGenApi;
import dev.echo.ui.searchbar.SearchBar;
import dev.echo.ui.sidegui.SideGUI;
import dev.echo.utils.Utils;
import dev.echo.utils.client.ReleaseType;
import dev.echo.utils.objects.Dragging;
import dev.echo.utils.server.PingerUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public class Echo implements Utils {

    public static final Echo INSTANCE = new Echo();


    public static final String NAME = "Echo";
    public static final String VERSION = "1.0";
    public static final ReleaseType RELEASE = ReleaseType.DEV;
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final File DIRECTORY = new File(mc.mcDataDir, NAME);

    private final EventHandler eventProtocol = new EventHandler();
    private final CloudDataManager cloudDataManager = new CloudDataManager();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final SideGUI sideGui = new SideGUI();
    private final SearchBar searchBar = new SearchBar();
    private ModuleCollection moduleCollection;
    private ConfigManager configManager;
    private GuiAltManager altManager;
    private CommandHandler commandHandler;
    private PingerUtils pingerUtils;
    public KingGenApi kingGenApi;

    public static boolean updateGuiScale;
    public static int prevGuiScale;

   // try {
    //        ViaMCP.create();
    //
    //        // In case you want a version slider like in the Minecraft options, you can use this code here, please choose one of those:
    //
    //        ViaMCP.INSTANCE.initAsyncSlider(); // For top left aligned slider
    //        ViaMCP.INSTANCE.initAsyncSlider(x, y, width (min. 110), height (recommended 20)); // For custom position and size slider
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }

    public String getVersion() {
        return VERSION + (RELEASE != ReleaseType.PUBLIC ? " (" + RELEASE.getName() + ")" : "");
    }



    public final Color getClientColor() {
        return new Color(0, 120, 250);
    }

    public final Color getAlternateClientColor() {
        return new Color(0, 90, 192);
    }

    public boolean isEnabled(Class<? extends Module> c) {
        Module m = INSTANCE.moduleCollection.get(c);
        return m != null && m.isEnabled();
    }

    public Dragging createDrag(Module module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }

}
