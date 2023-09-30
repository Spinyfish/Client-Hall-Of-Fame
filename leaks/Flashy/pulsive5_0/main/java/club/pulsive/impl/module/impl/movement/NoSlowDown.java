package club.pulsive.impl.module.impl.movement;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.player.BlockStepEvent;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.event.player.PlayerSlowEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.combat.Aura;
import club.pulsive.impl.module.impl.player.Scaffold;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.util.client.BlockPosUtil;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.MovementUtil;
import club.pulsive.impl.util.player.PlayerUtil;
import lombok.AllArgsConstructor;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "No Slow Down",  description = "No Slow Down modul", category = Category.MOVEMENT)
public class NoSlowDown extends Module {
    private EnumProperty<MODES> mode = new EnumProperty<MODES>("Mode", MODES.WATCHDOG);
    private boolean blocking;
    BlockPosUtil blockPosUtil = new BlockPosUtil();
    @Override
    public void init() {
        super.init();
    }
    @EventHandler
    private final Listener<PlayerSlowEvent> playerSlowEventListener = event -> {
        event.setCancelled(true);
    };
    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        switch(event.getEventState()) {
            case RECEIVING:
                if(mc.thePlayer == null || mc.theWorld == null || Pulsive.INSTANCE.getModuleManager().getModule(Aura.class).isEntityNearby()) return;
                break;
        }
    };
    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        if(Pulsive.INSTANCE.getModuleManager().getModule(Aura.class).isEntityNearby()) return;
        if (mc.thePlayer.isUsingItem() && mc.thePlayer.isMoving()) {
            switch (mode.getValue()) {
                case WATCHDOG:
                    if(event.isPre() && mc.thePlayer.ticksExisted % 2 == 0 && !Pulsive.INSTANCE.getModuleManager().getModule(Speed.class).isToggled() && MovementUtil.isMathGround()){
                        event.setPosY(event.getPosY() + 0.05);
                        event.setGround(false);
                    }
                    break;
                case NCP:
                    if(!blocking)
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f));
                    break;
            }
            blocking = true;
        }
    };

    @EventHandler
    private final Listener<BlockStepEvent> blockStepEventListener = event -> {
        if(mc.thePlayer.isUsingItem()){
            event.setCancelled(true);
        }
    };

    @AllArgsConstructor
    public enum MODES {
        WATCHDOG("Watchdog"),
        NCP("NCP");

        private final String modeName;

        @Override
        public String toString() {return modeName;}
    }
}
