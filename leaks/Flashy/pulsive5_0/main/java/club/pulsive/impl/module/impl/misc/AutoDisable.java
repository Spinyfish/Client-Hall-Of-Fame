package club.pulsive.impl.module.impl.misc;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.player.WorldLoadEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.movement.Flight;
import club.pulsive.impl.module.impl.movement.LongJump;
import club.pulsive.impl.module.impl.movement.Speed;
import club.pulsive.impl.property.Property;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInfo(name = "AutoDisable", category = Category.MISC)
//
public class AutoDisable extends Module {
    private Property<Boolean> worldChange = new Property<>("On World Change", true);
    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        switch(event.getEventState()) {
            case RECEIVING:
                if(event.getPacket() instanceof S08PacketPlayerPosLook) {
//                    if(Pulsive.INSTANCE.getModuleManager().getModule(Flight.class).isToggled()) {
//                        Pulsive.INSTANCE.getModuleManager().getModule(Flight.class).toggle();
//                    }
                    if(Pulsive.INSTANCE.getModuleManager().getModule(LongJump.class).isToggled()) {
                        Pulsive.INSTANCE.getModuleManager().getModule(LongJump.class).toggle();
                    }
                }
                break;
        }
    };
    
    @EventHandler
    private final Listener<WorldLoadEvent> worldLoadEventListener = event -> {
        if(worldChange.getValue()) {
            if(Pulsive.INSTANCE.getModuleManager().getModule(Speed.class).isToggled()) {
                Pulsive.INSTANCE.getModuleManager().getModule(Speed.class).toggle();
            }
            if(Pulsive.INSTANCE.getModuleManager().getModule(Flight.class).isToggled()) {
                Pulsive.INSTANCE.getModuleManager().getModule(Flight.class).toggle();
            }
            if(Pulsive.INSTANCE.getModuleManager().getModule(LongJump.class).isToggled()) {
                Pulsive.INSTANCE.getModuleManager().getModule(LongJump.class).toggle();
            }
        }
    };
}
