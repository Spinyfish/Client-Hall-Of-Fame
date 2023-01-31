package intent.AquaDev.aqua.modules.movement;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.combat.Killaura;
import intent.AquaDev.aqua.utils.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class Step extends Module {
   public Step() {
      super("Step", Module.Type.Movement, "Step", 0, Category.Movement);
      Aqua.setmgr.register(new Setting("Boost", this, 0.2, 0.1, 0.85, false));
      Aqua.setmgr.register(new Setting("BlockHeight", this, 1.0, 1.0, 30.0, false));
      Aqua.setmgr.register(new Setting("Mode", this, "Watchdog", new String[]{"Watchdog", "Vanilla"}));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      mc.thePlayer.stepHeight = 0.5F;
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventUpdate) {
         if (Aqua.setmgr.getSetting("StepMode").getCurrentMode().equalsIgnoreCase("Watchdog")) {
            if (mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindBack.pressed || Killaura.target != null) {
               return;
            }

            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedHorizontally) {
               mc.thePlayer.motionY = 0.37F;
            }

            if (mc.thePlayer.isCollidedHorizontally) {
               PlayerUtil.setSpeed(PlayerUtil.getSpeed() + Aqua.setmgr.getSetting("StepBoost").getCurrentNumber());
            }
         }

         if (Aqua.setmgr.getSetting("StepMode").getCurrentMode().equalsIgnoreCase("Vanilla") && mc.thePlayer.isCollidedHorizontally) {
            Fly.sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
            Fly.sendPacketUnlogged(
               new C08PacketPlayerBlockPlacement(
                  new BlockPos(Fly.getX(), Fly.getY() - 1.5, Fly.getZ()),
                  1,
                  new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))),
                  0.0F,
                  0.94F,
                  0.0F
               )
            );
            mc.thePlayer.stepHeight = (float)Aqua.setmgr.getSetting("StepBlockHeight").getCurrentNumber();
         }
      }
   }
}
