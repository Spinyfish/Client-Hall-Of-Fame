package rip.athena.client.modules.impl.other;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;

public class NickHider extends Module
{
    @ConfigValue.Text(name = "Nick", description = "Enter the name of your custom nickname")
    public String nick;
    
    public NickHider() {
        super("Nick Hider", Category.OTHER, "Athena/gui/mods/nickhider.png");
        this.nick = "Custom Nick";
    }
}
