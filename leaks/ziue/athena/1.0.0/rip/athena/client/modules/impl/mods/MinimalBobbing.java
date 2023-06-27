package rip.athena.client.modules.impl.mods;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;

public class MinimalBobbing extends Module
{
    @ConfigValue.Boolean(name = "Remove Hand Bobbing")
    public boolean handBobbing;
    @ConfigValue.Boolean(name = "Remove Screen Bobbing")
    public boolean screenBobbing;
    
    public MinimalBobbing() {
        super("Minimal Bobbing", Category.MODS, "Athena/gui/mods/nobobbing.png");
        this.handBobbing = true;
        this.screenBobbing = true;
    }
}
