package me.kansio.client.modules.impl.visuals;

import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.value.value.StringValue;
import org.lwjgl.input.Keyboard;

@ModuleData(
        name = "Click GUI",
        category = ModuleCategory.VISUALS,
        description = "The click gui... nothing special (Credit: Wykt)",
        bind = Keyboard.KEY_RSHIFT
)
public class ClickGUI extends Module {

    public BooleanValue hudcolor = new BooleanValue("Hud Colour", this, false);
    private BooleanValue font = new BooleanValue("Font", this, false);

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new me.kansio.client.gui.clickgui.frame.ClickGUI());
        toggle();
    }
}
