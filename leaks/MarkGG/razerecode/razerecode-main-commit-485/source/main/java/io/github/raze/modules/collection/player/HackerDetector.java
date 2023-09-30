package io.github.raze.modules.collection.player;

import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.utilities.collection.visual.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;

public class HackerDetector extends BaseModule {

    public HackerDetector() {
        super("HackerDetector", "Notices other peoples cheats.", ModuleCategory.PLAYER);
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            for(Object object1 : mc.theWorld.loadedEntityList) {
                if(object1 instanceof EntityPlayer) {
                    EntityPlayer susUser = (EntityPlayer) object1;
                    if(!susUser.isHacker && susUser != mc.thePlayer) {
                        if (susUser.fallDistance > 3 && susUser.onGround && !susUser.capabilities.isCreativeMode) {
                            ChatUtil.addChatMessage(susUser.getName() + " is using NoFall!", true);
                            susUser.isHacker = true;
                        }
                        if (susUser.capabilities.isFlying && !susUser.capabilities.isCreativeMode) {
                            ChatUtil.addChatMessage(susUser.getName() + " is using Creative Fly!", true);
                            susUser.isHacker = true;
                        }
                        if ((susUser.motionY == 0 && susUser.isAirBorne) && !susUser.capabilities.isCreativeMode) {
                            ChatUtil.addChatMessage(susUser.getName() + " is using AirWalk!", true);
                            susUser.isHacker = true;
                        }
                    }
                }
            }
        }
    }
}
