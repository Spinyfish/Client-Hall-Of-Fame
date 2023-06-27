package rip.athena.client.gui.clickgui;

public enum Category
{
    MODS("MODS", "Athena/gui/menu/mods.png"), 
    SETTINGS("SETTINGS", "Athena/gui/menu/settings.png"), 
    MACROS("MACROS", "Athena/gui/mods/cps.png"), 
    WAYPOINTS("WAYPOINTS", "Athena/gui/menu/waypoints.png"), 
    PROFILES("PROFILES", "Athena/gui/menu/profiles.png"), 
    CAPES("CAPES", "Athena/gui/menu/cosmetics.png"), 
    THEMES("THEMES", "Athena/gui/menu/themes.png");
    
    private String name;
    private String icon;
    
    private Category(final String name, final String icon) {
        this.name = name;
        this.icon = icon;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getIcon() {
        return this.icon;
    }
}
