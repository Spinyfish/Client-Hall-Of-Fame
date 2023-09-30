package dev.tenacity.event;

import lombok.Getter;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class Event {
    @Getter
    private boolean cancelled;
    public void cancel() {
        this.cancelled = true;
    }

    public static class StateEvent extends Event {
        private boolean pre = true;

        @Exclude(Strategy.NAME_REMAPPING)
        public boolean isPre() { return pre;}
        @Exclude(Strategy.NAME_REMAPPING)
        public boolean isPost() { return !pre;}
        @Exclude(Strategy.NAME_REMAPPING)
        public void setPost() { pre = false; }
    }
}
