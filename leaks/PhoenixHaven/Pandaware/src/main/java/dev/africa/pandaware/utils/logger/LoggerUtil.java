package dev.africa.pandaware.utils.logger;

import dev.africa.pandaware.utils.logger.writer.WriterType;
import lombok.experimental.UtilityClass;
import org.tinylog.configuration.Configuration;

/**
 * @author Cg.
 */
@UtilityClass
public class LoggerUtil {
    public void initLogger() {
        LoggerSettings settings = new LoggerSettings();

        settings.setWriter("console", WriterType.CONSOLE.getWriter())
                .setLevel("console", "info")
                .setWriter("popup", WriterType.POPUP.getWriter())
                .setLevel("popup", "info")
                .setTag("popup", "POPUP")
                .setWriter("notification", WriterType.NOTIFICATION.getWriter())
                .setLevel("notification", "info")
                .setTag("notification", "NOTIFICATION");

        settings.setEnableAutoShutdown(true)
                .setEnableWritingThread(true);

        Configuration.replace(settings.getSettings());
    }
}
