package club.pulsive.impl.module.impl.misc;

import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.ColorProperty;
import club.pulsive.impl.property.implementations.DoubleProperty;

import java.awt.*;

@ModuleInfo(name = "ClientSettings", renderName = "ClientSettings", description = "The main client settings.", category = Category.CLIENT)

public class ClientSettings extends Module {
    public static ColorProperty mainColor = new ColorProperty("Main Color", Color.CYAN);
    public static ColorProperty secondColor = new ColorProperty("Second Color", Color.PINK);
    public static DoubleProperty outlineWidth = new DoubleProperty("Outline Width", 0.4,0.2,0.7,0.1);
    public static Property<Boolean> uiOutlines = new Property<>("Outlined UIs", true);
}
