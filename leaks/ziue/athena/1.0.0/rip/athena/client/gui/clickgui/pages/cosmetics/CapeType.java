package rip.athena.client.gui.clickgui.pages.cosmetics;

public enum CapeType
{
    NORMAL("NORMAL"), 
    BEND("BEND"), 
    SHOULDERS("SHOULDERS"), 
    BEND_AND_SHOULDERS("BEND & SHOULDERS");
    
    String type;
    
    private CapeType(final String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
}
