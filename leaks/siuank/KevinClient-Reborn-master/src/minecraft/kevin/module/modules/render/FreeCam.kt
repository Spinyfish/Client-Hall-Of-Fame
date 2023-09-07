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
package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.ChatUtils
import kevin.utils.MovementUtils
import kevin.utils.PacketUtils
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.client.C0BPacketEntityAction

class FreeCam : Module("FreeCam", "Allows you to move out of your body.", category = ModuleCategory.RENDER) {
    private val speedValue = FloatValue("Speed", 0.8f, 0.1f, 2f)
    private val flyValue = BooleanValue("Fly", true)
    private val noClipValue = BooleanValue("NoClip", true)
    private val noPacket = BooleanValue("NoPlayerPacket", false)
    private val bypass = BooleanValue("Bypass", false)
    private val debug = BooleanValue("Debug", false)

    private var fakePlayer: EntityOtherPlayerMP? = null

    private var oldX = 0.0
    private var oldY = 0.0
    private var oldZ = 0.0
    private var oldOnGround = false
    private var positionUpdateTicks = 0

    override fun onEnable() {
        val thePlayer = mc.thePlayer ?: return

        oldX = thePlayer.posX
        oldY = thePlayer.posY
        oldZ = thePlayer.posZ
        oldOnGround = thePlayer.onGround

        val playerMP = EntityOtherPlayerMP(mc.theWorld!!, thePlayer.gameProfile)

        positionUpdateTicks = mc.thePlayer.positionUpdateTicks

        playerMP.rotationYawHead = thePlayer.rotationYawHead
        playerMP.renderYawOffset = thePlayer.renderYawOffset
        playerMP.rotationYawHead = thePlayer.rotationYawHead
        playerMP.copyLocationAndAnglesFrom(thePlayer)

        mc.theWorld!!.addEntityToWorld(-1000, playerMP)

        if (noClipValue.get())
            thePlayer.noClip = true

        fakePlayer = playerMP
    }

    override fun onDisable() {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || fakePlayer == null)
            return

        thePlayer.setPositionAndRotation(oldX, oldY, oldZ, thePlayer.rotationYaw, thePlayer.rotationPitch)

        mc.theWorld!!.removeEntityFromWorld(fakePlayer!!.entityId)
        fakePlayer = null

        thePlayer.motionX = 0.0
        thePlayer.motionY = 0.0
        thePlayer.motionZ = 0.0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer!!

        if (noClipValue.get())
            thePlayer.noClip = true

        thePlayer.fallDistance = 0.0f

        if (flyValue.get()) {
            val value = speedValue.get()

            thePlayer.motionY = 0.0
            thePlayer.motionX = 0.0
            thePlayer.motionZ = 0.0

            if (mc.gameSettings.keyBindJump.isKeyDown)
                thePlayer.motionY += value

            if (mc.gameSettings.keyBindSneak.isKeyDown)
                thePlayer.motionY -= value

            MovementUtils.strafe(value)
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer) {
            if (!noPacket.get() && bypass.get()) {
                PacketUtils.sendPacketNoEvent(
                    if (positionUpdateTicks >= 20) {
                        positionUpdateTicks = 0
                        if (debug.get())
                            ChatUtils.messageWithStart("FreeCam: C04")
                        C04PacketPlayerPosition(fakePlayer!!.posX, fakePlayer!!.posY, fakePlayer!!.posZ, oldOnGround)
                    } else {
                        positionUpdateTicks++
                        C03PacketPlayer(oldOnGround)
                    }
                )
            }
            event.cancelEvent()
        }
        if (packet is C0BPacketEntityAction)
            event.cancelEvent()
    }
}