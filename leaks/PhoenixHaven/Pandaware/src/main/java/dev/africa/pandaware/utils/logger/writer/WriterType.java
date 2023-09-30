package dev.africa.pandaware.utils.logger.writer;

import dev.africa.pandaware.utils.logger.writer.impl.ConsoleWriter;
import dev.africa.pandaware.utils.logger.writer.impl.NotificationWriter;
import dev.africa.pandaware.utils.logger.writer.impl.PopupWriter;
import org.tinylog.writers.Writer;

/**
 * @author Cg.
 */
public enum WriterType {
    CONSOLE(ConsoleWriter.class),
    POPUP(PopupWriter.class),
    NOTIFICATION(NotificationWriter.class);

    private final Class<? extends Writer> writerClass;

    WriterType(Class<? extends Writer> writerClass) {
        this.writerClass = writerClass;
    }

    public String getWriter() {
        return writerClass.getName();
    }
}