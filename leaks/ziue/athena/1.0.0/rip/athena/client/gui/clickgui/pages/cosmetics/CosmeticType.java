package rip.athena.client.gui.clickgui.pages.cosmetics;

public enum CosmeticType
{
    CAPES("CAPES"), 
    BANDANAS("BANDANAS"), 
    EMOTES("EMOTES"), 
    FLAGS("FLAGS");
    
    String type;
    
    private CosmeticType(final String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return this.type;
    }
}
