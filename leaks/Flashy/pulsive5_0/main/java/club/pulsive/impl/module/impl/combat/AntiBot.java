package club.pulsive.impl.module.impl.combat;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.event.player.WorldLoadEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.implementations.EnumProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "AntiBot", renderName = "AntiBot", category = Category.COMBAT, keybind = Keyboard.KEY_NONE)

public class AntiBot extends Module {
    private EnumProperty<MODES> mode = new EnumProperty<>("Mode", MODES.Hypixel);
    
    public static ArrayList<EntityPlayer> bots = new ArrayList<>();
    
    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        switch(mode.getValue()) {
            case Hypixel:
                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityPlayer) {
                        if (entity != mc.thePlayer && !((EntityPlayer) entity).isSpectator()) {
                            if (isBot((EntityLivingBase) entity) ) {
                                mc.theWorld.removeEntity(entity);
                            }
                        } else {
                            bots.remove(entity);
                        }
                    }
                }
                break;
            case Advanced:
                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityPlayer) {
                        if (entity != mc.thePlayer && !((EntityPlayer) entity).isSpectator()) {
                            if (isBot((EntityLivingBase) entity) ) {
                                mc.theWorld.removeEntity(entity);
                            }
                        } else {
                            bots.remove(entity);
                        }
                    }
                }
                break;
        }
    };
    
    @EventHandler
    private final Listener<WorldLoadEvent> worldLoadEventListener = event -> {
        bots.clear();
    };
    private boolean isBot(EntityLivingBase entity) {
        return (entity.isInvisible() && !entity.onGround && entity.isPotionActive(14) == false) || entity.motionY == 0 && !entity.onGround;
    }
    
    
    private enum MODES {
        Hypixel,
        Advanced
    }
}
