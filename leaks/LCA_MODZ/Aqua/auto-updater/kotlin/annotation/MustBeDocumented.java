// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.annotation;

import kotlin.Metadata;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Target(allowedTargets = { AnnotationTarget.ANNOTATION_CLASS })
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ ElementType.ANNOTATION_TYPE })
@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000\n\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\b\u0087\u0002\u0018\u00002\u00020\u0001B\u0000¨\u0006\u0002" }, d2 = { "Lkotlin/annotation/MustBeDocumented;", "", "kotlin-stdlib" })
public @interface MustBeDocumented {
}
