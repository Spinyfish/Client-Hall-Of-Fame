/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kevin.module.modules.movement.speeds.other

import kevin.event.UpdateEvent
import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils
import net.minecraft.client.settings.GameSettings

object Custom : SpeedMode("Custom") { // from FDP
    private val speedValue = FloatValue("CustomSpeed", 1.6f, 0f, 2f)
    private val doLaunchSpeedValue = BooleanValue("CustomDoLaunchSpeed", true)
    private val launchSpeedValue = FloatValue("CustomLaunchSpeed", 1.6f, 0.2f, 2f)
    private val doMinimumSpeedValue = BooleanValue("CustomDoMinimumSpeed", true)
    private val minimumSpeedValue = FloatValue("CustomMinimumSpeed", 0.25f, 0.1f, 2f)
    private val addYMotionValue = FloatValue("CustomAddYMotion", 0f, 0f, 2f)
    private val doCustomYValue = BooleanValue("CustomDoModifyJumpY", true)
    private val yValue = FloatValue("CustomY", 0.42f, 0f, 4f)
    private val upTimerValue = FloatValue("CustomUpTimer", 1f, 0.1f, 2f)
    private val jumpTimerValue = FloatValue("CustomJumpTimer", 1.25f, 0.1f, 2f)
    private val downTimerValue = FloatValue("CustomDownTimer", 1f, 0.1f, 2f)
    private val strafeValue = ListValue("CustomStrafe", arrayOf("Strafe", "Boost", "Plus", "PlusOnlyUp", "PlusOnlyDown", "Non-Strafe"), "Boost")
    private val plusMode = ListValue("PlusBoostMode", arrayOf("Add", "Multiply"), "Add")
    private val plusMultiply = FloatValue("PlusMultiplyAmount", 1.1f, 1f, 2f)
    private val groundStay = IntegerValue("CustomGroundStay", 0, 0, 10)
    private val groundResetXZValue = BooleanValue("CustomGroundResetXZ", false)
    private val resetXZValue = BooleanValue("CustomResetXZ", false)
    private val resetYValue = BooleanValue("CustomResetY", false)
    private val GroundSpaceKeyPressed = BooleanValue("CustomPressSpaceKeyOnGround", true)
    private val AirSpaceKepPressed = BooleanValue("CustomPressSpaceKeyInAir", false)
    private val usePreMotion = BooleanValue("CustomUsePreMotion", true)



    private var groundTick = 0


    override fun onPreMotion() {
        if (!usePreMotion.get()) return
        if (MovementUtils.isMoving) {
            mc.timer.timerSpeed = if (mc.thePlayer.motionY> 0) { upTimerValue.get() } else { downTimerValue.get() }

            when {
                mc.thePlayer.onGround -> {
                    if (groundTick >= groundStay.get()) {
                        if (GroundSpaceKeyPressed.get()) {
                            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                        }
                        mc.timer.timerSpeed = jumpTimerValue.get()
                        mc.thePlayer.jump()
                        if (doLaunchSpeedValue.get()) {
                            MovementUtils.strafe(launchSpeedValue.get())
                        }
                        if (doCustomYValue.get()) {
                            if (yValue.get() != 0f) {
                                mc.thePlayer.motionY = yValue.get().toDouble()
                            }
                        }
                    } else if (groundResetXZValue.get()) {
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                    }
                    groundTick++
                }
                else -> {
                    groundTick = 0
                    if (AirSpaceKepPressed.get()) {
                        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                    }
                    if (doMinimumSpeedValue.get() && MovementUtils.speed < minimumSpeedValue.get()) {
                        MovementUtils.strafe(minimumSpeedValue.get())
                    }
                    when (strafeValue.get().lowercase()) {
                        "strafe" -> MovementUtils.strafe(speedValue.get())
                        "non-strafe" -> MovementUtils.strafe()
                        "boost" -> MovementUtils.strafe()
                        "plus" -> {
                            when (plusMode.get().lowercase()) {
                                "plus" -> MovementUtils.move(speedValue.get() * 0.1f)
                                "multiply" -> {
                                    mc.thePlayer.motionX *= plusMultiply.get()
                                    mc.thePlayer.motionZ *= plusMultiply.get()
                                }
                            }
                        }
                        "plusonlyup" -> {
                            if (mc.thePlayer.motionY > 0) {
                                when (plusMode.get().lowercase()) {
                                    "plus" -> MovementUtils.move(speedValue.get() * 0.1f)
                                    "multiply" -> {
                                        mc.thePlayer.motionX *= plusMultiply.get()
                                        mc.thePlayer.motionZ *= plusMultiply.get()
                                    }
                                }
                            } else {
                                MovementUtils.strafe()
                            }
                        }
                        "plusonlydown" -> {
                            if (mc.thePlayer.motionY < 0) {
                                when (plusMode.get().lowercase()) {
                                    "plus" -> MovementUtils.move(speedValue.get() * 0.1f)
                                    "multiply" -> {
                                        mc.thePlayer.motionX *= plusMultiply.get()
                                        mc.thePlayer.motionZ *= plusMultiply.get()
                                    }
                                }
                            } else {
                                MovementUtils.strafe()
                            }
                        }
                    }
                    mc.thePlayer.motionY += addYMotionValue.get() * 0.03
                }
            }
        } else if (resetXZValue.get()) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    override fun onUpdate(event: UpdateEvent) {
        if (usePreMotion.get()) return
        if (MovementUtils.isMoving) {
            mc.timer.timerSpeed = if (mc.thePlayer.motionY> 0) { upTimerValue.get() } else { downTimerValue.get() }

            when {
                mc.thePlayer.onGround -> {
                    if (groundTick >= groundStay.get()) {
                        if (GroundSpaceKeyPressed.get()) {
                            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                        }
                        mc.timer.timerSpeed = jumpTimerValue.get()
                        mc.thePlayer.jump()
                        if (doLaunchSpeedValue.get()) {
                            MovementUtils.strafe(launchSpeedValue.get())
                        }
                        if (doCustomYValue.get()) {
                            if (yValue.get() != 0f) {
                                mc.thePlayer.motionY = yValue.get().toDouble()
                            }
                        }
                    } else if (groundResetXZValue.get()) {
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                    }
                    groundTick++
                }
                else -> {
                    groundTick = 0
                    if (AirSpaceKepPressed.get()) {
                        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                    }
                    if (doMinimumSpeedValue.get() && MovementUtils.speed < minimumSpeedValue.get()) {
                        MovementUtils.strafe(minimumSpeedValue.get())
                    }
                    when (strafeValue.get().lowercase()) {
                        "strafe" -> MovementUtils.strafe(speedValue.get())
                        "non-strafe" -> MovementUtils.strafe()
                        "boost" -> MovementUtils.strafe()
                        "plus" -> {
                            when (plusMode.get().lowercase()) {
                                "plus" -> MovementUtils.move(speedValue.get() * 0.1f)
                                "multiply" -> {
                                    mc.thePlayer.motionX *= plusMultiply.get()
                                    mc.thePlayer.motionZ *= plusMultiply.get()
                                }
                            }
                        }
                        "plusonlyup" -> {
                            if (mc.thePlayer.motionY > 0) {
                                when (plusMode.get().lowercase()) {
                                    "plus" -> MovementUtils.move(speedValue.get() * 0.1f)
                                    "multiply" -> {
                                        mc.thePlayer.motionX *= plusMultiply.get()
                                        mc.thePlayer.motionZ *= plusMultiply.get()
                                    }
                                }
                            } else {
                                MovementUtils.strafe()
                            }
                        }
                        "plusonlydown" -> {
                            if (mc.thePlayer.motionY < 0) {
                                when (plusMode.get().lowercase()) {
                                    "plus" -> MovementUtils.move(speedValue.get() * 0.1f)
                                    "multiply" -> {
                                        mc.thePlayer.motionX *= plusMultiply.get()
                                        mc.thePlayer.motionZ *= plusMultiply.get()
                                    }
                                }
                            } else {
                                MovementUtils.strafe()
                            }
                        }
                    }
                    mc.thePlayer.motionY += addYMotionValue.get() * 0.03
                }
            }
        } else if (resetXZValue.get()) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }



    override fun onEnable() {
        if (resetXZValue.get()) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
        if (resetYValue.get()) mc.thePlayer.motionY = 0.0
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }
}