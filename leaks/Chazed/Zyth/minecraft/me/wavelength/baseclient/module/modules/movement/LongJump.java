
        package me.wavelength.baseclient.module.modules.movement;

        import java.util.Iterator;

        import org.lwjgl.input.Keyboard;

        import net.minecraft.util.BlockPos;
        import me.wavelength.baseclient.BaseClient;
        import me.wavelength.baseclient.command.commands.FriendsCommand;
        import me.wavelength.baseclient.event.events.PacketSentEvent;
        import me.wavelength.baseclient.event.events.UpdateEvent;
        import me.wavelength.baseclient.module.AntiCheat;
        import me.wavelength.baseclient.module.Category;
        import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.modules.exploit.Disabler;
import me.wavelength.baseclient.utils.MovementUtils;
import me.wavelength.baseclient.utils.Utils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
        import net.minecraft.client.entity.EntityPlayerSP;
        import net.minecraft.client.renderer.GlStateManager;
        import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
        import net.minecraft.entity.EntityLivingBase;
        import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
        import net.minecraft.network.play.client.C03PacketPlayer;
        import net.minecraft.network.play.client.C02PacketUseEntity.Action;
        import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
        import net.minecraft.util.ChatComponentText;
        import net.minecraft.util.EnumFacing;

        public class LongJump extends Module {

        public LongJump() {
        super("Longjump", "Makes you bounce.", Keyboard.KEY_G, Category.MOVEMENT, AntiCheat.VERUS, AntiCheat.HYPIXEL, AntiCheat.HYPIXELBOOST, AntiCheat.MATRIX);
        }

        int timepassed;

        @Override
        public void setup() {

        setToggled(false);
        }

        @Override
        public void onDisable() {
        mc.timer.timerSpeed = 1;

        }

        @Override
        public void onEnable() {
            timepassed = 0;
        if (this.antiCheat == AntiCheat.VERUS) {
        	

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(0, 80, 0, true));

        }

        
        if (this.antiCheat == AntiCheat.MMC) {
            timepassed = 0;
        	mc.thePlayer.jump();
        }


        if (this.antiCheat == AntiCheat.HYPIXEL) {
        	if(mc.thePlayer.onGround) {
        		Utils.hypixelDamage();
        		mc.thePlayer.motionY = 0.55;
        	}else {
        		toggle();
        	}
        	}
        
        if (this.antiCheat == AntiCheat.HYPIXELBOOST) {
        	if(mc.thePlayer.onGround) {
        		Utils.hypixelDamage(); 
        		mc.thePlayer.motionY = 0.6;
        	}else {
        		toggle();
        	}
        	}
        
        
        }
        
        
        
        @Override
        public void onUpdate(UpdateEvent event) {
        timepassed++;
        if (this.antiCheat == AntiCheat.VERUS) {
        	

            if(timepassed >= 60) {
            	toggle();
            }
        }

        if (this.antiCheat == AntiCheat.HYPIXEL) {
        	
        	if(timepassed >= 2)
        		//MovementUtils.setMotion(0.325);
        	
        	
        	
        if(timepassed >= 3 && mc.thePlayer.isCollided) {
        	//toggle();
        
        	}
        
        }
        
        if (this.antiCheat == AntiCheat.HYPIXELBOOST) {
        	mc.timer.timerSpeed = 0.75;
        	if(timepassed >= 2 && timepassed < 10)
        		MovementUtils.setMotion(1.2);

        	
        	if(timepassed >= 10 && timepassed < 14)
        		MovementUtils.setMotion(0.8);
        	
        	if(timepassed >= 14)
        		MovementUtils.setMotion(0.4);
        	
        	
        if(timepassed >= 10 && mc.thePlayer.isCollided) {
        toggle();
        
        	}
        
        }
        
        
        //funni atrix TP
        if (this.antiCheat == AntiCheat.MATRIX) {
        	mc.thePlayer.motionY = 0.01;
			mc.thePlayer.addChatComponentMessage(new ChatComponentText("\u00a75Zyth\u00a78 > \u00a7f !" + timepassed));
        	mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 10, mc.thePlayer.posZ);

        	

                }

            }
        }