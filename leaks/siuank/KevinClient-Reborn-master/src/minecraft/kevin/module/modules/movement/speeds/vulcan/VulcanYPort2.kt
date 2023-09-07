package kevin.module.modules.movement.speeds.vulcan

import kevin.event.UpdateEvent
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object VulcanYPort2 : SpeedMode("VulcanYPort2") {
    override fun onUpdate(event: UpdateEvent) {
        if (!MovementUtils.isMoving) return
        val thePlayer = mc.thePlayer ?: return
        if (thePlayer.onGround) {
            thePlayer.jump()
            if (MovementUtils.speed > 0.62) MovementUtils.strafe(0.61f)
        } else if (thePlayer.motionY > 0.13 && thePlayer.hurtTime < 6) {
            thePlayer.motionY = 0.1263
            MovementUtils.strafe(MovementUtils.speed * 0.99257314f)
        }
    }
}