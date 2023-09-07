package kevin.module.modules.movement.speeds.other

import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object Spartan : SpeedMode("Spartan") { // From Rise
    override fun onPreMotion() {
        if (MovementUtils.isMoving) {
            MovementUtils.strafe()
            mc.thePlayer.speedInAir = (0.04 + Math.random() / 5000).toFloat()

            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump()

                val speed = (0.5 + Math.random() / 500).toFloat()

                if (MovementUtils.speed < speed)
                    MovementUtils.strafe(speed)
            }

            mc.thePlayer.isSprinting = false
            mc.gameSettings.keyBindSprint.pressed = false
        }
    }
}