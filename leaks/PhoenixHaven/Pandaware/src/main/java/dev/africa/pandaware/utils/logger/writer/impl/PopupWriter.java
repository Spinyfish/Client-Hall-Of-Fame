package dev.africa.pandaware.utils.logger.writer.impl;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.writers.Writer;

import javax.swing.*;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

/**
 * @author Cg.
 */
public class PopupWriter implements Writer {
    public PopupWriter(Map<String, String> properties) {
    }

    @Override
    public Collection<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.MESSAGE);
    }

    @Override
    public void write(LogEntry logEntry) {
        int messageType;
        switch (logEntry.getLevel()) {
            case WARN:
                messageType = JOptionPane.WARNING_MESSAGE;
                break;
            case ERROR:
                messageType = JOptionPane.ERROR_MESSAGE;
                break;
            default:
                messageType = JOptionPane.INFORMATION_MESSAGE;
        }
        JOptionPane.showMessageDialog(null, logEntry.getMessage(), "Stitch", messageType);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}