package rip.athena.client.modules.impl.other;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;

public class Settings extends Module
{
    @ConfigValue.Boolean(name = "F5 Nametags", description = "Shows your own nametags in f5 mode")
    public boolean F5Nametags;
    @ConfigValue.Boolean(name = "Render Socket Logo")
    public static boolean socketLogo;
    @ConfigValue.Boolean(name = "Custom GUI Font")
    public static boolean customGuiFont;
    
    public Settings() {
        super("General Settings", Category.HIDDEN, "Athena/gui/mods/fps.png");
        this.F5Nametags = true;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    static {
        Settings.socketLogo = true;
        Settings.customGuiFont = true;
    }
}
