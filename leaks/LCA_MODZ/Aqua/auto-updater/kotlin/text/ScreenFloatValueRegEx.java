// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.text;

import kotlin.jvm.JvmField;
import org.jetbrains.annotations.NotNull;
import kotlin.Metadata;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u00c2\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0005" }, d2 = { "Lkotlin/text/ScreenFloatValueRegEx;", "", "()V", "value", "Lkotlin/text/Regex;", "kotlin-stdlib" })
final class ScreenFloatValueRegEx
{
    @NotNull
    public static final ScreenFloatValueRegEx INSTANCE;
    @JvmField
    @NotNull
    public static final Regex value;
    
    private ScreenFloatValueRegEx() {
    }
    
    static {
        INSTANCE = new ScreenFloatValueRegEx();
        final ScreenFloatValueRegEx $this$value_u24lambda_u240 = ScreenFloatValueRegEx.INSTANCE;
        final int n = 0;
        final String Digits = "(\\p{Digit}+)";
        final String HexDigits = "(\\p{XDigit}+)";
        final String Exp = "[eE][+-]?" + Digits;
        final String HexString = "(0[xX]" + HexDigits + "(\\.)?)|(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ')';
        final String Number = '(' + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|(\\.(" + Digits + ")(" + Exp + ")?)|((" + HexString + ")[pP][+-]?" + Digits + ')';
        final String fpRegex = "[\\x00-\\x20]*[+-]?(NaN|Infinity|((" + Number + ")[fFdD]?))[\\x00-\\x20]*";
        value = new Regex(fpRegex);
    }
}
