// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.openal;

public class NullAudio implements Audio
{
    public int getBufferID() {
        return 0;
    }
    
    public float getPosition() {
        return 0.0f;
    }
    
    public boolean isPlaying() {
        return false;
    }
    
    public int playAsMusic(final float pitch, final float gain, final boolean loop) {
        return 0;
    }
    
    public int playAsSoundEffect(final float pitch, final float gain, final boolean loop) {
        return 0;
    }
    
    public int playAsSoundEffect(final float pitch, final float gain, final boolean loop, final float x, final float y, final float z) {
        return 0;
    }
    
    public boolean setPosition(final float position) {
        return false;
    }
    
    public void stop() {
    }
}
