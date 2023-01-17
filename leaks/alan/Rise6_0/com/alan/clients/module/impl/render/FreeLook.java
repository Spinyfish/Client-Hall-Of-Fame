package com.alan.clients.module.impl.render;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.value.impl.BooleanValue;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "Free look", description = "module.render.freelook.description", category = Category.RENDER)
public final class FreeLook extends Module {

    public BooleanValue invertPitch = new BooleanValue("Invert Pitch", this, false);

    private int previousPerspective;
    public float originalYaw, originalPitch, lastYaw, lastPitch;

    @Override
    protected void onEnable() {
        previousPerspective = mc.gameSettings.thirdPersonView;
        originalYaw = lastYaw = mc.thePlayer.rotationYaw;
        originalPitch = lastPitch = mc.thePlayer.rotationPitch;

        if (invertPitch.getValue()) lastPitch *= -1;
    }

    @Override
    protected void onDisable() {
        mc.thePlayer.rotationYaw = originalYaw;
        mc.thePlayer.rotationPitch = originalPitch;
        mc.gameSettings.thirdPersonView = previousPerspective;
    }

    @EventLink(value = Priorities.LOW)
    public final Listener<Render2DEvent> onRender2D = event -> {

        if (this.getKeyCode() == Keyboard.KEY_NONE || !Keyboard.isKeyDown(this.getKeyCode())) {
            this.setEnabled(false);
            return;
        }

        this.mc.mouseHelper.mouseXYChange();
        final float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float f1 = (float) (f * f * f * 1.5);
        lastYaw += this.mc.mouseHelper.deltaX * f1;
        lastPitch -= this.mc.mouseHelper.deltaY * f1;

        lastPitch = MathHelper.clamp_float(lastPitch, -90, 90);
        mc.gameSettings.thirdPersonView = 1;
    };

    @EventLink(value = Priorities.LOW)
    public final Listener<TeleportEvent> onTeleport = event -> {
        originalYaw = event.getYaw();
        originalPitch = event.getPitch();
    };
}