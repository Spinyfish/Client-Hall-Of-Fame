package dev.hera.client.utils;


public class Timer {

    public long lastMS;

    public Timer(){
        reset();
    }

    public void reset(){
        lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(long ms){
        return System.currentTimeMillis() - lastMS > ms;
    }

}