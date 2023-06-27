package rip.athena.client.gui.framework.draw;

public enum DrawType
{
    LINE("line"), 
    BACKGROUND("background"), 
    TEXT("text");
    
    String type;
    
    private DrawType(final String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return this.type;
    }
}
