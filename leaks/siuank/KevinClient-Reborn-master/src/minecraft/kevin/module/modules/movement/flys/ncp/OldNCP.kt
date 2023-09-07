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
package kevin.module.modules.movement.flys.ncp

import kevin.event.UpdateEvent
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer

object OldNCP : FlyMode("OldNCPFly") {
    override fun onEnable() {
        if (!mc.thePlayer.onGround) {
            return
        }

        repeat(3) {
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 1.01,
                    mc.thePlayer.posZ,
                    false
                )
            )
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    false
                )
            )
        }

        mc.thePlayer.jump()
        mc.thePlayer.swingItem()
    }

    override fun onUpdate(event: UpdateEvent) {
        if (fly.launchY > mc.thePlayer.posY) {
            mc.thePlayer.motionY = -0.000000000000000000000000000000001
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown) {
            mc.thePlayer.motionY = -0.2
        }

        if (mc.gameSettings.keyBindJump.isKeyDown && mc.thePlayer.posY < fly.launchY - 0.1) {
            mc.thePlayer.motionY = 0.2
        }

        MovementUtils.strafe()
    }
}