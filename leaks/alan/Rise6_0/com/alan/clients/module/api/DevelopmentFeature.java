package com.alan.clients.module.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to denote modules that shouldn't appear in release builds of the client
 * @author Hazsi
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DevelopmentFeature {
}
