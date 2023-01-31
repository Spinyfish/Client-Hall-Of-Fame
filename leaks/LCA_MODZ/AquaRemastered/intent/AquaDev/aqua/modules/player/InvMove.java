package intent.AquaDev.aqua.modules.player;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPacket;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.misc.Disabler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class InvMove extends Module {
   public InvMove() {
      super("InvMove", Module.Type.Player, "InvMove", 0, Category.Movement);
      Aqua.setmgr.register(new Setting("PacketSpoof", this, false));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventPacket) {
         Packet packet = EventPacket.getPacket();
         if (Aqua.setmgr.getSetting("InvMovePacketSpoof").isState()) {
            C0BPacketEntityAction c0B = (C0BPacketEntityAction)packet;
            if (c0B.getAction().equals(C0BPacketEntityAction.Action.OPEN_INVENTORY)) {
               Disabler.sendPacketUnlogged(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
               Disabler.sendPacketUnlogged(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
         }
      }

      if (event instanceof EventUpdate) {
         if (Aqua.setmgr.getSetting("InvMovePacketSpoof").isState() && mc.currentScreen instanceof GuiInventory) {
            return;
         }

         if (mc.currentScreen instanceof GuiChat || Aqua.moduleManager.getModuleByName("Longjump").isToggled()) {
            return;
         }

         mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
         mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
         mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
         mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
         mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint);
         mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
      }
   }
}
