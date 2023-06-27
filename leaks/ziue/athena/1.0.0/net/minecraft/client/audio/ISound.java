package net.minecraft.client.audio;

import net.minecraft.util.*;

public interface ISound
{
    ResourceLocation getSoundLocation();
    
    boolean canRepeat();
    
    int getRepeatDelay();
    
    float getVolume();
    
    float getPitch();
    
    float getXPosF();
    
    float getYPosF();
    
    float getZPosF();
    
    AttenuationType getAttenuationType();
    
    public enum AttenuationType
    {
        NONE(0), 
        LINEAR(2);
        
        private final int type;
        
        private AttenuationType(final int typeIn) {
            this.type = typeIn;
        }
        
        public int getTypeInt() {
            return this.type;
        }
    }
}
