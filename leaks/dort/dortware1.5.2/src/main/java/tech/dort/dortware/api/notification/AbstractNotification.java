package tech.dort.dortware.api.notification;

public abstract class AbstractNotification {

    private final String message;
    private int x;
    private int y;
    private long endTime;

    public AbstractNotification(String message, int x, int y, long endTime) {
        this.message = message;
        this.x = x;
        this.y = y;
        this.endTime = endTime;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public abstract void update();

    protected String getMessage() {
        return message;
    }
}
