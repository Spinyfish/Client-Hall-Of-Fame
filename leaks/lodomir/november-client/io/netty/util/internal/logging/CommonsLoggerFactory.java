/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.CommonsLogger;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.LogFactory;

public class CommonsLoggerFactory
extends InternalLoggerFactory {
    Map<String, InternalLogger> loggerMap = new HashMap<String, InternalLogger>();

    @Override
    public InternalLogger newInstance(String name) {
        return new CommonsLogger(LogFactory.getLog((String)name), name);
    }
}

