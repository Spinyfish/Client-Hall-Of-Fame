package kevin.module.modules.movement.flys.other

import kevin.event.MoveEvent
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.module.modules.movement.flys.FlyMode
import kotlin.math.*

@Suppress("SameParameterValue")
object Sparky : FlyMode("Sparky") {
    private val timerSpeed = FloatValue("${valuePrefix}TimerSpeed", 0.1f, 0.1f, 2f)
    private val horizontalSpeed = FloatValue("${valuePrefix}HorizontalSpeed", 9.0f, 1f, 10f)
    private val verticalSpeed = FloatValue("${valuePrefix}VerticalSpeed", 5.0f, 1f, 10f)
    private val waitTime = IntegerValue("${valuePrefix}WaitTime", 1, 1, 10)
    private var state = 0
    private var horizontal = false
    override fun onEnable() {
        horizontal = false
        state = -2
    }

    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0
        mc.timer.timerSpeed = timerSpeed.get()
        if (++state > waitTime.get()) {
            if (mc.gameSettings.keyBindJump.isKeyDown) {
                vClip(verticalSpeed.get())
            } else if (mc.gameSettings.keyBindSneak.isKeyDown) {
                vClip(verticalSpeed.get())
            } else {
                horizontal = true
            }
            mc.timer.timerSpeed = 1f
            state = 0
        }
    }

    override fun onDisable() {
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionZ = 0.0
        mc.timer.timerSpeed = 1f
    }

    override fun onMove(event: MoveEvent) {
        event.zero()
        if (horizontal) {
            horizontal = false
            val baseSpeed = horizontalSpeed.get()
            val playerYaw = mc.thePlayer.rotationYaw * PI / 180.0
            event.x = baseSpeed * -sin(playerYaw)
            event.z = baseSpeed * cos(playerYaw)
        }
    }

    private fun vClip(d: Float) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ)
    }
}