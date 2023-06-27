package rip.athena.client.modules.impl.render;

import java.awt.*;
import rip.athena.client.config.*;
import rip.athena.client.modules.*;

public class CustomHitColor extends Module
{
    @ConfigValue.Color(name = "Hit Color", description = "Chose the color of your desires")
    public Color color;
    
    public CustomHitColor() {
        super("Custom Hit Color", Category.RENDER, "Athena/gui/mods/hitcolor.png");
        this.color = Color.WHITE;
    }
}
