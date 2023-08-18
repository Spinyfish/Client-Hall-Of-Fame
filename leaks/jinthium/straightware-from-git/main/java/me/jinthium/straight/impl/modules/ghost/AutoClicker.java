package me.jinthium.straight.impl.modules.ghost;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.game.ClickEvent;
import me.jinthium.straight.impl.event.game.PlayerAttackEvent;
import me.jinthium.straight.impl.event.game.TickEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.event.render.Render3DEvent;
import me.jinthium.straight.impl.modules.ghost.autoclicker.DragClicker;
import me.jinthium.straight.impl.modules.ghost.autoclicker.NormalClicker;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.MovingObjectPosition;

public class AutoClicker extends Module {
    private BooleanSetting jitter = new BooleanSetting("Jitter", false);

    private TimerUtil stopWatch = new TimerUtil();
    private double directionX, directionY;

    public AutoClicker(){
        super("Auto Clicker", Category.GHOST);

        this.registerModes(
                new NormalClicker(),
                new DragClicker()
        );

        this.addSettings(jitter);
    }

    @Callback
    final EventCallback<TickEvent> tickEventEventCallback = event -> {
        this.setSuffix(this.getCurrentMode().getInformationSuffix());
    };

    @Callback
    final EventCallback<ClickEvent> clickEventEventCallback = event -> {
        stopWatch.reset();

        directionX = (Math.random() - 0.5) * 4;
        directionY = (Math.random() - 0.5) * 4;
    };

    @Callback
    final EventCallback<Render3DEvent> render3DEventEventCallback = event -> {
        if (!stopWatch.hasTimeElapsed(100) && this.jitter.isEnabled() && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            EntityRenderer.mouseAddedX = (float) (((Math.random() - 0.5) * 400 / Minecraft.getDebugFPS()) * directionX);
            EntityRenderer.mouseAddedY = (float) (((Math.random() - 0.5) * 400 / Minecraft.getDebugFPS()) * directionY);
        }
    };
}
