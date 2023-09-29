package de.verschwiegener.atero.module.modules.world;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventReceivedPacket;
import net.minecraft.client.Minecraft;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;

public class Disabler extends Module {
    TimeUtils timeUtils;


    public Disabler() {
        super("Disabler", "Disabler", Keyboard.KEY_NONE, Category.World);
    }

    public void onEnable() {

        super.onEnable();
    }

    public void onDisable() {

        super.onDisable();
    }




    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    @EventTarget
    public void onUpdate(EventReceivedPacket ppe) {
        Packet p = ppe.getPacket();
setExtraTag("Watchdog");


            if (p instanceof C13PacketPlayerAbilities && !mc.thePlayer.isUsingItem()) {
                ppe.setCancelled(true);



            //    @EventTarget
        //    public void onUpdate(EventReceivedPacket ppe) {
        //Packet p = ppe.getPacket();
       // mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive());
     //   if(p instanceof  C00PacketKeepAlive) {
        //    if (!Management.instance.modulemgr.getModuleByName("HighJump").isEnabled()) {
        //        ppe.setCancelled(true);
       //     }
      //  }
       // mc.thePlayer.capabilities.isFlying = false;
       // mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction());
      // mc.thePlayer.sendQueue.addToSendQueue(new C0FPacketConfirmTransaction());
      //  if(p instanceof  C0BPacketEntityAction) {

       //         ppe.setCancelled(true);

        }
        }
}
