// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.text;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.Metadata;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\r\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u00012\u00020\u0002B\u0019\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004¢\u0006\u0002\u0010\u0006R\u0014\u0010\u0005\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010¨\u0006\u0011" }, d2 = { "Lkotlin/text/RegexOption;", "", "Lkotlin/text/FlagEnum;", "value", "", "mask", "(Ljava/lang/String;III)V", "getMask", "()I", "getValue", "IGNORE_CASE", "MULTILINE", "LITERAL", "UNIX_LINES", "COMMENTS", "DOT_MATCHES_ALL", "CANON_EQ", "kotlin-stdlib" })
public enum RegexOption implements FlagEnum
{
    private final int value;
    private final int mask;
    
    IGNORE_CASE(2, 0, 2, (DefaultConstructorMarker)null), 
    MULTILINE(8, 0, 2, (DefaultConstructorMarker)null), 
    LITERAL(16, 0, 2, (DefaultConstructorMarker)null), 
    UNIX_LINES(1, 0, 2, (DefaultConstructorMarker)null), 
    COMMENTS(4, 0, 2, (DefaultConstructorMarker)null), 
    DOT_MATCHES_ALL(32, 0, 2, (DefaultConstructorMarker)null), 
    CANON_EQ(128, 0, 2, (DefaultConstructorMarker)null);
    
    private RegexOption(final int value, final int mask) {
        this.value = value;
        this.mask = mask;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    @Override
    public int getMask() {
        return this.mask;
    }
    
    private static final /* synthetic */ RegexOption[] $values() {
        return new RegexOption[] { RegexOption.IGNORE_CASE, RegexOption.MULTILINE, RegexOption.LITERAL, RegexOption.UNIX_LINES, RegexOption.COMMENTS, RegexOption.DOT_MATCHES_ALL, RegexOption.CANON_EQ };
    }
    
    static {
        $VALUES = $values();
    }
}
