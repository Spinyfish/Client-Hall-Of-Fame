package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.impl.events.GameLoopEvent;
import de.lirium.impl.events.TimeEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import god.buddy.aot.BCompiler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Mouse;

@ModuleFeature.Info(name = "Time Shift", description = "Travel through the time", category = ModuleFeature.Category.MOVEMENT)
public class TimeShiftFeature extends ModuleFeature {

    @Value(name = "Cap Ticks")
    final CheckBox capTicks = new CheckBox(false);

    @Value(name = "Instant")
    final CheckBox instant = new CheckBox(false);

    double balance, lastTime;

    @EventHandler
    public final Listener<TimeEvent> timeEventListener = this::doTimeShift;

    @EventHandler
    public final Listener<GameLoopEvent> gameLoopEventListener = e -> {
        e.capTicks = capTicks.getValue();
    };

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doTimeShift(TimeEvent e) {
        if (isKeyDown(mc.gameSettings.keyBindPickBlock.getKeyCode())) {
            balance += Minecraft.getSystemTime() - lastTime;
        }

        if ((instant.getValue() ? !isKeyDown(mc.gameSettings.keyBindPickBlock.getKeyCode()) : isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) && balance != 0) {
            balance = 0;
            mc.gameSettings.keyBindSneak.pressed = false;
        }
        lastTime = Minecraft.getSystemTime();
        e.time = (long) (Minecraft.getSystemTime() - balance);
    }

    @Override
    public void onEnable() {
        balance = 0;
        lastTime = 0;
        super.onEnable();
    }
}
