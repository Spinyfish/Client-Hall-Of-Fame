// 
// Decompiled by Procyon v0.5.36
// 

package kotlin;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import kotlin.jvm.functions.Function0;
import java.io.Serializable;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u00000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0002\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\u00060\u0003j\u0002`\u0004B\u001f\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00000\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0002\u0010\tJ\b\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\bH\u0002R\u0010\u0010\n\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u0006X\u0088\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\u00028\u00008VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\r¨\u0006\u0013" }, d2 = { "Lkotlin/SynchronizedLazyImpl;", "T", "Lkotlin/Lazy;", "Ljava/io/Serializable;", "Lkotlin/io/Serializable;", "initializer", "Lkotlin/Function0;", "lock", "", "(Lkotlin/jvm/functions/Function0;Ljava/lang/Object;)V", "_value", "value", "getValue", "()Ljava/lang/Object;", "isInitialized", "", "toString", "", "writeReplace", "kotlin-stdlib" })
final class SynchronizedLazyImpl<T> implements Lazy<T>, Serializable
{
    @Nullable
    private Function0<? extends T> initializer;
    @Nullable
    private volatile Object _value;
    @NotNull
    private final Object lock;
    
    public SynchronizedLazyImpl(@NotNull final Function0<? extends T> initializer, @Nullable final Object lock) {
        Intrinsics.checkNotNullParameter(initializer, "initializer");
        this.initializer = initializer;
        this._value = UNINITIALIZED_VALUE.INSTANCE;
        Object lock2 = lock;
        if (lock == null) {
            lock2 = this;
        }
        this.lock = lock2;
    }
    
    @Override
    public T getValue() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        kotlin/SynchronizedLazyImpl._value:Ljava/lang/Object;
        //     4: astore_1        /* _v1 */
        //     5: aload_1         /* _v1 */
        //     6: getstatic       kotlin/UNINITIALIZED_VALUE.INSTANCE:Lkotlin/UNINITIALIZED_VALUE;
        //     9: if_acmpeq       14
        //    12: aload_1         /* _v1 */
        //    13: areturn        
        //    14: aload_0         /* this */
        //    15: getfield        kotlin/SynchronizedLazyImpl.lock:Ljava/lang/Object;
        //    18: astore_2       
        //    19: aload_2        
        //    20: astore_3       
        //    21: aload_3        
        //    22: monitorenter   
        //    23: nop            
        //    24: iconst_0       
        //    25: istore          $i$a$-synchronized-SynchronizedLazyImpl$value$1
        //    27: aload_0         /* this */
        //    28: getfield        kotlin/SynchronizedLazyImpl._value:Ljava/lang/Object;
        //    31: astore          _v2
        //    33: aload           _v2
        //    35: getstatic       kotlin/UNINITIALIZED_VALUE.INSTANCE:Lkotlin/UNINITIALIZED_VALUE;
        //    38: if_acmpeq       46
        //    41: aload           _v2
        //    43: goto            74
        //    46: aload_0         /* this */
        //    47: getfield        kotlin/SynchronizedLazyImpl.initializer:Lkotlin/jvm/functions/Function0;
        //    50: dup            
        //    51: invokestatic    kotlin/jvm/internal/Intrinsics.checkNotNull:(Ljava/lang/Object;)V
        //    54: invokeinterface kotlin/jvm/functions/Function0.invoke:()Ljava/lang/Object;
        //    59: astore          typedValue
        //    61: aload_0         /* this */
        //    62: aload           typedValue
        //    64: putfield        kotlin/SynchronizedLazyImpl._value:Ljava/lang/Object;
        //    67: aload_0         /* this */
        //    68: aconst_null    
        //    69: putfield        kotlin/SynchronizedLazyImpl.initializer:Lkotlin/jvm/functions/Function0;
        //    72: aload           typedValue
        //    74: nop            
        //    75: astore          null
        //    77: aload_3        
        //    78: monitorexit    
        //    79: aload           4
        //    81: goto            91
        //    84: astore          4
        //    86: aload_3        
        //    87: monitorexit    
        //    88: aload           4
        //    90: athrow         
        //    91: areturn        
        //    Signature:
        //  ()TT;
        //    StackMapTable: 00 05 FC 00 0E 07 00 05 FF 00 1F 00 06 07 00 02 07 00 05 07 00 05 07 00 05 01 07 00 05 00 00 5B 07 00 05 FF 00 09 00 04 07 00 02 07 00 05 07 00 05 07 00 05 00 01 07 00 41 FF 00 06 00 06 07 00 02 07 00 05 07 00 05 07 00 05 07 00 05 07 00 05 00 01 07 00 05
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  23     77     84     91     Any
        //  84     86     84     91     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:372)
        //     at java.base/java.util.ArrayList.remove(ArrayList.java:535)
        //     at com.strobel.assembler.ir.StackMappingVisitor.pop(StackMappingVisitor.java:267)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:595)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2030)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public boolean isInitialized() {
        return this._value != UNINITIALIZED_VALUE.INSTANCE;
    }
    
    @NotNull
    @Override
    public String toString() {
        return this.isInitialized() ? String.valueOf(this.getValue()) : "Lazy value not initialized yet.";
    }
    
    private final Object writeReplace() {
        return new InitializedLazyImpl(this.getValue());
    }
}
