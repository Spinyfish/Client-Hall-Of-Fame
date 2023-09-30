package io.github.raze.modules.collection.player;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.NumberSetting;
import net.minecraft.util.BlockPosition;

public class AntiVoid extends BaseModule {

    public ArraySetting mode;
    public NumberSetting height;
    private double oldX, oldY, oldZ;
    private boolean checkEdge;
    // Again, dont remove!
    private boolean wasDamaged;

    public AntiVoid() {
        super("AntiVoid", "Anti void", ModuleCategory.PLAYER);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(

                mode = new ArraySetting(this, "Mode", "Set-Position", "Set-Position", "Motion", "Vulcan"),

                height = new NumberSetting(this, "Falling Height", 1, 15, 5)

        );
    }

    @Override
    public void onEnable() {
        checkEdge = true;
    }

    @Override
    public void onDisable() {
        checkEdge = false;
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            if (checkEdge && checkNearEdge()) {
                oldX = mc.thePlayer.posX;
                oldY = mc.thePlayer.posY;
                oldZ = mc.thePlayer.posZ;
                checkEdge = false;
            }

            switch(mode.get()) {
                case "Set-Position":
                    if (mc.thePlayer.fallDistance > height.get().floatValue())
                        mc.thePlayer.setPositionAndUpdate(oldX, oldY, oldZ);
                    break;
                case "Motion":
                    if(mc.thePlayer.fallDistance > height.get().floatValue())
                        mc.thePlayer.motionY = 0.2;
                    break;
                case "Vulcan":
                    if(mc.thePlayer.fallDistance > height.get().floatValue())
                        if(!wasDamaged) {
                            mc.thePlayer.motionY = 0;
                            eventMotion.setOnGround(true);
                        }
                    if(mc.thePlayer.hurtTime != 0) {
                        wasDamaged = true;
                    }
                    if(wasDamaged) {
                        mc.thePlayer.setPositionAndUpdate(oldX, oldY, oldZ);
                        wasDamaged = false;
                    }
                    break;

            }
        }
    }

    private boolean checkNearEdge() {
        int range = 1;
        double threshold = height.get().floatValue();
        double xPos = mc.thePlayer.posX, yPos = mc.thePlayer.posY, zPos = mc.thePlayer.posZ;

        for (int x = -range; x <= range; x++) {

            for (int z = -range; z <= range; z++) {

                double edgeY = mc.theWorld.getPrecipitationHeight(new BlockPosition(xPos + x, 0, zPos + z)).getY();

                if (yPos - edgeY >= threshold)
                    return true;

            }

        }

        return false;

    }

}
