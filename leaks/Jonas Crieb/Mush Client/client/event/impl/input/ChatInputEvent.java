package client.event.impl.input;

import client.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class ChatInputEvent extends CancellableEvent {
    private String message;
}
