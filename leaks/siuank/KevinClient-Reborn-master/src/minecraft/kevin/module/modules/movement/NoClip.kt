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
package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory

class NoClip : Module("NoClip", "Allows you to freely move through walls.", category = ModuleCategory.MOVEMENT) {

    private val speedValue = FloatValue("Speed",0.25F,0.01F,1F)

    override fun onDisable() {
        mc.thePlayer?.noClip = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        thePlayer.noClip = true
        thePlayer.fallDistance = 0f
        thePlayer.onGround = false

        thePlayer.capabilities.isFlying = false
        thePlayer.motionX = 0.0
        thePlayer.motionY = 0.0
        thePlayer.motionZ = 0.0

        val speed = speedValue.get()

        thePlayer.jumpMovementFactor = speed

        if (mc.gameSettings.keyBindJump.isKeyDown)
            thePlayer.motionY += speed.toDouble()

        if (mc.gameSettings.keyBindSneak.isKeyDown)
            thePlayer.motionY -= speed.toDouble()
    }
}