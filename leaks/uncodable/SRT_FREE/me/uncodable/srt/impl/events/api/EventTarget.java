package me.uncodable.srt.impl.events.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventTarget {
   Class<? extends Event> target();
}
