package dev.echo.listener.event;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class Event {
    private State state = State.PRE;
    private boolean cancelled;

    public static enum State {
        PRE, MID, POST;
    }

    public boolean isPre() {
        return this.state == State.PRE;
    }

    public boolean isMid() {
        return this.state == State.MID;
    }

    public boolean isPost() {
        return this.state == State.POST;
    }
}
