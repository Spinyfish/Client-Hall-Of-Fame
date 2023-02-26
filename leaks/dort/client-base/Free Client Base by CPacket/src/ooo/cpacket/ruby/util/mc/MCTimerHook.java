package ooo.cpacket.ruby.util.mc;

import net.minecraft.util.Timer;

public class MCTimerHook extends Timer
{
	
    public static double timerSpeed = 1.0;
    
    public MCTimerHook(float speed)
    {
    	super(speed);
    }
    
    public void updateTimer()
    {
    	super.updateTimer(this.timerSpeed);
    }
}
