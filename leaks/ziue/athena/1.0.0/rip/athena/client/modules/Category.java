package rip.athena.client.modules;

public enum Category
{
    ALL_MODS(0, "ALL MODS", false), 
    MODS(1, "MODS", false), 
    RENDER(2, "RENDER", false), 
    MOVEMENT(3, "MOVEMENT", false), 
    FACTIONS(4, "FACTIONS", false), 
    OTHER(5, "OTHER", false), 
    HIDDEN(6, "", true), 
    FPS_SETTINGS(7, "", true), 
    GROUPS(8, "", true);
    
    private int index;
    private String text;
    private boolean hidden;
    
    private Category(final int index, final String text, final boolean hidden) {
        this.index = index;
        this.text = text;
        this.hidden = hidden;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public String getText() {
        return this.text;
    }
    
    public boolean isHidden() {
        return this.hidden;
    }
}
