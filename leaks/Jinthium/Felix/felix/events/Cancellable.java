package felix.events;

public class Cancellable {

    private boolean canceled;

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean canceled) {
        this.canceled = canceled;
    }
}
