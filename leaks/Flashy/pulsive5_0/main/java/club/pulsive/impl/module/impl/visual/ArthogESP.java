package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(name = "WarthogESP", category = Category.VISUALS, description = "WarthogESP")
public class ArthogESP extends Module {
    
    
    private float prevFov, startPosY;
    
    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
      if(!mc.thePlayer.isSneaking()) {
          mc.thePlayer.rotationPitch = 90;
          mc.gameSettings.fovSetting = 15;
          mc.gameSettings.thirdPersonView = 1;
          EntityPlayer.enableCameraYOffset = true;
          EntityPlayer.cameraYPosition = mc.thePlayer.posY + 110;
      }else{
          mc.gameSettings.fovSetting = prevFov;
          EntityPlayer.enableCameraYOffset = false;
          EntityPlayer.cameraYPosition = mc.thePlayer.posY;
      }
    };

    @Override
    public void onEnable() {
        super.onEnable();
        startPosY = (float) (mc.thePlayer.posY + 110);
        prevFov = mc.gameSettings.fovSetting;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.fovSetting = prevFov;
        EntityPlayer.enableCameraYOffset = false;
        EntityPlayer.cameraYPosition = mc.thePlayer.posY;
    }
}
