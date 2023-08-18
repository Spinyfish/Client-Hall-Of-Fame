package me.jinthium.straight.api.clickgui;

import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImDouble;
import imgui.type.ImString;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import io.mxngo.echo.core.EventListener;
import me.jinthium.scripting.api.Script;
import me.jinthium.straight.api.clickgui.theme.Theme;
import me.jinthium.straight.api.config.LocalConfig;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.Setting;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.keyboard.KeyPressEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.visual.ClickGui;
import me.jinthium.straight.impl.settings.*;
import me.jinthium.straight.impl.ui.themes.Compact;
import me.jinthium.straight.impl.ui.themes.Dropdown;
import me.jinthium.straight.impl.utils.file.FileUtils;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import okhttp3.OkHttpClient;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import org.lwjglx.opengl.Display;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class CInterface implements MinecraftInstance, EventListener {

    private long windowHandle;
    float[] funny = new float[]{5.2f};
    private Theme cguiTheme;
    private String currentMode;
    private boolean listening, extended;
    private Module binding;

    private final ImGuiImplGlfw imGuiGlfw;
    private IImGuiTheme theme;
    private ImGuiStyle guiStyle;
    private final ImGuiImplGl3 imGuiGl3;
    private Module.Category currentCategory = Module.Category.MOVEMENT;
    private LocalConfig currentConfig;

    private ImString imString = new ImString("Put what u wanna ask here"), configName = new ImString("Config", 255);

    public CInterface() {
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
        // dont try to init stuff here that wont exist before client startup (init stuff in the init() method)
    }

    @Callback
    final EventCallback<KeyPressEvent> keyPressEventEventCallback = event -> {
        if(binding != null && listening){
            if(event.getKey() == Keyboard.KEY_ESCAPE){
                event.cancel();
                binding.getKeybind().setCode(0);
            }else{
                binding.getKeybind().setCode(event.getKey());
            }
            binding = null;
            listening = false;
        }
    };

    public void setCurrentCategory(Module.Category currentCategory) {
        this.currentCategory = currentCategory;
    }

    public Module.Category getCurrentCategory() {
        return currentCategory;
    }

    public ImString getConfigName() {
        return configName;
    }

    public LocalConfig getCurrentConfig() {
        return currentConfig;
    }

    public Module getBinding() {
        return binding;
    }

    public void setBinding(Module binding) {
        this.binding = binding;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    public boolean isListening() {
        return listening;
    }

    private void loadConfig() {
        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename(null);                               // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);     // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);   // Enable Multi-Viewport / Platform Windows

        final ImFontConfig fontConfig = theme.applyFont(io);
        // apply additional config flags and destroy just in case someone forgot to implement within the theme
        fontConfig.setPixelSnapH(true);
        fontConfig.destroy();

        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final ImGuiStyle style = ImGui.getStyle();
            style.setWindowRounding(6.5f);
            style.setColor(ImGuiCol.WindowBg, ImGui.getColorU32(ImGuiCol.WindowBg, 1));
        }
    }

    public void init(final long handle) {
        theme = new IImGuiTheme();

        ImGui.createContext();
        loadConfig();

        imGuiGlfw.init(handle, true);
        imGuiGl3.init();
        windowHandle = handle;
    }

    public void destroy() {
        imGuiGlfw.dispose();
        imGuiGl3.dispose();
        windowHandle = 0;
    }

    public void render() {
        if(guiStyle == null)
            guiStyle = ImGui.getStyle();
        ClickGui clickGui = Client.INSTANCE.getModuleManager().getModule(ClickGui.class);
        theme.applyStyle(guiStyle);
        imGuiGlfw.newFrame();
        ImGui.newFrame();
        Client.INSTANCE.getConfigManager().collectConfigs();
        switch(clickGui.getMode().getMode()){
            case "Compact" -> cguiTheme = new Compact();
            case "Dropdown" -> cguiTheme = new Dropdown();
        }
        cguiTheme.render();
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);

        }

    }
}
