package me.levansj01.verus.check.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.data.version.ClientVersion;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CheckInfo {
    CheckType type();

    ServerVersion[] unsupportedServers() default {};

    int maxViolations() default Integer.MAX_VALUE;

    boolean blocks() default false;

    String friendlyName() default "";

    ClientVersion unsupportedAtleast() default ClientVersion.NONE;

    double minViolations() default 0.0;

    ClientVersion[] unsupportedVersions() default {};

    boolean heavy() default false;

    boolean schem() default false;

    int priority() default 1;

    CheckVersion version() default CheckVersion.RELEASE;

    boolean logData() default false;

    String subType();

    ServerVersion unsupportedServerAtleast() default ServerVersion.NONE;

    boolean butterfly() default false;
}