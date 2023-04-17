package me.levansj01.verus.compat.api.wrapper;

public enum EnumHand
{
    private static final EnumHand[] $VALUES;
    
    MAIN_HAND("MAIN_HAND", 0), 
    OFF_HAND("OFF_HAND", 1);
    
    private EnumHand(final String s, final int n) {
    }
    
    static {
        $VALUES = $values();
    }
    
    private static EnumHand[] $values() {
        return new EnumHand[] { EnumHand.MAIN_HAND, EnumHand.OFF_HAND };
    }
}
