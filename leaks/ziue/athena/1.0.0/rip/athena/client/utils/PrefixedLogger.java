package rip.athena.client.utils;

import org.apache.logging.log4j.*;

public class PrefixedLogger
{
    private final Logger logger;
    
    public PrefixedLogger(final String prefix) {
        this.logger = LogManager.getLogger();
        ThreadContext.put("LOG_PREFIX", prefix);
    }
    
    public void info(final String message) {
        this.logger.log(Level.INFO, "[{}] {}", new Object[] { ThreadContext.get("LOG_PREFIX"), message });
    }
    
    public void warn(final String message) {
        this.logger.log(Level.WARN, "[{}] {}", new Object[] { ThreadContext.get("LOG_PREFIX"), message });
    }
    
    public void error(final String message) {
        this.logger.log(Level.ERROR, "[{}] {}", new Object[] { ThreadContext.get("LOG_PREFIX"), message });
    }
}
