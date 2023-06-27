package rip.athena.client.gui.clickgui;

import rip.athena.client.gui.framework.*;
import java.util.*;
import net.minecraft.client.*;
import rip.athena.client.gui.clickgui.pages.*;

public class PageManager
{
    private IngameMenu parent;
    private Menu menu;
    private Map<Category, Page> pages;
    
    public PageManager(final IngameMenu parent, final Menu menu) {
        this.parent = parent;
        this.menu = menu;
        this.pages = new HashMap<Category, Page>();
        this.init();
    }
    
    private void init() {
        final Minecraft mc = Minecraft.getMinecraft();
        this.pages.put(Category.MODS, new ModsPage(mc, this.menu, this.parent));
        this.pages.put(Category.SETTINGS, new SettingsPage(mc, this.menu, this.parent));
        this.pages.put(Category.MACROS, new MacrosPage(mc, this.menu, this.parent));
        this.pages.put(Category.WAYPOINTS, new WaypointsPage(mc, this.menu, this.parent));
        this.pages.put(Category.PROFILES, new ProfilesPage(mc, this.menu, this.parent));
        this.pages.put(Category.CAPES, new CapesPage(mc, this.menu, this.parent));
        this.pages.put(Category.THEMES, new ThemesPage(mc, this.menu, this.parent));
    }
    
    public Map<Category, Page> getPages() {
        return this.pages;
    }
    
    public <T extends Page> T getPage(final Category category) {
        return (T)this.pages.get(category);
    }
    
    public <T extends Page> T getPage(final Class<T> cast, final Category category) {
        return (T)this.pages.get(category);
    }
}
