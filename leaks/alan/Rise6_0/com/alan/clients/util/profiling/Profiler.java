package com.alan.clients.util.profiling;

import lombok.Getter;

/**
 * @author Hazsi, Alan
 * @since 10/11/2022
 */
@Getter
public class Profiler {
    long totalTime = 0;
    long lastStart;
    long lastTime;

    public void start() {
        lastStart = System.nanoTime();
    }

    public void stop() {
        totalTime += System.nanoTime() - lastStart;
        start();
    }

    public void reset() {
        lastTime = totalTime;
        totalTime = 0;
    }

    public long getLastTotalTime() {
        return lastTime;
    }
}
