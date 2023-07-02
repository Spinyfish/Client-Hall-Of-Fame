package me.eldodebug.soar.utils.animation.simple;

public class SimpleAnimation {
	
    private float value;
    private long lastMS;
    
    public SimpleAnimation(final float value){
    	this.value = value;
        this.lastMS = System.currentTimeMillis();
    }
    
    public void setAnimation(final float value, double speed){
    	
        final long currentMS = System.currentTimeMillis();
        final long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        
        double deltaValue = 0.0;

        if(speed > 28) {
        	speed = 28;
        }
        
        if (speed != 0.0)
        {
        	deltaValue = Math.abs(value - this.value) * 0.35f / (10.0 / speed);
        }
        
        this.value = AnimationUtils.calculateCompensation(value, this.value, deltaValue, delta);
    }

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
}
