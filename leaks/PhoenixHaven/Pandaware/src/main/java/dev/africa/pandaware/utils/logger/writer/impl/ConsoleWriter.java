package dev.africa.pandaware.utils.logger.writer.impl;

import org.tinylog.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.writers.AbstractFormatPatternWriter;

import java.io.PrintStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

/**
 * @author Cg.
 */
public class ConsoleWriter extends AbstractFormatPatternWriter {
    private final boolean swingConsole;

    public ConsoleWriter(Map<String, String> properties) {
        this(properties, false);
    }

    protected ConsoleWriter(Map<String, String> properties, boolean swingConsole) {
        super(properties);
        this.swingConsole = swingConsole;
    }

    @Override
    public Collection<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.MESSAGE);
    }

    @Override
    public void write(LogEntry logEntry) {
        if (logEntry.getTag() != null) {
            return;
        }

        String message = logEntry.getMessage();
        StringBuilder msgBuilder = new StringBuilder();
        switch (logEntry.getLevel()) {
            case DEBUG:
                msgBuilder.append("[DEBUG] ").append(message);
                break;
            case WARN:
                msgBuilder.append(swingConsole ? "[WARNING] " : "\u001B[33m[WARNING]\u001B[0m ").append(message);
                break;
            case ERROR:
                msgBuilder.append(swingConsole ? "[ERROR] " : "\u001B[31m[ERROR]\u001B[0m ").append(message);
                break;
            default:
                msgBuilder.append(swingConsole ? "[INFO] " : "\u001B[32m[INFO]\u001B[0m ").append(message);
        }

        msgBuilder.append("\n");
        getPrintStream(logEntry.getLevel()).print(msgBuilder);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    private PrintStream getPrintStream(Level level) {
        if (level == Level.ERROR) {
            return System.err;
        } else {
            return System.out;
        }
    }
}
