// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.io;

import kotlin.Metadata;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004¨\u0006\u0005" }, d2 = { "Lkotlin/io/FileWalkDirection;", "", "(Ljava/lang/String;I)V", "TOP_DOWN", "BOTTOM_UP", "kotlin-stdlib" })
public enum FileWalkDirection
{
    TOP_DOWN, 
    BOTTOM_UP;
    
    private static final /* synthetic */ FileWalkDirection[] $values() {
        return new FileWalkDirection[] { FileWalkDirection.TOP_DOWN, FileWalkDirection.BOTTOM_UP };
    }
    
    static {
        $VALUES = $values();
    }
}
