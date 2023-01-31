package net.minecraft.util;

import intent.AquaDev.aqua.Aqua;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
   private final GameSettings gameSettings;

   public MovementInputFromOptions(GameSettings gameSettingsIn) {
      this.gameSettings = gameSettingsIn;
   }

   @Override
   public void updatePlayerMoveState() {
      this.moveStrafe = 0.0F;
      this.moveForward = 0.0F;
      if (this.gameSettings.keyBindForward.isKeyDown()) {
         ++this.moveForward;
      }

      if (this.gameSettings.keyBindBack.isKeyDown()) {
         --this.moveForward;
      }

      if (this.gameSettings.keyBindLeft.isKeyDown()) {
         ++this.moveStrafe;
      }

      if (this.gameSettings.keyBindRight.isKeyDown()) {
         --this.moveStrafe;
      }

      this.jump = this.gameSettings.keyBindJump.isKeyDown();
      this.sneak = this.gameSettings.keyBindSneak.isKeyDown();
      if (this.sneak) {
         boolean scaffold = Aqua.setmgr.getSetting("ScaffoldLegitPlace").isState()
            && Aqua.moduleManager.getModuleByName("Scaffold").isToggled()
            && !Minecraft.getMinecraft().gameSettings.keyBindJump.pressed;
         if (scaffold) {
            if (Aqua.moduleManager.getModuleByName("Disabler").isToggled()) {
               this.moveStrafe = (float)((double)this.moveStrafe * 0.9);
               this.moveForward = (float)((double)this.moveForward * 0.9);
            } else {
               this.moveStrafe = (float)((double)this.moveStrafe * (double)((float)Aqua.setmgr.getSetting("ScaffoldSneakModify").getCurrentNumber()));
               this.moveForward = (float)((double)this.moveForward * (double)((float)Aqua.setmgr.getSetting("ScaffoldSneakModify").getCurrentNumber()));
            }
         } else {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3);
            this.moveForward = (float)((double)this.moveForward * 0.3);
         }
      }
   }
}
