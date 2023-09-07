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
import kevin.event.PacketEvent
import kevin.event.Render3DEvent
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.ColorUtils.rainbow
import kevin.utils.MSTimer
import kevin.utils.RenderUtils
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class Blink : Module("Blink", "Suspends all movement packets.", category = ModuleCategory.PLAYER) {
    private val packets = LinkedBlockingQueue<Packet<*>>()
    private val c0fs = LinkedBlockingQueue<Packet<*>>()
    private var fakePlayer: EntityOtherPlayerMP? = null
    private var disableLogger = false
    private val positions = LinkedList<DoubleArray>()
    private val pulseValue = BooleanValue("Pulse", false)
    private val pulseDelayValue = IntegerValue("PulseDelay", 1000, 500, 5000)
    private val grimAC = ListValue("GrimAC-TimerMode", arrayOf("None", "Storage", "Cancel"), "None")
    private val pulseTimer = MSTimer()
    private val colorRainbow = BooleanValue("ColorRainbow",true)
    private val colorRedValue = IntegerValue("R",255,0,255)
    private val colorGreenValue = IntegerValue("G",255,0,255)
    private val colorBlueValue = IntegerValue("B",255,0,255)

    override fun onEnable() {
        val thePlayer = mc.thePlayer ?: return

        if (!pulseValue.get()) {
            val faker = EntityOtherPlayerMP(mc.theWorld!!, thePlayer.gameProfile)


            faker.rotationYawHead = thePlayer.rotationYawHead;
            faker.renderYawOffset = thePlayer.renderYawOffset;
            faker.copyLocationAndAnglesFrom(thePlayer)
            faker.rotationYawHead = thePlayer.rotationYawHead
            mc.theWorld!!.addEntityToWorld(-1337, faker)


            fakePlayer = faker
        }
        synchronized(positions) {
            positions.add(doubleArrayOf(thePlayer.posX, thePlayer.entityBoundingBox.minY + thePlayer.eyeHeight / 2, thePlayer.posZ))
            positions.add(doubleArrayOf(thePlayer.posX, thePlayer.entityBoundingBox.minY, thePlayer.posZ))
        }
        pulseTimer.reset()
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        blink()

        val faker = fakePlayer

        if (faker != null) {
            mc.theWorld?.removeEntityFromWorld(faker.entityId)
            fakePlayer = null
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (event.isCancelled) return

        if (mc.thePlayer == null || disableLogger)
            return

        if ((packet)is C03PacketPlayer) // Cancel all movement stuff
            event.cancelEvent()

        if ((packet)is C03PacketPlayer.C04PacketPlayerPosition || (packet)is C03PacketPlayer.C05PacketPlayerLook || (packet)is C03PacketPlayer.C06PacketPlayerPosLook ||
            (packet)is C08PacketPlayerBlockPlacement ||
            (packet)is C0APacketAnimation ||
            (packet)is C0BPacketEntityAction || (packet)is C02PacketUseEntity) {
            event.cancelEvent()
            packets.add(packet)
        }

        if ((packet)is C0FPacketConfirmTransaction && grimAC notEqual "None") {
            event.cancelEvent()
            if (grimAC equal "Storage") c0fs.add(packet)
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer ?: return

        synchronized(positions) { positions.add(doubleArrayOf(thePlayer.posX, thePlayer.entityBoundingBox.minY, thePlayer.posZ)) }
        if (pulseValue.get() && pulseTimer.hasTimePassed(pulseDelayValue.get().toLong())) {
            blink()
            pulseTimer.reset()
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val breadcrumbs = this
        val color = if (breadcrumbs.colorRainbow.get()) rainbow() else Color(breadcrumbs.colorRedValue.get(), breadcrumbs.colorGreenValue.get(), breadcrumbs.colorBlueValue.get())
        synchronized(positions) {
            GL11.glPushMatrix()
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            mc.entityRenderer.disableLightmap()
            GL11.glBegin(GL11.GL_LINE_STRIP)
            RenderUtils.glColor(color)
            val renderPosX: Double = mc.renderManager.viewerPosX
            val renderPosY: Double = mc.renderManager.viewerPosY
            val renderPosZ: Double = mc.renderManager.viewerPosZ
            for (pos in positions) GL11.glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ)
            GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
            GL11.glEnd()
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glPopMatrix()
        }
    }

    override val tag: String
        get() = packets.size.toString()

    private fun blink() {
        try {
            disableLogger = true

            while (!packets.isEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }

            // send c0f after movement to bypass timer check
            // don't ignore c0f cause some server will detect lost C0F
            while (c0fs.isNotEmpty()) {
                mc.netHandler.networkManager.sendPacket(c0fs.take())
            }

            disableLogger = false
        } catch (e: Exception) {
            e.printStackTrace()
            disableLogger = false
        }
        synchronized(positions) { positions.clear() }
    }
}