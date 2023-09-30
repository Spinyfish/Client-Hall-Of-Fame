package alos.stella.module.modules.visual;

import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.ui.clickgui.astolfo.AstolfoClickGui;
import alos.stella.ui.clickgui.dropdown.DropdownGUI;
import alos.stella.utils.ColorUtils;
import alos.stella.value.BoolValue;
import alos.stella.value.FloatValue;
import alos.stella.value.IntegerValue;
import alos.stella.value.ListValue;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "ClickGUI", description = "Opens the ClickGUI.", category = ModuleCategory.VISUAL, keyBind = Keyboard.KEY_RSHIFT, forceNoSound = true, onlyEnable = true)
public class ClickGUI extends Module {
    public static final ListValue modeValue = new ListValue("Mode", new String[]{"Stella","Dropdown"}, "Sky");

    public static final ListValue colorModeValue = new ListValue("Color", new String[]{"Custom","Rainbow","Astolfo","Fade"}, "Sky");
    public static final IntegerValue colorRedValue = new IntegerValue("Red", 0, 0, 255);
    public static final IntegerValue colorGreenValue = new IntegerValue("Green", 160, 0, 255);
    public static final IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);
    public final ListValue backgroundValue = new ListValue("Background", new String[]{"Gradient", "None"}, "Default");
    private static final FloatValue saturationValue = new FloatValue("saturation", 0.5f, 0f, 1f);
    private static final FloatValue brightnessValue = new FloatValue("brightness", 1f, 0f, 1f);

    public final IntegerValue gradStartValue = new IntegerValue("GradientStartAlpha", 255, 0, 255, () -> backgroundValue.get().equalsIgnoreCase("gradient"));
    public final IntegerValue gradEndValue = new IntegerValue("GradientEndAlpha", 0, 0, 255, () -> backgroundValue.get().equalsIgnoreCase("gradient"));

    public final BoolValue getClosePrevious = new BoolValue("ClosePrevious",false);

    public void onEnable() {
        if (modeValue.get().contains("Stella")) {
            mc.displayGuiScreen(new AstolfoClickGui());
            this.setState(false);
        } else if (modeValue.get().contains("Dropdown")) {
            mc.displayGuiScreen(new DropdownGUI());
            this.setState(false);
        }
    }
    public static Color generateColor() {
        Color c = new Color(255, 255, 255, 255);
        switch (colorModeValue.get().toLowerCase()) {
            case "Custom":
                c = new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
                break;
            case "rainbow":
                c = new Color(alos.stella.utils.render.ColorUtils.getRainbowOpaque(2,saturationValue.get(), brightnessValue.get(), 0));
                break;
            case "astolfo":
                c = alos.stella.utils.render.ColorUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get());
                break;
            case "fade":
                c = ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100);
                break;
        }
        return c;
    }
}
