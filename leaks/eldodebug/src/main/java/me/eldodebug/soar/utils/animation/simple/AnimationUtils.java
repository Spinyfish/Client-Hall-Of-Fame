package me.eldodebug.soar.utils.animation.simple;

public class AnimationUtils {
	
    public static float calculateCompensation(final float target, float current, final double speed, long delta) {
    	
        final float diff = current - target;

        double add =  (delta * (speed / 50));
        
        if (diff > speed){
        	if(current - add > target) {
                current -= add;
        	}else {
                current = target;
        	}
        }
        else if (diff < -speed) {
        	if(current + add < target) {
                current += add;
        	}else {
                current = target;
        	}
        }
        else{
            current = target;
        }

        return current;
    }
}
