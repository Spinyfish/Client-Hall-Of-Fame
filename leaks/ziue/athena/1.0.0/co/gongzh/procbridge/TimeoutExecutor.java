package co.gongzh.procbridge;

import org.jetbrains.annotations.*;
import java.util.concurrent.*;
import java.util.*;

final class TimeoutExecutor implements Executor
{
    private final long timeout;
    @Nullable
    private final Executor base;
    
    TimeoutExecutor(final long timeout, @Nullable final Executor base) {
        this.timeout = timeout;
        this.base = base;
    }
    
    public long getTimeout() {
        return this.timeout;
    }
    
    @Nullable
    public Executor getBaseExecutor() {
        return this.base;
    }
    
    @Override
    public void execute(@NotNull final Runnable task) throws TimeoutException {
        final Semaphore semaphore = new Semaphore(0);
        final boolean[] isTimeout = { false };
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isTimeout[0] = true;
                semaphore.release();
            }
        }, this.timeout);
        final Semaphore semaphore2;
        final Runnable runnable = () -> {
            try {
                task.run();
            }
            finally {
                semaphore2.release();
            }
            return;
        };
        if (this.base != null) {
            this.base.execute(runnable);
        }
        else {
            new Thread(runnable).start();
        }
        try {
            semaphore.acquire();
            if (isTimeout[0]) {
                throw new TimeoutException();
            }
        }
        catch (InterruptedException ex) {}
        finally {
            timer.cancel();
        }
    }
}
