package co.gongzh.procbridge;

import org.jetbrains.annotations.*;

enum StatusCode
{
    REQUEST(0), 
    GOOD_RESPONSE(1), 
    BAD_RESPONSE(2);
    
    int rawValue;
    
    private StatusCode(final int rawValue) {
        this.rawValue = rawValue;
    }
    
    @Nullable
    static StatusCode fromRawValue(final int rawValue) {
        for (final StatusCode sc : values()) {
            if (sc.rawValue == rawValue) {
                return sc;
            }
        }
        return null;
    }
}
