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

import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils
import net.minecraft.potion.Potion

object LegacyNCP : SpeedMode("LegacyNCP") {  // from Rise
    private val airSpeedValue = FloatValue("AirSpeed", 0.2F, 0F, 2F)
    private val jumpModifierValue = FloatValue("JumpModifier", 1.02F, 1F, 1.25F)
    private val speedPotionModifierValue = FloatValue("SpeedEffectModifier", 0.03F, 0F, 0.1F)
    private val lowHopValue = BooleanValue("LowHop", true)
    private val timerValue = BooleanValue("UseTimer", true)
    override fun onPreMotion() {
        mc.timer.timerSpeed = 1F
        mc.thePlayer.speedInAir = airSpeedValue.get() / 10F
        if (!MovementUtils.isMoving
            || mc.thePlayer.isInLava
            || mc.thePlayer.isInWater
            || mc.thePlayer.inWeb
            || mc.thePlayer.isOnLadder) {
            return
        }
        if (mc.thePlayer.onGround) {
            if (!mc.gameSettings.keyBindJump.isKeyDown)
                mc.thePlayer.jump()
            if (lowHopValue.get()) {
                mc.thePlayer.motionY = MovementUtils.getJumpMotion(0.4 + (Math.random() / 500.0))
            }
            var speed = jumpModifierValue.get()
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                speed += (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1) * speedPotionModifierValue.get()
            }
            mc.thePlayer.motionX *= speed
            mc.thePlayer.motionZ *= speed
        }
        if (timerValue.get()) {
            mc.timer.timerSpeed = 1.337F - MovementUtils.speed
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                mc.timer.timerSpeed += (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1) / 12.5F
            }
            if (mc.timer.timerSpeed > 1.5F) {
                mc.timer.timerSpeed = 1.5F;
            } else if (mc.timer.timerSpeed < 0.6F) {
                mc.timer.timerSpeed = 0.6F;
            }
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1F
        mc.thePlayer.speedInAir = 0.02F
    }
}