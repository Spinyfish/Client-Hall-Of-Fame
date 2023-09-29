package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.events.SlowDownEvent;
import de.lirium.impl.module.ModuleFeature;
import god.buddy.aot.BCompiler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

@ModuleFeature.Info(name = "No Slow", description = "Modify your slowdown", category = ModuleFeature.Category.MOVEMENT)
public class NoSlowFeature extends ModuleFeature {

    @Value(name = "Speed")
    private final SliderSetting<Float> speed = new SliderSetting<>(1.0f, 0.0f, 1.0f);

    @Value(name = "Mode")
    private final ComboBox<String> mode = new ComboBox<>("Vanilla", new String[]{"Gomme"});

    @EventHandler
    public final Listener<SlowDownEvent> updateEvent = e -> {
        setSuffix(mode.getValue(), String.valueOf(speed.getValue()));
        e.setForward(speed.getValue());
        e.setStrafe(speed.getValue());
        if (mode.getValue().equalsIgnoreCase("Gomme"))
            doGomme();
    };

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doGomme() {
        if (getPlayer().ticksExisted % 2 == 0) {
            if (getPlayer().isHandActive()) {
                sendPacketUnlogged(new CPacketKeepAlive(-131));
                sendPacketUnlogged(new CPacketConfirmTransaction());
                sendPacketUnlogged(new CPacketKeepAlive(-131));
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}