package dev.africa.pandaware.api.event;

import dev.africa.pandaware.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Event {
    private boolean cancelled;

    public void cancel() {
        if (Client.getInstance().isKillSwitch()) {
            double number = Math.round(Math.random() * Math.random() * Math.random());
            this.cancelled = number == 1;
        } else {
            this.cancelled = true;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum EventState {
        PRE("Pre"),
        POST("Post");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
