package me.uncodable.srt.impl.modules.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
   String internalName();

   String name();

   String desc();

   Module.Category category();

   boolean exp() default false;

   boolean legit() default false;
}
