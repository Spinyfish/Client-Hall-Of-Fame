package club.pulsive.impl.event.client;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShaderEvent extends Event {

    private ShaderType shaderType;
    
    public boolean isBlur(){
        return shaderType == ShaderType.BLUR;
    }
    
    public boolean isShadow(){
        return shaderType == ShaderType.SHADOW;
    }

    public boolean isGlow(){
        return shaderType == ShaderType.GLOW;
    }
    
    public enum ShaderType{
        BLUR, SHADOW, GLOW
    }
}
