package dev.africa.pandaware.impl.module.combat.criticals;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.combat.KillAuraModule;
import dev.africa.pandaware.impl.module.combat.criticals.modes.*;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.module.movement.longjump.LongJumpModule;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.utils.player.MovementUtils;
import lombok.Getter;
import net.minecraft.network.play.client.C02PacketUseEntity;

@Getter
@ModuleInfo(name = "Criticals", category = Category.COMBAT)
public class CriticalsModule extends Module {
    private boolean hasAttacked;

    public CriticalsModule() {
        this.registerModes(
                new PacketCriticals("Packet", this),
                new HypixelCriticals("Hypixel", this),
                new VerusCriticals("Verus", this),
                new FuncraftCriticals("Funcraft", this),
                new VulcanCriticals("Vulcan", this)
        );
    }

    private boolean shouldCritical;

    @Override
    public void onEnable() {
        this.hasAttacked = false;
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            ICriticalsMode iCriticalsMode = (ICriticalsMode) this.getCurrentMode();

            this.shouldCritical = this.shouldHandleCriticals();

            if (this.shouldCritical) {
                iCriticalsMode.handle(event, mc.thePlayer.ticksExisted);
            } else {
                iCriticalsMode.entityIsNull();
            }
        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();

            if (packet != null) {
                this.hasAttacked = packet.getAction().equals(C02PacketUseEntity.Action.ATTACK)
                        && mc.objectMouseOver.entityHit != null;
            }
        } else {
            this.hasAttacked = false;
        }
    };

    boolean shouldHandleCriticals() {
        boolean isInGame = (mc.thePlayer != null && mc.theWorld != null);
        boolean speedToggled = Client.getInstance().getModuleManager()
                .getByClass(SpeedModule.class).getData().isEnabled();

        KillAuraModule killAuraModule = Client.getInstance().getModuleManager().getByClass(KillAuraModule.class);
        boolean killAuraNotNull = killAuraModule.getData().isEnabled() && killAuraModule.getTarget() != null;

        return !Client.getInstance().getModuleManager().getByClass(FlightModule.class).getData().isEnabled() &&
                !Client.getInstance().getModuleManager().getByClass(LongJumpModule.class).getData().isEnabled() &&
                (!speedToggled || !MovementUtils.isMoving()) && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()
                && !mc.thePlayer.isOnLadder() && (this.hasAttacked || killAuraNotNull) && isInGame;
    }

    @Override
    public String getSuffix() {
        return this.getCurrentMode().getName();
    }
}
