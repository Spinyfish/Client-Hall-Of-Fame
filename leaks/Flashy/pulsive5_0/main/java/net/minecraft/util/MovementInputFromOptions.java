package net.minecraft.util;

import club.pulsive.api.main.Pulsive;
import club.pulsive.client.ui.clickgui.clickgui.MainCGUI;
import club.pulsive.impl.module.impl.movement.GuiMove;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;

import static club.pulsive.api.minecraft.MinecraftUtil.mc;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }
    
    public boolean shouldGuiMove() {
        if(!Pulsive.INSTANCE.getModuleManager().getModule(GuiMove.class).isToggled() ||
                (!GuiMove.elements.isSelected(GuiMove.MODES.CLICKGUI) && mc.currentScreen instanceof MainCGUI)
                || (!GuiMove.elements.isSelected(GuiMove.MODES.INVENTORY) && mc.currentScreen instanceof GuiInventory)
                || (!GuiMove.elements.isSelected(GuiMove.MODES.CHEST) && mc.currentScreen instanceof GuiChest) || mc.currentScreen instanceof GuiChat || mc.currentScreen == null) {
            return false;
        }
        return true;
    }
    public void updatePlayerMoveState() {
     if(this.shouldGuiMove()) {

         moveStrafe = 0.0F;
         moveForward = 0.0F;

         if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
           ++moveForward;
         }

         if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
             --moveForward;
         }

         if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
             ++moveStrafe;
         }

         if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
             --moveStrafe;
         }

         if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
             jump = true;
         } else {
             jump = false;
         }
     }  else {
         moveStrafe = 0.0F;
         moveForward = 0.0F;
         if (gameSettings.keyBindForward.isKeyDown()) ++moveForward;
         if (gameSettings.keyBindBack.isKeyDown()) --moveForward;
         if (gameSettings.keyBindLeft.isKeyDown()) ++moveStrafe;
         if (gameSettings.keyBindRight.isKeyDown()) --moveStrafe;
         jump = gameSettings.keyBindJump.isKeyDown();
         sneak = gameSettings.keyBindSneak.isKeyDown();
         if (sneak) {
             moveStrafe = (float) ((double) moveStrafe * 0.3D);
             moveForward = (float) ((double) moveForward * 0.3D);
         }
     }
    
        super.updatePlayerMoveState();
    }
}
