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
package kevin.utils

import net.minecraft.potion.Potion
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


object MovementUtils : MinecraftInstance() {
    var speed: Float
        get() = sqrt(mc.thePlayer!!.motionX * mc.thePlayer!!.motionX + mc.thePlayer!!.motionZ * mc.thePlayer!!.motionZ).toFloat()
        set(value) = setMotion(value.toDouble())

    @JvmStatic
    val isMoving: Boolean
        get() = mc.thePlayer != null && (mc.thePlayer!!.movementInput.moveForward != 0f || mc.thePlayer!!.movementInput.moveStrafe != 0f)

    fun hasMotion(): Boolean {
        return mc.thePlayer!!.motionX != 0.0 && mc.thePlayer!!.motionZ != 0.0 && mc.thePlayer!!.motionY != 0.0
    }

    @JvmStatic
    @JvmOverloads
    fun strafe(speed: Float = this.speed) {
        if (!isMoving) return
        val yaw = direction
        val thePlayer = mc.thePlayer!!
        thePlayer.motionX = -sin(yaw) * speed
        thePlayer.motionZ = cos(yaw) * speed
    }

    @JvmStatic
    @JvmOverloads
    fun strafeDouble(speed: Double) {
        if (!isMoving) return
        val yaw = direction
        val thePlayer = mc.thePlayer!!
        thePlayer.motionX = -sin(yaw) * speed
        thePlayer.motionZ = cos(yaw) * speed
    }

    @JvmStatic
    fun forward(length: Double) {
        val thePlayer = mc.thePlayer!!
        val yaw = toRadians(thePlayer.rotationYaw.toDouble())
        thePlayer.setPosition(thePlayer.posX + -sin(yaw) * length, thePlayer.posY, thePlayer.posZ + cos(yaw) * length)
    }

    @JvmStatic
    val direction: Double
        get() {
            val thePlayer = mc.thePlayer!!
            var rotationYaw = thePlayer.rotationYaw
            if (thePlayer.moveForward < 0f) rotationYaw += 180f
            var forward = 1f
            if (thePlayer.moveForward < 0f) forward = -0.5f else if (thePlayer.moveForward > 0f) forward = 0.5f
            if (thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward
            if (thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward
            return toRadians(rotationYaw.toDouble())
        }

    val movingYaw: Float
        get() = (direction * 180f / Math.PI).toFloat()

    fun move(speed: Float) {
        if (!isMoving) return
        val yaw = direction
        mc.thePlayer.motionX += -sin(yaw) * speed
        mc.thePlayer.motionZ += cos(yaw) * speed
    }
    fun setMotion(speed: Double) {
        var forward = mc.thePlayer.movementInput.moveForward.toDouble()
        var strafe = mc.thePlayer.movementInput.moveStrafe.toDouble()
        var yaw = mc.thePlayer.rotationYaw
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (if (forward > 0.0) -45 else 45).toFloat()
                } else if (strafe < 0.0) {
                    yaw += (if (forward > 0.0) 45 else -45).toFloat()
                }
                strafe = 0.0
                if (forward > 0.0) {
                    forward = 1.0
                } else if (forward < 0.0) {
                    forward = -1.0
                }
            }
            val cos = cos(toRadians((yaw + 90.0f).toDouble()))
            val sin = sin(toRadians((yaw + 90.0f).toDouble()))
            mc.thePlayer.motionX = (forward * speed * cos +
                    strafe * speed * sin)
            mc.thePlayer.motionZ = (forward * speed * sin -
                    strafe * speed * cos)
        }
    }
    @JvmStatic
    fun getJumpMotion(base: Double) : Double {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            return base + (mc.thePlayer.getActivePotionEffect(Potion.jump).amplifier + 1) * 0.1
        }
        return base
    }
    fun getBaseMoveSpeed(): Double {
        var baseSpeed = 0.2873
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(
            Potion.moveSpeed
        ).amplifier + 1)
        return baseSpeed
    }
    fun getMoveSpeed(baseSpeed: Double): Double {
        return if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
            baseSpeed * (1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1))
        else
            baseSpeed
    }

    fun resetMotion(y: Boolean) {
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionZ = 0.0
        if(y) mc.thePlayer.motionY = 0.0
    }
}