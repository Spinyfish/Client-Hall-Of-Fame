package alos.stella.module.modules.visual;

import alos.stella.event.EventTarget;
import alos.stella.event.events.EventFogColor;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.utils.ColorUtils;
import alos.stella.value.BoolValue;
import alos.stella.value.FloatValue;
import alos.stella.value.IntegerValue;
import alos.stella.value.ListValue;

import java.awt.*;

@ModuleInfo(name = "Camera", description = "Change your Camera", category = ModuleCategory.VISUAL)
public class Camera extends Module {
    //FOG COLOR
    public static BoolValue fogColor = new BoolValue("FogColor", false);
    public static final ListValue colorMode = new ListValue("ModelMode", new String[]{"Rainbow", "Client", "Custom"}, "Rainbow", fogColor::get);
    public static FloatValue distance = new FloatValue("FogDistance", 0.7f,0.1f, 2.39f, fogColor::get);
    public static IntegerValue rFog = new IntegerValue("RFog", 255,0,255, fogColor::get);
    public static IntegerValue gFog = new IntegerValue("GFog", 255,0,255, fogColor::get);
    public static IntegerValue bFog = new IntegerValue("BFog", 255,0,255, fogColor::get);
    //FOG COLOR
    //CUSTOM HIT COLOR
    public static BoolValue customHitColor = new BoolValue("CustomHitColor", false);
    public static IntegerValue rHit = new IntegerValue("RHit", 255,0,255, customHitColor::get);
    public static IntegerValue gHit = new IntegerValue("GHit", 255,0,255, customHitColor::get);
    public static IntegerValue bHit = new IntegerValue("BHit", 255,0,255, customHitColor::get);
    public static IntegerValue aHit = new IntegerValue("AHit", 255,0,255, customHitColor::get);
    //CUSTOM HIT COLOR

    @EventTarget
    public void onFogColor(EventFogColor event) {
        if (fogColor.get()) {
            String colorModeValue = colorMode.get();
            if (colorModeValue.equalsIgnoreCase("Rainbow")) {
                Color color = ColorUtils.rainbow(1, 1.0f, 1.0f);
                event.setRed(color.getRed());
                event.setGreen(color.getGreen());
                event.setBlue(color.getBlue());
            } else if (colorModeValue.equalsIgnoreCase("Client")) {
                Color clientColor = new Color(255, 111, 255);
                event.setRed(clientColor.getRed());
                event.setGreen(clientColor.getGreen());
                event.setBlue(clientColor.getBlue());
            } else if (colorModeValue.equalsIgnoreCase("Custom")) {
                Color customColorValue = new Color(rFog.get(), gFog.get(), bFog.get());
                event.setRed(customColorValue.getRed());
                event.setGreen(customColorValue.getGreen());
                event.setBlue(customColorValue.getBlue());
            }
        }
    }

}
