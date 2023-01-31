package intent.AquaDev.aqua.modules.world;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPreMotion;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import java.util.Random;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Eagle extends Module {
   public Eagle() {
      super("Eagle", Module.Type.World, "Eagle", 0, Category.World);
      Aqua.setmgr.register(new Setting("PlaceTicks", this, 2.0, 1.0, 20.0, false));
      Aqua.setmgr.register(new Setting("PlaceTicks2", this, 2.0, 1.0, 20.0, false));
      Aqua.setmgr.register(new Setting("OnlyOnAir", this, true));
      Aqua.setmgr.register(new Setting("RandomPlaceTicks", this, true));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventPreMotion) {
      }

      if (event instanceof EventUpdate) {
         mc.thePlayer.setSprinting(false);
         mc.gameSettings.keyBindSprint.pressed = false;
         float ticks1 = (float)Aqua.setmgr.getSetting("EaglePlaceTicks").getCurrentNumber();
         float ticks2 = (float)Aqua.setmgr.getSetting("EaglePlaceTicks2").getCurrentNumber();
         float TICKS = (float)MathHelper.getRandomDoubleInRange(new Random(), (double)ticks1, (double)ticks2);
         float roundTicks = (float)Math.round(TICKS);
         float ticks = Aqua.setmgr.getSetting("EagleRandomPlaceTicks").isState()
            ? roundTicks
            : (float)Aqua.setmgr.getSetting("EaglePlaceTicks").getCurrentNumber();
         BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
         if (Aqua.setmgr.getSetting("EagleOnlyOnAir").isState()
            ? mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air && mc.thePlayer.ticksExisted % Math.round(ticks) == 0
            : mc.thePlayer.ticksExisted % Math.round(ticks) == 0) {
            mc.rightClickMouse();
         }

         mc.gameSettings.keyBindSneak.pressed = mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air;
      }
   }
}
