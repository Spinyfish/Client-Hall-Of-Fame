package club.pulsive.impl.module.impl.movement;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.player.PlayerMoveEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.combat.Aura;
import club.pulsive.impl.module.impl.player.Scaffold;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.ColorProperty;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.util.player.MovementUtil;
import club.pulsive.impl.util.player.PlayerUtil;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "Sprint", renderName = "Sprint", category = Category.MOVEMENT, keybind = Keyboard.KEY_NONE)
public class Sprint extends Module {
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = event -> {
        mc.thePlayer.setSprinting(canSprint());
    };

    @Override
    public void onDisable() {
        if(mc.thePlayer != null) mc.thePlayer.setSprinting(mc.gameSettings.keyBindSprint.isPressed());
        super.onDisable();
    }

    public boolean canSprint() {
        Scaffold scaffold = Pulsive.INSTANCE.getModuleManager().getModule(Scaffold.class);
        return (MovementUtil.isMoving() && PlayerUtil.isOnServer("hypixel") || mc.thePlayer.moveForward >= 0.8f) &&
                !mc.thePlayer.isCollidedHorizontally &&
                (mc.thePlayer.getFoodStats().getFoodLevel() > 6 ||
                        mc.thePlayer.capabilities.allowFlying) && (!scaffold.isToggled() || (scaffold.isToggled() && (!scaffold.noSprintProperty.getValue() || scaffold.autoJumpProperty.getValue()))) &&
                !mc.thePlayer.isSneaking() &&
                (!mc.thePlayer.isUsingItem() || (Boolean) Module.propertyRepository().propertyBy(Aura.class, "Keep Sprint").getValue()) &&
                !mc.thePlayer.isPotionActive(Potion.moveSlowdown.id);
    }
    
    private enum MODES {
        HYPIXEL,
        VANILLA
    }
}
