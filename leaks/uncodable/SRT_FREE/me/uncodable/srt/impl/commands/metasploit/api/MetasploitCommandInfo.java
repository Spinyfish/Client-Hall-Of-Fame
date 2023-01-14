package me.uncodable.srt.impl.commands.metasploit.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MetasploitCommandInfo {
   String name();

   String desc();
}
