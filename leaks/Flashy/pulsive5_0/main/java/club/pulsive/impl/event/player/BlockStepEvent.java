package club.pulsive.impl.event.player;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BlockStepEvent extends Event {

    private EventState state;
    private float height;
    
    public boolean isPre(){
        return state == EventState.PRE;
    }
    
    public boolean isPost(){
        return state == EventState.POST;
    }
    
    public enum EventState {
        PRE,
        POST
    }
}