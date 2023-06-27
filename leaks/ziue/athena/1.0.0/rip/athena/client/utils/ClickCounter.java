package rip.athena.client.utils;

import java.util.*;

public class ClickCounter
{
    private final Queue<Long> clicks;
    
    public ClickCounter() {
        this.clicks = new LinkedList<Long>();
    }
    
    public void onClick() {
        this.clicks.add(System.currentTimeMillis() + 1000L);
    }
    
    public int getCps() {
        final long time = System.currentTimeMillis();
        while (!this.clicks.isEmpty() && this.clicks.peek() < time) {
            this.clicks.remove();
        }
        return this.clicks.size();
    }
}
