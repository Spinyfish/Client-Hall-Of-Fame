package club.mineman.antigamingchair.log;

public class Log {
    private final long timestamp;
    private final int minemanId;
    private final String log;

    public Log(final int minemanId, final String log) {
        this.timestamp = System.currentTimeMillis();
        this.minemanId = minemanId;
        this.log = log;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public int getMinemanId() {
        return this.minemanId;
    }

    public String getLog() {
        return this.log;
    }
}
