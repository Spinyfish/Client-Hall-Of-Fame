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
package kevin.module.modules.player

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.MSTimer
import kevin.utils.RandomUtils
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import java.util.*

class AntiAFK : Module("AntiAFK", "Prevents you from getting kicked for being AFK.", category = ModuleCategory.PLAYER) {

    private val swingDelayTimer = MSTimer()
    private val delayTimer = MSTimer()

    private val modeValue = ListValue("Mode", arrayOf("Old", "Random", "Custom"), "Random")

    private val swingDelayValue = IntegerValue("SwingDelay", 100, 0, 1000)
    private val rotationDelayValue = IntegerValue("RotationDelay", 100, 0, 1000)
    private val rotationAngleValue = FloatValue("RotationAngle", 1f, -180F, 180F)

    private val jumpValue = BooleanValue("Jump", true)
    private val moveValue = BooleanValue("Move", true)
    private val rotateValue = BooleanValue("Rotate", true)
    private val swingValue = BooleanValue("Swing", true)

    private var shouldMove = false
    private var randomTimerDelay = 500L

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "old" -> {
                mc.gameSettings.keyBindForward.pressed = true

                if (delayTimer.hasTimePassed(500)) {
                    thePlayer.rotationYaw += 180F
                    delayTimer.reset()
                }
            }
            "random" -> {
                getRandomMoveKeyBind()!!.pressed = shouldMove

                if (!delayTimer.hasTimePassed(randomTimerDelay)) return
                shouldMove = false
                randomTimerDelay = 500L
                when (RandomUtils.nextInt(0, 6)) {
                    0 -> {
                        if (thePlayer.onGround) thePlayer.jump()
                        delayTimer.reset()
                    }
                    1 -> {
                        if (!thePlayer.isSwingInProgress) thePlayer.swingItem()
                        delayTimer.reset()
                    }
                    2 -> {
                        randomTimerDelay = RandomUtils.nextInt(0, 1000).toLong()
                        shouldMove = true
                        delayTimer.reset()
                    }
                    3 -> {
                        thePlayer.inventory.currentItem = RandomUtils.nextInt(0, 9)
                        mc.playerController.updateController()
                        delayTimer.reset()
                    }
                    4 -> {
                        thePlayer.rotationYaw += RandomUtils.nextFloat(-180.0F, 180.0F)
                        delayTimer.reset()
                    }
                    5 -> {
                        if (thePlayer.rotationPitch <= -90 || thePlayer.rotationPitch >= 90) thePlayer.rotationPitch =
                            0F
                        thePlayer.rotationPitch += RandomUtils.nextFloat(-10.0F, 10.0F)
                        delayTimer.reset()
                    }
                }
            }
            "custom" -> {
                if (moveValue.get())
                    mc.gameSettings.keyBindForward.pressed = true

                if (jumpValue.get() && thePlayer.onGround)
                    thePlayer.jump()

                if (rotateValue.get() && delayTimer.hasTimePassed(rotationDelayValue.get().toLong())) {
                    thePlayer.rotationYaw += rotationAngleValue.get()
                    if (thePlayer.rotationPitch <= -90 || thePlayer.rotationPitch >= 90) thePlayer.rotationPitch = 0F
                    thePlayer.rotationPitch += RandomUtils.nextFloat(0F, 1F) * 2 - 1
                    delayTimer.reset()
                }

                if (swingValue.get() && !thePlayer.isSwingInProgress && swingDelayTimer.hasTimePassed(
                        swingDelayValue.get().toLong()
                    )
                ) {
                    thePlayer.swingItem()
                    swingDelayTimer.reset()
                }
            }
        }
    }

    private fun getRandomMoveKeyBind(): KeyBinding? {
        when (RandomUtils.nextInt(0, 4)) {
            0 -> {
                return mc.gameSettings.keyBindRight
            }
            1 -> {
                return mc.gameSettings.keyBindLeft
            }
            2 -> {
                return mc.gameSettings.keyBindBack
            }
            3 -> {
                return mc.gameSettings.keyBindForward
            }
            else -> {
                return null
            }
        }
    }

    override fun onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindForward))
            mc.gameSettings.keyBindForward.pressed = false
    }
}