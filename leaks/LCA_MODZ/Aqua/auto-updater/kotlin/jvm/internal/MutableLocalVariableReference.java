// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import kotlin.KotlinNothingValueException;
import kotlin.reflect.KDeclarationContainer;
import kotlin.SinceKotlin;
import kotlin.Metadata;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0017\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\n\u0010\u0003\u001a\u0004\u0018\u00010\u0004H\u0016J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\u0012\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\u0004H\u0016¨\u0006\n" }, d2 = { "Lkotlin/jvm/internal/MutableLocalVariableReference;", "Lkotlin/jvm/internal/MutablePropertyReference0;", "()V", "get", "", "getOwner", "Lkotlin/reflect/KDeclarationContainer;", "set", "", "value", "kotlin-stdlib" })
@SinceKotlin(version = "1.1")
public class MutableLocalVariableReference extends MutablePropertyReference0
{
    @NotNull
    @Override
    public KDeclarationContainer getOwner() {
        LocalVariableReferencesKt.access$notSupportedError();
        throw new KotlinNothingValueException();
    }
    
    @Nullable
    @Override
    public Object get() {
        LocalVariableReferencesKt.access$notSupportedError();
        throw new KotlinNothingValueException();
    }
    
    @Override
    public void set(@Nullable final Object value) {
        LocalVariableReferencesKt.access$notSupportedError();
        throw new KotlinNothingValueException();
    }
}
