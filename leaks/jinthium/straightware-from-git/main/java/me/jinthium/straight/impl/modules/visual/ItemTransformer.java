package me.jinthium.straight.impl.modules.visual;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import net.minecraft.network.play.server.S2FPacketSetSlot;

public class ItemTransformer extends Module {


    public static final ModeSetting mode = new ModeSetting("Mode", "Stella",
            "Stella", "Middle", "1.7", "Exhi", "Exhi 2", "Exhi 3", "Exhi 4", "Exhi 5", "Shred", "Smooth", "Sigma", "Spin", "Swong", "Stab");
    public static final NumberSetting slowdown = new NumberSetting("Swing Slowdown", 1, 1, 15, 1);
    public static final BooleanSetting oldDamage = new BooleanSetting("Old Damage", false);
    public static final BooleanSetting smallSwing = new BooleanSetting("Small Swing", false);
    private final BooleanSetting fixDipAnimation = new BooleanSetting("Fix Dipping", true);
    public static final NumberSetting x = new NumberSetting("X", 0, -50, 50, 1);
    public static final NumberSetting y = new NumberSetting("Y", 0, -50, 50, 1);
    public static final NumberSetting size = new NumberSetting("Size", 0, -50, 50, 1);

    public ItemTransformer(){
        super("Item Transformer", Category.VISUALS);
        this.addSettings(mode, slowdown, oldDamage, smallSwing, fixDipAnimation, x, y, size);
    }

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
        if(event.getPacketState() == PacketEvent.PacketState.RECEIVING && event.getPacket() instanceof S2FPacketSetSlot && fixDipAnimation.isEnabled() && (mc.thePlayer.isBlocking() || killAura.block))
            event.cancel();
    };
}
