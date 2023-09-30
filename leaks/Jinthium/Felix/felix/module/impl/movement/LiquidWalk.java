package felix.module.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketReceive;
import felix.events.player.EventBoundingBox;
import felix.events.player.EventJump;
import felix.events.player.EventLiquidCollide;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.util.other.PlayerUtil;
import felix.util.other.TimeHelper;
import felix.util.player.MovementUtils;
import felix.value.impl.EnumValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class LiquidWalk extends Module {
	
	int stage, water;

	private TimeHelper timer = new TimeHelper();
	
	private EnumValue<Mode> mode = new EnumValue<>("LiquidWalk Mode", Mode.Solid);
	
    public LiquidWalk() {
    	super("LiquidWalk", 0, ModuleCategory.MOVEMENT);
    	addValues(mode);
    }
    
    private enum Mode {
    	Solid, Dolphin;
    }
    
    @Override
    public void onEnable(){
    	super.onEnable();
    	stage = 0;
    	water = 0;
    }
    
    @Override
    public void onDisable() {
    	super.onDisable();
    }
    
    @Handler
    public void onReceivePacket(final EventPacketReceive event) {
    	Packet p = event.getPacket();
    	if(p instanceof S08PacketPlayerPosLook){
    		stage = 0;
    	}
    }
    
    @Handler
    public void onJump(final EventJump event) {
    	if(PlayerUtil.isOnLiquid(0.001))
    	if(PlayerUtil.isTotalOnLiquid(0.001) && mc.thePlayer.onGround && !mc.thePlayer.isInWater()){
    	    event.setCancelled(mc.thePlayer.ticksExisted % 2 != 0);   	
    	}
    }
    
    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
    	setSuffix(mode.getValueAsString());
    	boolean ncp = mode.getValue().equals(Mode.Solid);
    	boolean dolphin = mode.getValue().equals(Mode.Dolphin);
    	if(!event.isPre())
    		return;
    	boolean sh = shouldJesus();
    	if(mc.thePlayer.isInWater() && !mc.thePlayer.isSneaking() && sh){
    		mc.thePlayer.motionY = 0.09;
    	}
    	if(ncp){
    		if(PlayerUtil.isOnLiquid(0.001))
    	    if(PlayerUtil.isTotalOnLiquid(0.001) && mc.thePlayer.onGround && !mc.thePlayer.isInWater()){
    	    	event.setPosY(event.getPosY() + (mc.thePlayer.ticksExisted % 2 == 0 ? 0.0000000001D : -0.000000000001D));
    	    }
    	}

    	if(dolphin){    
    	if(mc.thePlayer.onGround && !mc.thePlayer.isInWater() && sh){
    		stage = 1;
			timer.reset();
    	}
    	
    	if(stage > 0 && !timer.reach(2500)){
    		if((mc.thePlayer.isCollidedVertically  && !MovementUtils.isOnGround(0.001)) || mc.thePlayer.isSneaking()){
    			stage = -1;
    		}
    		mc.thePlayer.motionX *= 0;
    		mc.thePlayer.motionZ *= 0;
    		if(!PlayerUtil.isInLiquid() && !mc.thePlayer.isInWater()){
    			MovementUtils.setMotion(0.25 + MovementUtils.getSpeedEffect() * 0.05);
    		}
    		double motionY = getMotionY(stage);
    		if(motionY != -999) {
    			mc.thePlayer.motionY = motionY;
    		}		
    		stage +=1;
    		}
    	}
    }
    
    @Handler
    public void onLiquidCollide(final EventLiquidCollide event) {
    	boolean ncp = mode.getValue().equals(Mode.Solid);
    	int nigga = -1; 
        if (event.getPos().getY() + 0.9 < mc.thePlayer.boundingBox.minY) {
            if(nigga <= 4){
            	event.setBounds(new AxisAlignedBB(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getPos().getX() + 1, event.getPos().getY() + (ncp? 1 : 0.999), event.getPos().getZ() + 1));
            	event.setCancelled(shouldSetBoundingBox());
            }
    	}   
    }
    	
    boolean shouldJesus(){
    	double x = mc.thePlayer.posX; double y = mc.thePlayer.posY; double z = mc.thePlayer.posZ;
    	ArrayList<BlockPos>pos = new ArrayList<BlockPos>(Arrays.asList(new BlockPos(x + 0.3, y, z+0.3),
    			 new BlockPos(x - 0.3, y, z+0.3),new BlockPos(x + 0.3, y, z-0.3),new BlockPos(x - 0.3, y, z-0.3)));
    	for(BlockPos po : pos){
    		if(!(mc.theWorld.getBlockState(po).getBlock() instanceof BlockLiquid))
    			continue;
    		if(mc.theWorld.getBlockState(po).getProperties().get(BlockLiquid.LEVEL) instanceof Integer){
        		if((int)mc.theWorld.getBlockState(po).getProperties().get(BlockLiquid.LEVEL) <= 4){
        			return true;
        		}
        	}
    	}
    	return false;
    }
    public double getMotionY(double stage){
    	stage --;
    	double[] motion = new double[]{0.500,0.484,0.468,0.436,0.404,0.372,0.340,0.308,0.276,0.244,0.212,0.180,0.166,0.166,
    			0.156,0.123,0.135,0.111,0.086,0.098,0.073,0.048,0.06,0.036,0.0106,0.015,0.004,0.004,0.004,0.004,
    					-0.013,-0.045,-0.077,-0.109};
    	if(stage < motion.length && stage >= 0)
    		return motion[(int)stage];
    	else
    		return -999;
    	
    }
    private boolean shouldSetBoundingBox() { 	
        return (!mc.thePlayer.isSneaking()) && (mc.thePlayer.fallDistance < 12.0F);
    }
}
