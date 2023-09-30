package intentions.modules.player;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.dto.RealmsServer.McoServerComparator;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.settings.ModeSetting;
import intentions.util.BlockUtils;
import intentions.util.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class Jesus extends Module {
  public static ModeSetting mode = new ModeSetting("Mode", "None", new String[] {"Normal", "NCP","ACD"});
  
  public Jesus() {
    super("Jesus", Keyboard.KEY_NONE, Module.Category.PLAYER, "Allows you to walk on water", true);
    addSettings(mode);
  }
  public static boolean jesus;
  // public int pitch, yaw;
  
  public void onEnable() {
	  jesus = true;
  }
  public void onDisable() {
	  jesus = false;
  }
  
  public void onEvent(Event e) {
	  if(e instanceof EventUpdate) {
		  try {
			  if(mode.getMode().equalsIgnoreCase("ACD")) {
				//this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ, false));
			  }
			  if(BlockUtils.isLiquidBlock(PlayerUtil.getBlockAt())) {
			    mc.thePlayer.motionY += 0F + ((mode.getMode().equalsIgnoreCase("NCP")) ? 0.5F : (mc.thePlayer.isSneaking()) ? 0F : 0.05F);
			    if (!mode.getMode().equalsIgnoreCase("NCP")) return;
			    mc.thePlayer.motionY = Math.min(mc.thePlayer.motionY, 0.25F);
			    mc.gameSettings.keyBindJump.pressed = false;
			  }
		  }catch(Throwable t) {

		  }
	  } else if (e instanceof EventMotion) {
		  if(mode.getMode().equalsIgnoreCase("ACD")) {
			  
			  if(mc.thePlayer.isInLiquid || mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.3, mc.thePlayer.posZ)).getBlock() != Blocks.water) return;
			  
			  ((EventMotion) e).setY(Math.floor(mc.thePlayer.posY) + (mc.thePlayer.ticksExisted % 2 == 0 ? -0.1 : -0.2));
			  ((EventMotion) e).setOnGround(mc.thePlayer.ticksExisted % 2 != 0);
		  }
	  }
  }
}
