package com.alan.clients.module.impl.ghost;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.ghost.autoclicker.DragClickAutoClicker;
import com.alan.clients.module.impl.ghost.autoclicker.NormalAutoClicker;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.ClickEvent;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.newevent.impl.other.TickEvent;
import com.alan.clients.newevent.impl.render.Render3DEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.BoundsNumberValue;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.impl.ModeValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import util.time.StopWatch;
import net.minecraft.util.MovingObjectPosition;

/**
 * @author Alan
 * @since 29/01/2021
 */

@Rise
@ModuleInfo(name = "Auto Clicker", description = "module.ghost.autoclicker.description", category = Category.GHOST)
public class AutoClicker extends Module {

    private ModeValue mode = new ModeValue("Mode", this)
            .add(new NormalAutoClicker("Normal", this))
            .add(new DragClickAutoClicker("Drag Click Simulations", this))
            .setDefault("Normal");

    private BooleanValue jitter = new BooleanValue("Jitter", this, false);

    private StopWatch stopWatch = new StopWatch();
    private double directionX, directionY;

    @EventLink
    public final Listener<ClickEvent> onClick = event -> {
        stopWatch.reset();

        directionX = (Math.random() - 0.5) * 4;
        directionY = (Math.random() - 0.5) * 4;
    };

    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        if (!stopWatch.finished(100) && this.jitter.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            EntityRenderer.mouseAddedX = (float) (((Math.random() - 0.5) * 400 / Minecraft.getDebugFPS()) * directionX);
            EntityRenderer.mouseAddedY = (float) (((Math.random() - 0.5) * 400 / Minecraft.getDebugFPS()) * directionY);
        }
    };

}