package dev.africa.pandaware.utils.logger.writer.impl;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.writers.Writer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

public class NotificationWriter implements Writer {
    public NotificationWriter(Map<String, String> properties) {
    }

    @Override
    public Collection<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.MESSAGE);
    }

    @Override
    public void write(LogEntry logEntry) {
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
