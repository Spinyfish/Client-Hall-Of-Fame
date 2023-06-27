package rip.athena.client.utils.input;

public enum BindType
{
    HOLD("Hold"), 
    TOGGLE("Toggle");
    
    String type;
    
    private BindType(final String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return this.type;
    }
    
    public static BindType getBind(final String type) {
        for (final BindType bind : values()) {
            if (bind.type.equalsIgnoreCase(type)) {
                return bind;
            }
        }
        return BindType.TOGGLE;
    }
}
