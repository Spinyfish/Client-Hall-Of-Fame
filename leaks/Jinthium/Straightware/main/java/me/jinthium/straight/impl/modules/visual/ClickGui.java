package me.jinthium.straight.impl.modules.visual;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.clickgui.dropdown.DropdownClickGUI;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.render.ShaderEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import org.lwjglx.input.Keyboard;

public class ClickGui extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Compact", "Compact", "Dropdown", "Dropdown-Real");

    public ModeSetting getMode() {
        return mode;
    }

    public static final DropdownClickGUI dropdownClickGui = new DropdownClickGUI();

    public ClickGui(){
        super("ClickGui", Category.VISUALS);
        this.setKey(Keyboard.KEY_RSHIFT);
        this.addSettings(mode);
    }

    @Callback
    final EventCallback<ShaderEvent> shaderEventEventCallback = event -> {
        if(!(mc.currentScreen instanceof GuiImGui) || mode.is("Dropdown-Real"))
            return;

        Client.INSTANCE.getCInterface().render();
    };

    @Override
    public void onEnable() {
        if(mc.currentScreen == null){
            mc.displayGuiScreen(mode.is("Dropdown-Real") ? dropdownClickGui : new GuiImGui());
        }
        toggle();
        super.onEnable();
    }
}
