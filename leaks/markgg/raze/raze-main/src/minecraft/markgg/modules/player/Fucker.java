package markgg.modules.player;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Fucker extends Module{	

	public ModeSetting mode = new ModeSetting("Block", this, "Beds", "Beds", "Eggs", "All");
	public NumberSetting range = new NumberSetting("Range", this, 3, 1, 6, 1);
	public BooleanSetting noSwing = new BooleanSetting("No Swing", this, false);

	public Fucker() {
		super("Fucker", "Clips you up", 0, Category.PLAYER);
		addSettings(mode, range, noSwing);
	}
	
	public void onEvent(Event e) {
		if (e instanceof EventUpdate) {
			for (int n = (int)range.getValue(), i = -n; i < n; ++i) {
				for (int j = n; j > -n; --j) {
					for (int k = -n; k < n; ++k) {
						int n2 = (int)this.mc.thePlayer.posX + i;
                        int n3 = (int)this.mc.thePlayer.posY + j;
                        int n4 = (int)this.mc.thePlayer.posZ + k;
                        BlockPos blockPos = new BlockPos(n2, n3, n4);
                        Block block = this.mc.theWorld.getBlockState(blockPos).getBlock();
                        switch(mode.getMode()) {
                        case "Beds":
                        	if(block instanceof BlockBed) {
                        		if(noSwing.isEnabled()) {
                        			mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                        		} else {
                        			mc.thePlayer.swingItem();
                        			mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                        		}
                        	}
                        	break;
                        case "Eggs":
                        	if(block instanceof BlockDragonEgg) {
                        		if(noSwing.isEnabled()) {
                        			mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                        		} else {
                        			mc.thePlayer.swingItem();
                        			mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                        		}
                        	}
                        	break;
                        case "All":
                        	if(block instanceof BlockBed || block instanceof BlockDragonEgg ) {
                        		if(noSwing.isEnabled()) {
                        			mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                        		} else {
                        			mc.thePlayer.swingItem();
                        			mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                        		}
                        	}
                        	break;
                        }
					}
				}
			}
		}
	}
	
	
}
