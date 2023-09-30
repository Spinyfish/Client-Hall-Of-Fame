package dev.africa.pandaware.utils.logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cg.
 */
public class LoggerSettings {
    private final Map<String, String> settings = new HashMap<>();

    public String getWriter(String writerName) {
        return settings.get("writer" + writerName);
    }

    public LoggerSettings setWriter(String writerName, String writer) {
        settings.put("writer" + writerName, writer);
        return this;
    }

    public String getTag(String writerName) {
        return settings.get("writer" + writerName + "." + "tag");
    }

    public LoggerSettings setTag(String writerName, String tag) {
        settings.put("writer" + writerName + "." + "tag", tag);
        return this;
    }

    public String getLevel(String writerName) {
        return settings.get("writer" + writerName + "." + "level");
    }

    public LoggerSettings setLevel(String writerName, String level) {
        settings.put("writer" + writerName + "." + "level", level);
        return this;
    }

    public String getFormat(String writerName) {
        return settings.get("writer" + writerName + "." + "format");
    }

    public LoggerSettings setFormat(String writerName, String format) {
        settings.put("writer" + writerName + "." + "format", format);
        return this;
    }

    public String getEnableWritingThread() {
        return settings.get("writingthread");
    }

    public LoggerSettings setEnableWritingThread(boolean enable) {
        settings.put("writingthread", String.valueOf(enable));
        return this;
    }

    public String getEnableAutoShutdown() {
        return settings.get("autoshutdown");
    }

    public LoggerSettings setEnableAutoShutdown(boolean autoShutdown) {
        settings.put("autoshutdown", String.valueOf(autoShutdown));
        return this;
    }

    public Map<String, String> getSettings() {
        return settings;
    }
}