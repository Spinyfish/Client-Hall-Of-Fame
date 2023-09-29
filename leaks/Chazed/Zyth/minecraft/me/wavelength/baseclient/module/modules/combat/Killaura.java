package me.wavelength.baseclient.module.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;

import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.extensions.DiscordRP;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;

public class Killaura extends Module {

    public Killaura() {
        super("Killaura", "Hit people.", 0, Category.COMBAT, AntiCheat.VANILLA, AntiCheat.AAC);
    }
    

    int auradelay;
	private Object itemToRender;
	public static boolean fake;
	public static String name;
	public static boolean attacking;
	public static double health;
	public static int hp;
	public static EntityLivingBase hurt;
	public static int derpo;

    @Override
    public void setup() {
        moduleSettings.addDefault("range", 3.5D);
        moduleSettings.addDefault("tick_delay", 1.0D);
        moduleSettings.addDefault("Autoblock", true);
        moduleSettings.addDefault("FakeAutoBlock", true);
        moduleSettings.addDefault("PlayerController", true);
        moduleSettings.addDefault("KeepSprint", true);
        moduleSettings.addDefault("Derp", true);
        setToggled(false);
    }
    public void onDisable(){
    	name = null;
        attacking = false;
    	if(mc.gameSettings.keyBindUseItem.isKeyDown())
    		mc.gameSettings.keyBindUseItem.setPressed(false);
    }

    public void onPacketSent(PacketSentEvent event) {
    	
    	if(this.moduleSettings.getBoolean("Derp")) {
        	if(event.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
        		C05PacketPlayerLook C05 = (C05PacketPlayerLook)event.getPacket();
        		if(C05.getYaw() != derpo) {
        			event.setCancelled(true);
                	mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(derpo, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
        		}
        	}
    	}

    }
    
    @Override
    public void onUpdate(UpdateEvent event) {
    	derpo = derpo + 10;
    	
    	if(derpo >= 360) {
    		derpo = 0;
    	}
    	
    	if(this.moduleSettings.getBoolean("Derp")) {

    	}
		
    	//Fake AutoBlock incase real is flagging :shrug:
    	if(this.moduleSettings.getBoolean("FakeAutoBlock") == true) {
    		fake = true;
    	}else {
    		fake = false;
    	}
    	
        //Define the entity.
        for(Iterator<Entity> entities = mc.theWorld.loadedEntityList.iterator(); entities.hasNext();) {
            Object theObject = entities.next();
            if(theObject instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) theObject;
                if((EntityLivingBase) theObject instanceof EntityPlayerSP) continue;
                
                



                if(mc.thePlayer.getDistanceToEntity(entity) <= this.moduleSettings.getDouble("range") && !entity.isInvisibleToPlayer(mc.thePlayer)) {
                    attacking = true;
                	hurt = entity;
                    name = entity.getName();
                	health = entity.getHealth();
                    if(Criticals.toggled && Criticals.mode == 2) {
                    if(entity.hurtTime <= 2 || entity.hurtTime >= 9) {
                    	Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + 0.565, Minecraft.getMinecraft().thePlayer.posZ, false));
                    	 Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - 1.2, Minecraft.getMinecraft().thePlayer.posZ, false));
                    	Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, true));
                        }
                    }
                    
                    if(AntiBot.enabled == true && AntiBot.mode == 1) {
                    	if(entity.ticksExisted >= 100) {

                    //keepsprint toggle
                    if(this.moduleSettings.getBoolean("KeepSprint") == true) {
                    	mc.thePlayer.setSprinting(true);

                    }else {
                    	mc.thePlayer.setSprinting(false);
                    }


                	//Wait for delay in ticks to attack the entity..
                    if(auradelay >= this.moduleSettings.getDouble("tick_delay")/mc.timer.timerSpeed) {
                        if(Criticals.toggled && Criticals.mode == 1) {
                            if (mc.thePlayer.onGround) {
                                if (mc.thePlayer.isCollided)
                                    mc.thePlayer.motionY = 0.01f;
                            }
                        }
                        mc.thePlayer.swingItem();

                        //Option for playercontroller because yes
                        if(this.moduleSettings.getBoolean("PlayerController") == true) {
                        mc.playerController.attackEntity(mc.thePlayer, entity);
                        }else {
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, Action.ATTACK));
                        }
                        
                        auradelay = 0;


                    		}
                        }
                    }else {
                        //keepsprint toggle
                        if(this.moduleSettings.getBoolean("KeepSprint") == true) {
                        	mc.thePlayer.setSprinting(true);

                        }else {
                        	mc.thePlayer.setSprinting(false);
                        }


                    	//Wait for delay in ticks to attack the entity..
                        if(auradelay >= this.moduleSettings.getDouble("tick_delay")/mc.timer.timerSpeed) {
                            if(Criticals.toggled && Criticals.mode == 1) {
                                if (mc.thePlayer.onGround) {
                                    if (mc.thePlayer.isCollided)
                                        mc.thePlayer.motionY = 0.01f;
                                }
                            }
                            mc.thePlayer.swingItem();

                            //Option for playercontroller because yes
                            if(this.moduleSettings.getBoolean("PlayerController") == true) {
                            mc.playerController.attackEntity(mc.thePlayer, entity);
                            }else {
                            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, Action.ATTACK));
                            }
                            
                            auradelay = 0;


                        		}
                    }
                    


                }

                

                
            	//Autoblock (almost every server xD)
                if(!entity.isDead){
                	if(this.moduleSettings.getBoolean("Autoblock") == true)
                			//mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), 2);
            			mc.gameSettings.keyBindUseItem.setPressed(true);
                		else {

                			mc.gameSettings.keyBindUseItem.setPressed(false);
                		}

                	
                	}else {
        			mc.gameSettings.keyBindUseItem.setPressed(false);
                }

                auradelay++;


            }else {
            }

		}
    }

}