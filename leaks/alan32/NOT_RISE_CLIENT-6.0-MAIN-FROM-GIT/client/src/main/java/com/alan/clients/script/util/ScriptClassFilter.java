package com.alan.clients.script.util;

import jdk.nashorn.api.scripting.ClassFilter;

/**
 * @author Strikeless
 * @since 19.05.2022
 */
public final class ScriptClassFilter implements ClassFilter {

    @Override
    public boolean exposeToScripts(final String className) {
        return className.startsWith("com.alan.clients.script.api");
    }
}
