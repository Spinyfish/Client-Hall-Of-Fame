package rip.athena.client.modules.impl.mods;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;
import rip.athena.client.utils.input.*;
import net.minecraft.entity.*;
import rip.athena.client.events.*;
import rip.athena.client.events.types.input.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;

public class Freelook extends Module
{
    @ConfigValue.Boolean(name = "Require Hold")
    private boolean returnOnRelease;
    @ConfigValue.Keybind(name = "Perspective Key")
    private int keyBind;
    public static boolean perspectiveToggled;
    public int previousPerspective;
    public static float cameraYaw;
    public static float cameraPitch;
    
    public Freelook() {
        super("Freelook", Category.MODS, "Athena/gui/mods/freelook.png");
        this.returnOnRelease = false;
        this.keyBind = 0;
        this.previousPerspective = 0;
    }
    
    @SubscribeEvent
    public void onKeyPress(final KeyDownEvent event) {
        if (KeybindManager.isInvalidScreen(Freelook.mc.currentScreen)) {
            return;
        }
        if (this.keyBind == 0) {
            return;
        }
        if (event.getKey() == this.keyBind) {
            Freelook.perspectiveToggled = !Freelook.perspectiveToggled;
            if (Freelook.perspectiveToggled) {
                this.previousPerspective = Freelook.mc.gameSettings.thirdPersonView;
                Freelook.mc.gameSettings.thirdPersonView = 1;
                Freelook.cameraPitch = Freelook.mc.thePlayer.rotationPitch;
                Freelook.cameraYaw = Freelook.mc.thePlayer.rotationYaw;
                Freelook.mc.entityRenderer.loadEntityShader(null);
                Freelook.mc.renderGlobal.setDisplayListEntitiesDirty();
            }
            else {
                Freelook.mc.gameSettings.thirdPersonView = this.previousPerspective;
            }
        }
        if (event.getKey() == Freelook.mc.gameSettings.keyBindTogglePerspective.getKeyCode()) {
            Freelook.perspectiveToggled = false;
        }
    }
    
    @SubscribeEvent
    public void onKeyUp(final KeyUpEvent event) {
        if (event.getKey() == this.keyBind && this.returnOnRelease) {
            Freelook.perspectiveToggled = false;
            Freelook.mc.gameSettings.thirdPersonView = this.previousPerspective;
        }
    }
    
    public float getCameraYaw() {
        return Freelook.perspectiveToggled ? Freelook.cameraYaw : Minecraft.getMinecraft().thePlayer.rotationYaw;
    }
    
    public float getCameraPitch() {
        return Freelook.perspectiveToggled ? Freelook.cameraPitch : Minecraft.getMinecraft().thePlayer.rotationPitch;
    }
    
    public boolean overrideMouse() {
        if (Minecraft.getMinecraft().inGameHasFocus && Display.isActive() && this.isToggled()) {
            if (!Freelook.perspectiveToggled) {
                return true;
            }
            Minecraft.getMinecraft().mouseHelper.mouseXYChange();
            final float f1 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6f + 0.2f;
            final float f2 = f1 * f1 * f1 * 8.0f;
            final float f3 = Minecraft.getMinecraft().mouseHelper.deltaX * f2;
            final float f4 = Minecraft.getMinecraft().mouseHelper.deltaY * f2;
            Freelook.cameraYaw += f3 * 0.15f;
            Freelook.cameraPitch -= f4 * 0.15f;
            if (Freelook.cameraPitch > 90.0f) {
                Freelook.cameraPitch = 90.0f;
            }
            if (Freelook.cameraPitch < -90.0f) {
                Freelook.cameraPitch = -90.0f;
            }
            Minecraft.getMinecraft().renderGlobal.setDisplayListEntitiesDirty();
        }
        return false;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    static {
        Freelook.perspectiveToggled = false;
        Freelook.cameraPitch = 0.0f;
    }
}
