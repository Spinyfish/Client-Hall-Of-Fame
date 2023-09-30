// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.util;

import java.util.Date;
import java.io.PrintStream;

public class DefaultLogSystem implements LogSystem
{
    public static PrintStream out;
    
    public void error(final String message, final Throwable e) {
        this.error(message);
        this.error(e);
    }
    
    public void error(final Throwable e) {
        DefaultLogSystem.out.println(new Date() + " ERROR:" + e.getMessage());
        e.printStackTrace(DefaultLogSystem.out);
    }
    
    public void error(final String message) {
        DefaultLogSystem.out.println(new Date() + " ERROR:" + message);
    }
    
    public void warn(final String message) {
        DefaultLogSystem.out.println(new Date() + " WARN:" + message);
    }
    
    public void info(final String message) {
        DefaultLogSystem.out.println(new Date() + " INFO:" + message);
    }
    
    public void debug(final String message) {
        DefaultLogSystem.out.println(new Date() + " DEBUG:" + message);
    }
    
    public void warn(final String message, final Throwable e) {
        this.warn(message);
        e.printStackTrace(DefaultLogSystem.out);
    }
    
    static {
        DefaultLogSystem.out = System.out;
    }
}
