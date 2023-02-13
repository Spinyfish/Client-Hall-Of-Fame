package juul.util.player;

import juul.Juul;
import juul.inject.wrappers.EntityLivingBaseWrapper;
import juul.util.Utils;

public class PlayerUtil implements Utils {

	public static boolean isMoving() {
        EntityLivingBaseWrapper entityLivingBaseWrapper = thePlayerSP.getEntityLivingBaseWrapper();
        return entityLivingBaseWrapper.moveForward() != 0.0f || entityLivingBaseWrapper.moveStrafing() != 0.0f;
    }
	
}
