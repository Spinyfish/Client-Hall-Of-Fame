// 
// Decompiled by Procyon v0.5.36
// 

package kotlin;

import org.jetbrains.annotations.NotNull;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000\u0010\n\u0002\u0010\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0002\b\u0002J\b\u0010\u0002\u001a\u00020\u0003H\u0016¨\u0006\u0004" }, d2 = { "", "", "toString", "", "kotlin-stdlib" })
public final class Unit
{
    @NotNull
    public static final Unit INSTANCE;
    
    private Unit() {
    }
    
    @NotNull
    @Override
    public String toString() {
        return "kotlin.Unit";
    }
    
    static {
        INSTANCE = new Unit();
    }
}
