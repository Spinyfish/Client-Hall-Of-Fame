package de.verschwiegener.atero.animation;

public abstract class Animation extends Thread {
    
    
    public Animation() {
	setName("Animation-Thread");
	start();
    }
    
    private void reset() {}
    
    

}
