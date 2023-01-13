package me.uncodable.srt.impl.commands.client.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
   String name();

   String desc();

   String usage();

   boolean legit() default false;
}
