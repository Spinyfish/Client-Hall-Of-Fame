package kevin.utils;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

public class LogUtils {
    public static final Logger logger = Minecraft.logger;

    // A simple debug helper
    public static class DebugLogger {
        private DebugLogger parent;
        private DebugLogger child;
        private final String name;
        private boolean loggerEnabled = true;
        private byte step = 0;
        public DebugLogger(String name) {
            this.name = name;
        }

        public DebugLogger(String name, DebugLogger parent) {
            this.name = name;
            this.parent = parent;
        }

        public void setLoggerEnabled(boolean loggerEnabled) {
            this.loggerEnabled = loggerEnabled;
        }

        public boolean isLoggerEnabled() {
            return loggerEnabled;
        }

        public void step() { nextStep(); }

        public void nextStep() {
            nextStep(true);
        }

        public void nextStep(boolean count) {
            if (count) ++step;
            if (!isLoggerEnabled()) return;
            if (parent != null) {
                parent.nextStep(false);
                return;
            }
            StringBuilder sb = new StringBuilder();
            buildMessage(sb);
            logger.info(sb.toString());
        }

        public String getMessage() {
            return name + " | " + step;
        }

        public String getMessage(String msg) {
            return name + " | " + step + " " + msg;
        }

        public StringBuilder buildMessage(StringBuilder sb) {
            sb.append(getMessage());
            if (child != null) {
                sb.append(' ');
                child.buildMessage(sb);
            }
            return sb;
        }

        public DebugLogger child(String name) {
            if (child != null) return child.child(name);
            return child = new DebugLogger(name, this);
        }

        public boolean shrinkChild() {
            if (child != null) {
                // the child have no child
                if (!child.shrinkChild()) {
                    child = null;
                    parent = null;
                }
                return true;
            }
            return false;
        }

        public void clearChild() {
            if (child != null) {
                child.clearChild();
            }
            child = null;
        }

        public DebugLogger getChild() {
            return child;
        }

        public void close() {
            if (child != null) {
                child.kill();
            }
            kill();
        }

        public void kill() {
            if (child != null) child.kill();
            child = null;
            if (parent != null) parent.shrinkChild();
            parent = null;
            setLoggerEnabled(false);
        }
    }
}
