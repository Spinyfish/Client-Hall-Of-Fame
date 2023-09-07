package kevin.module.modules.movement.speeds.other

import kevin.event.UpdateEvent
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils
import kevin.utils.RandomUtils
import net.minecraft.client.settings.GameSettings

object Hypixel : SpeedMode("Hypixel") {
    private val mode = ListValue("${valuePrefix}Mode", arrayOf("LowHop", "FastDown"), "FastDown")
    private val airStrafe = BooleanValue("${valuePrefix}AirStrafe", true)
    private val modeValue: Boolean
        get() = mode equal "LowHop"
    var airTicks = 0
    override fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return
        if (!MovementUtils.isMoving) {
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
            airTicks = 5
            return
        }
        if (thePlayer.onGround) {
            mc.gameSettings.keyBindJump.pressed = true
            MovementUtils.strafe()
            airTicks = 0
            if (modeValue) {
                thePlayer.motionY -= RandomUtils.random.nextDouble() * 0.01
            }
        } else if (!modeValue) {
            if (++airTicks == 4) {
                thePlayer.motionY = -0.10200565429997936
                if (airStrafe.get()) {
                    MovementUtils.strafe()
                }
            }
        }
    }

    override fun onDisable() {
        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
    }
}