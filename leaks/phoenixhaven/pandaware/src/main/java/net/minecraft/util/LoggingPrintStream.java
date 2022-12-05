package net.minecraft.util;

import org.tinylog.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

public class LoggingPrintStream extends PrintStream
{
    private final String domain;

    public LoggingPrintStream(String domainIn, OutputStream outStream)
    {
        super(outStream);
        this.domain = domainIn;
    }

    public void println(String p_println_1_)
    {
        this.logString(p_println_1_);
    }

    public void println(Object p_println_1_)
    {
        this.logString(String.valueOf(p_println_1_));
    }

    private void logString(String string)
    {
        StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
        StackTraceElement stacktraceelement = astacktraceelement[Math.min(3, astacktraceelement.length)];
        Logger.info("[{}]@.({}:{}): {}", new Object[] {this.domain, stacktraceelement.getFileName(), Integer.valueOf(stacktraceelement.getLineNumber()), string});
    }
}