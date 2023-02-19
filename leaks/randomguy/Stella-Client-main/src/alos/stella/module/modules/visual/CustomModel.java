package alos.stella.module.modules.visual;

import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.value.BoolValue;
import alos.stella.value.FloatValue;
import alos.stella.value.IntegerValue;
import alos.stella.value.ListValue;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "CustomModel", description = "CustomModel.", category = ModuleCategory.VISUAL)
public class CustomModel extends Module {

    public static final ListValue modelMode = new ListValue("ModelMode", new String[]{"Rabbit","Amogus", "None"}, "None");
    public static final ListValue bodyColor = new ListValue("BodyColor", new String[]{"Custom","Client", "Rainbow", "Astolfo"}, "Custom", () -> modelMode.get().equalsIgnoreCase("Amogus"));
    public static final ListValue eyeColor = new ListValue("EyeColor", new String[]{"Custom","Client", "Rainbow", "Astolfo"}, "Custom", () -> modelMode.get().equalsIgnoreCase("Amogus"));
    public static final ListValue legsColor = new ListValue("LegsColor", new String[]{"Custom","Client", "Rainbow", "Astolfo"}, "Custom", () -> modelMode.get().equalsIgnoreCase("Amogus"));

    //ONLY ME
    public static final BoolValue onlyMe = new BoolValue("OnlyMe", true);
    public static IntegerValue r = new IntegerValue("R", 255,0,255, () -> modelMode.get().equalsIgnoreCase("Amogus"));
    public static IntegerValue g = new IntegerValue("G", 255,0,255, () -> modelMode.get().equalsIgnoreCase("Amogus"));
    public static IntegerValue b = new IntegerValue("B", 255,0,255, () -> modelMode.get().equalsIgnoreCase("Amogus"));
    //ONLY ME

}
