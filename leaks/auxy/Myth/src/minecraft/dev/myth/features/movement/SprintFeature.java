/**
 * @project Myth
 * @author Skush/Duzey
 * @at 05.08.2022
 */
package dev.myth.features.movement;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.events.UpdateEvent;
import dev.myth.api.feature.Feature;
import dev.myth.features.player.ScaffoldFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

@Feature.Info(
        name = "Sprint",
        description = "Allows you to automatically sprint",
        category = Feature.Category.MOVEMENT,
        keyBind = Keyboard.KEY_Y
)
public class SprintFeature extends Feature {

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() != EventState.PRE) return;
        ScaffoldFeature scaffoldFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ScaffoldFeature.class);
        if(scaffoldFeature.isEnabled() && scaffoldFeature.sprintMode.is(ScaffoldFeature.SprintSettings.NONE)) return;
//        if(getPlayer().currentEvent != null) {
//            if(Math.abs(MathHelper.wrapAngleTo180_float(getPlayer().currentEvent.getYaw() - getPlayer().rotationYaw)) > 90) {
//                MC.gameSettings.keyBindSprint.pressed = false;
//                return;
//            }
//        }
        MC.gameSettings.keyBindSprint.pressed = true;

    };

    @Override
    public void onDisable() {
        super.onDisable();

        MC.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(MC.gameSettings.keyBindSprint.getKeyCode());
    }
}
