package intentions.modules.world;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.settings.BooleanSetting;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.BlockUtils;
import intentions.util.RotationUtils;
import intentions.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class Scaffold extends Module {
	
	private final List validBlocks;
	   private final Random rng;
	   private final List invalidBlocks;
	   private final EnumFacing[] facings;
	   public boolean rel = false;
	   private final BlockPos[] blockPositions;
	   private boolean rotating;
	   private int slot;
	   private float[] angles;

	public static boolean scaffold = false;
	public boolean cooldown = false;
	public BooleanSetting eagle = new BooleanSetting("Eagle", false), timerTower = new BooleanSetting("Timer", false);;
	public static NumberSetting delay = new NumberSetting("Delay", 1, 0, 50, 1);
	
	public static ModeSetting mode = new ModeSetting("Mode", "V1", new String[] {"V2", "V1", "Hypixel"});
	
	public int t, t2;
	
	public boolean rotated = false;
  
    public Scaffold() {
        super("Scaffold", Keyboard.KEY_X, Category.WORLD, "Automatically builds below your feet", true);
        this.addSettings(eagle, delay, mode, timerTower);
        this.invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web);
        this.validBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava);
        this.blockPositions = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1)};
        this.facings = new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.NORTH};
        this.rng = new Random();
        this.angles = new float[2];
    }
  
    public void onEnable() {
        scaffold = true;
        t=0;
        t2=0;
        if(!mode.getMode().equalsIgnoreCase("V2")) return;
        this.slot = mc.thePlayer.inventory.currentItem;
    }
    
    public Timer timer = new Timer();
  
    public void onDisable() {
        scaffold = false;
        setSneaking(false);
        
        mc.timer.timerSpeed = 1f;
        
        if(!mode.getMode().equalsIgnoreCase("V2")) return;
        this.mc.thePlayer.inventory.currentItem = this.slot;
        this.mc.gameSettings.keyBindUseItem.pressed = false;
    }
    
    
  
    public void onEvent(Event e) {
    	if(mode.getMode().equalsIgnoreCase("V2")) {
    		onEvent1(e);
    		return;
    	}
        if (e instanceof EventMotion) {
        	
        	if(eagle.isEnabled()) {
        		if(rotated) {
        			setSneaking(true);
        		} else {
        			setSneaking(false);
        		}
        	}
        	if(mode.getMode().equalsIgnoreCase("Hypixel")) {
        		mc.timer.timerSpeed = 0.75f;
        		mc.thePlayer.setSprinting(false);
        	}
        	BlockPos playerBlock = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ);
        	
        	if(mc.theWorld.isAirBlock(playerBlock.add(0, -1, 0))) {
        		if(isValidBlock(playerBlock.add(0, -2, 0))) {
        			place(playerBlock.add(0, -1, 0), EnumFacing.UP);
        		} else if(isValidBlock(playerBlock.add(-1, -1, 0))) {
        			place(playerBlock.add(0, -1, 0), EnumFacing.EAST);
        		} else if(isValidBlock(playerBlock.add(1, -1, 0))) {
        			place(playerBlock.add(0, -1, 0), EnumFacing.WEST);
        		} else if(isValidBlock(playerBlock.add(0, -1, -1))) {
        			place(playerBlock.add(0, -1, 0), EnumFacing.SOUTH);
        		} else if(isValidBlock(playerBlock.add(0, -1, 1))) {
        			place(playerBlock.add(0, -1, 0), EnumFacing.NORTH);
        			
        			
        		} else if(isValidBlock(playerBlock.add(1, -1, 1))) {
        			
        			if(isValidBlock(playerBlock.add(0, -1, 1))) {
        				
        				place(playerBlock.add(0, -1, 1), EnumFacing.NORTH);
        				
        			}
        			else
        			place(playerBlock.add(0, -1, 0), EnumFacing.EAST);
        			
        		} else if(isValidBlock(playerBlock.add(1, -1, 1))) {
        			
        			if(isValidBlock(playerBlock.add(-1, -1, 0))) {
        				
        				place(playerBlock.add(-1, -1, 0), EnumFacing.WEST);
        				
        			}
        			else
        			place(playerBlock.add(-1, -1, 1), EnumFacing.SOUTH);
        			
        			
        			
        		} else if(isValidBlock(playerBlock.add(-1, -1, -1))) {
        			
        			if(isValidBlock(playerBlock.add(0, -1, -1))) {
        				
        				place(playerBlock.add(0, -1, -1), EnumFacing.SOUTH);
        				
        			}
        			else
        			place(playerBlock.add(-1, -1, -1), EnumFacing.WEST);
        			
        			
        			
        		} else if(isValidBlock(playerBlock.add(0, -1, -1))) {
        			
        			if(isValidBlock(playerBlock.add(1, -1, 0))) {
        				
        				place(playerBlock.add(1, -1, 0), EnumFacing.EAST);
        				
        			}
        			else
        			place(playerBlock.add(0, -1, -1), EnumFacing.NORTH);
        		}
        	}
        	
        }
    }
    
    private void setSneaking(boolean b) {
		KeyBinding sneakBinding = mc.gameSettings.keyBindSneak;
		try {
			Field field = sneakBinding.getClass().getDeclaredField("pressed");
			field.setAccessible(true);
			field.setBoolean(sneakBinding, b);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void place(BlockPos pos, EnumFacing face) {
    	cooldown = true;
    	
    	
    	if(face == EnumFacing.UP) {
    		pos = pos.add(0, -1, 0);
    	} else if (face == EnumFacing.NORTH) {
    		pos = pos.add(0, 0, 1);
    	} else if (face == EnumFacing.EAST) {
    		pos = pos.add(-1, 0, 0);
    	} else if (face == EnumFacing.SOUTH) {
    		pos = pos.add(0, 0, -1);
    	} else if (face == EnumFacing.WEST) {
    		pos = pos.add(1, 0, 0);
    	}
    	
    	
    	if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
    		
    		rotated = true;
    		
    		if(!timer.hasTimeElapsed((long) (delay.getValue() * 50), true)) return;

    		rotated = false;
    		
    		float[] rotations = BlockUtils.getFacePos(BlockUtils.getVec3(pos));
    		float yaw = rotations[0];
    		float pitch = rotations[1];
    		
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, yaw, pitch, mc.thePlayer.onGround));
    	
			mc.thePlayer.swingItem();
			mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, face, new Vec3(0.5D, 0.5D, 0.5D));
    	}
    }
    
    private boolean isValidBlock(BlockPos pos) {
    	return !(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid) && mc.theWorld.getBlockState(pos).getBlock().getMaterial() != Material.air;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

     private Vec3 getVec3(BlockUtils.BlockData var1) {
        BlockPos var2 = var1.position;
        EnumFacing var3 = var1.face;
        double var4 = (double)var2.getX() + 0.5D;
        double var6 = (double)var2.getY() + 0.5D;
        double var8 = (double)var2.getZ() + 0.5D;
        var4 += (double)var3.getFrontOffsetX() / 2.0D;
        var8 += (double)var3.getFrontOffsetZ() / 2.0D;
        var6 += (double)var3.getFrontOffsetY() / 2.0D;
        if (var3 != EnumFacing.UP && var3 != EnumFacing.DOWN) {
           var6 += this.randomNumber(0.49D, 0.5D);
        } else {
           var4 += this.randomNumber(0.3D, -0.3D);
           var8 += this.randomNumber(0.3D, -0.3D);
        }

        if (var3 == EnumFacing.WEST || var3 == EnumFacing.EAST) {
           var8 += this.randomNumber(0.3D, -0.3D);
        }

        if (var3 == EnumFacing.SOUTH || var3 == EnumFacing.NORTH) {
           var4 += this.randomNumber(0.3D, -0.3D);
        }

        return new Vec3(var4, var6, var8);
     }

     public void onEvent1(Event var1) {
        if (var1 instanceof EventMotion) {
        	
          if(!mode.getMode().equalsIgnoreCase("V2")) return;
        	
           EventMotion var2 = (EventMotion)var1;
           EntityPlayerSP var3 = this.mc.thePlayer;
           WorldClient var4 = this.mc.theWorld;
           double var5 = 1.0D;
           BlockUtils.BlockData var7 = null;

           for(double var8 = var3.posY - 1.0D; var8 > 0.0D; --var8) {
              BlockUtils.BlockData var10 = this.getBlockData(new BlockPos(var3.posX, var8, var3.posZ));
              if (var10 != null) {
                 var5 = var3.posY - var8;
                 if (var5 <= 3.0D) {
                    var7 = var10;
                    break;
                 }
              }
           }

           float[] var13 = RotationUtils.getRotationFromPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ);
           if (var7 != null) {
        	   
        	   rotated = true;
       		
       		t2++;
       		
       		if(t2 < delay.getValue()) return;

       		rotated = false;
       		
       		t2 = 0;
        	   
              BlockPos var9 = var7.position;
              Block var12 = var4.getBlockState(var9.offset(var7.face)).getBlock();
              Vec3 var11 = this.getVec3(var7);
              this.mc.playerController.func_178890_a(var3, var4, var3.getCurrentEquippedItem(), var9, var7.face, var11);
              this.mc.playerController.func_178890_a(var3, var4, var3.getCurrentEquippedItem(), var9, var7.face, var11);
              this.mc.playerController.func_178890_a(var3, var4, var3.getCurrentEquippedItem(), var9, var7.face, var11);
              this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ);
           }
        }

        if (var1 instanceof EventUpdate) {
           this.mc.timer.timerSpeed = 1.1F;
        }

     }

     private double randomNumber(double var1, double var3) {
        return Math.random() * (var1 - var3) + var3;
     }

     private BlockUtils.BlockData getBlockData(BlockPos var1) {
        BlockPos[] var2 = this.blockPositions;
        EnumFacing[] var3 = this.facings;
        WorldClient var4 = this.mc.theWorld;
        BlockPos var5 = new BlockPos(0, -1, 0);
        List var6 = this.validBlocks;
        if (!var6.contains(var4.getBlockState(var1.add(var5)).getBlock())) {
           return new BlockUtils.BlockData(var1.add(var5), EnumFacing.UP);
        } else {
           int var7 = 0;

           for(int var8 = var2.length; var7 < var8; ++var7) {
              BlockPos var9 = var1.add(var2[var7]);
              if (!var6.contains(var4.getBlockState(var9).getBlock())) {
                 return new BlockUtils.BlockData(var9, var3[var7]);
              }

              for(int var10 = 0; var10 < var8; ++var10) {
                 BlockPos var11 = var1.add(var2[var10]);
                 BlockPos var12 = var9.add(var2[var10]);
                 if (!var6.contains(var4.getBlockState(var11).getBlock())) {
                    return new BlockUtils.BlockData(var11, var3[var10]);
                 }

                 if (!var6.contains(var4.getBlockState(var12).getBlock())) {
                    return new BlockUtils.BlockData(var12, var3[var10]);
                 }
              }
           }

           return null;
        }
     }
    
}
