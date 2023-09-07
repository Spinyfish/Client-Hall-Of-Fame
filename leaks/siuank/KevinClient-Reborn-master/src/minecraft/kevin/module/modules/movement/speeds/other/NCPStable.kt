package kevin.module.modules.movement.speeds.other

import kevin.event.UpdateEvent
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils
import kotlin.math.max

object NCPStable : SpeedMode("NCPStable") { // from FDP
    override fun onUpdate(event: UpdateEvent) {
        if (MovementUtils.isMoving) {
            if (mc.thePlayer.onGround) mc.thePlayer.jump()
            MovementUtils.strafe(max(MovementUtils.speed.toDouble(), MovementUtils.getMoveSpeed(0.27)).toFloat())
        } else {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }
}