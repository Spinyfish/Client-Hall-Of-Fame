package rip.athena.client.gui.clickgui.pages.cosmetics;

public enum BandanaSize
{
    SMALL("SMALL"), 
    MEDIUM("MEDIUM"), 
    LARGE("LARGE");
    
    String type;
    
    private BandanaSize(final String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return this.type;
    }
}
