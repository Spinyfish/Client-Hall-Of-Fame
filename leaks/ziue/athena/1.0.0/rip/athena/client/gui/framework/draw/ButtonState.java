package rip.athena.client.gui.framework.draw;

public enum ButtonState
{
    NORMAL(""), 
    HOVER("Hover"), 
    ACTIVE("Active"), 
    POPUP("Popup"), 
    DISABLED("Disabled"), 
    HOVERACTIVE("HoverActive");
    
    String state;
    
    private ButtonState(final String state) {
        this.state = state;
    }
    
    @Override
    public String toString() {
        return this.state;
    }
}
