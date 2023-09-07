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
package kevin.module.modules.movement.flys.vanilla

import kevin.event.UpdateEvent
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C00PacketKeepAlive

object Vanilla : FlyMode("Vanilla") {
    override fun onUpdate(event: UpdateEvent) {
        if (fly.keepAlive.get()) mc.netHandler.addToSendQueue(C00PacketKeepAlive())
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionZ = 0.0
        mc.thePlayer.capabilities.isFlying = false
        if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += fly.speed.get()
        if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= fly.speed.get()
        MovementUtils.strafe(fly.speed.get())
    }
}