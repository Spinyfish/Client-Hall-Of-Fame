package rip.athena.client.gui.clickgui;

import rip.athena.client.gui.framework.draw.*;
import net.minecraft.client.*;
import rip.athena.client.gui.framework.*;

public abstract class Page implements IPage, DrawImpl
{
    protected Minecraft mc;
    protected Menu menu;
    protected IngameMenu parent;
    
    public Page(final Minecraft mc, final Menu menu, final IngameMenu parent) {
        this.mc = mc;
        this.menu = menu;
        this.parent = parent;
    }
}
