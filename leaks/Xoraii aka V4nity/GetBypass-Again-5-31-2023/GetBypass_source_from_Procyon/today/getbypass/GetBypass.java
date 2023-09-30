// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass;

import org.lwjgl.opengl.Display;
import today.getbypass.utils.fontrenderer.FontRenderer;
import today.getbypass.module.ModuleManager;
import java.awt.Color;

public class GetBypass
{
    public static GetBypass instance;
    public static final Color blueClient;
    public static String name;
    public static String version;
    public static String buildString;
    public static ModuleManager moduleManager;
    public static FontRenderer normal;
    public static FontRenderer title;
    public static FontRenderer small;
    public static FontRenderer large;
    public static FontRenderer client;
    
    static {
        GetBypass.instance = new GetBypass();
        blueClient = new Color(0, 128, 255);
        GetBypass.name = "GetBypass";
        GetBypass.version = "a1.0";
        GetBypass.buildString = "alpha";
    }
    
    public static void startClient() {
        GetBypass.moduleManager = new ModuleManager();
        GetBypass.normal = new FontRenderer("assets/minecraft/GetBypass/font/Inter-Regular.ttf", 25.0f);
        GetBypass.title = new FontRenderer("assets/minecraft/GetBypass/font/arial2.ttf", 68.0f);
        GetBypass.small = new FontRenderer("assets/minecraft/GetBypass/font/Inter-Regular.ttf", 12.0f);
        GetBypass.large = new FontRenderer("assets/minecraft/GetBypass/font/Inter-Regular.ttf", 50.0f);
        GetBypass.client = new FontRenderer("assets/minecraft/GetBypass/font/arial.ttf", 35.0f);
        Display.setTitle(String.valueOf(GetBypass.name) + " (" + GetBypass.version + "/" + GetBypass.version.hashCode() + ")");
    }
}
