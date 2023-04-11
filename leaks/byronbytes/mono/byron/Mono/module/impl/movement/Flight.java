package byron.Mono.module.impl.movement;

import byron.Mono.Mono;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.MovementUtils;
import byron.Mono.utils.TimeUtil;

import com.google.common.eventbus.Subscribe;

import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

@ModuleInterface(name = "Flight", description = "Your dreams come true! Fly till the clouds!", category = Category.Movement)
public class Flight extends Module {
    double y;
    @Override
    public void setup() {
        super.setup();
        ArrayList<String> options = new ArrayList<>();
        options.add("Watchdog");
        options.add("Vanilla");
        options.add("NCP");
        options.add("Verus");
        options.add("Verus Test");
        rSetting(new Setting("Flight Mode", this, "Watchdog", options));
        rSetting(new Setting("Flight Speed", this, 1,1,3, false));
        rSetting(new Setting("Fake Damage Fly", this, false));
        rSetting(new Setting("Damage Fly", this, false));
        rSetting(new Setting("Flight Disabler Test", this, false));


    }
    private TimeUtil timeUtil = new TimeUtil();

    @Subscribe
    public void onUpdate(EventUpdate e)
    {
        switch(getSetting("Flight Mode").getValString())
        {
            case "Watchdog":
                hypixel();
                break;
            case "Vanilla":
                vanilla();
                break;
            case "NCP":
                ncp();
                break;
            case "Verus":
                verus();
                break;

            case "Verus Test":
                verusfast();
                break;
        }
    }

    private final void hypixel() {
        mc.thePlayer.motionY = 0;
        mc.thePlayer.onGround = true;
        MovementUtils.setMotion(0.40);
        }

    private final void vanilla() {
        mc.thePlayer.capabilities.isFlying = true;
    }

    private final void verus() {
    	
    	if(getSetting("Flight Disabler Test").getValBoolean())
    	{
    		/*
    		if (timeUtil.hasTimePassed(350))
        	{
        		mc.timer.timerSpeed = 0.25F;
        		
        		if (timeUtil.hasTimePassed(2000))
            	{
            		mc.timer.timerSpeed = 1.45F;
            	}
        	}
         	
    		if (timeUtil.hasTimePassed(3000))
        	{
        		timeUtil.reset();
        	}
        	*/
    		 MovementUtils.setSpeed(1.5D);
    	}
    	
    	 mc.thePlayer.motionY = 0;
    	 mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));	
    	
   
    }

    private final void verusfast() {
    	double posX = mc.thePlayer.posX;
    	double posY = mc.thePlayer.posY;
    	double posZ = mc.thePlayer.posZ;
        if (mc.thePlayer.posY < y)
            mc.thePlayer.onGround = true;
        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }
       this.mc.thePlayer.cameraYaw = -0.2f;
    }
    
    private final void ncp() {

    	mc.thePlayer.jump();
    	
    }

    @Override
    public void onEnable() {
    	if(getSetting("Fake Damage Fly").getValBoolean())
    	{
    	    mc.thePlayer.handleStatusUpdate((byte) 2);
    	}
    	
    	if(getSetting("Damage Fly").getValBoolean())
    	{
    	   MovementUtils.damageVerus();
    	}
    	y = mc.thePlayer.posY;
    	super.onEnable();
    }
    @Override
    public void onDisable()
    {
        super.onDisable();
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.capabilities.isFlying = false;
        MovementUtils.setSpeed(MovementUtils.defaultMoveSpeed());
       
        if(MovementUtils.isMovingOnGround())
        {
        	mc.thePlayer.onGround = true;
        }
        else
        {
        	mc.thePlayer.onGround = false;
        }
       
    }
}
