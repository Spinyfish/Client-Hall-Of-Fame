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
package kevin.hud.element.elements

import kevin.event.EventTarget
import kevin.event.Listenable
import kevin.event.PacketEvent
import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.hud.element.Side
import kevin.main.KevinClient
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.utils.MSTimer
import kevin.utils.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "PacketCounter",single = true)
class PacketCounter(x: Double = 100.0, y: Double = 30.0, scale: Float = 1F, side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP)) : Element(x, y, scale, side),Listenable {

    init {
        KevinClient.eventManager.registerListener(this)
    }
    override fun handleEvents(): Boolean = true

    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet = event.packet
        if (packet.javaClass.name.contains("net.minecraft.network.play.client.",ignoreCase = true)){
            sentPackets += 1
        }
        if (packet.javaClass.name.contains("net.minecraft.network.play.server.",ignoreCase = true)){
            receivedPackets += 1
        }
    }

    private var sentPackets = 0
    private var receivedPackets = 0
    private var sentPacketsList = ArrayList<Int>()
    private var receivedPacketsList = ArrayList<Int>()
    private var Timer = MSTimer()

    private val packetCounterHeight = IntegerValue("PacketCounterHeight", 50, 30, 150)
    private val packetCounterWidth = IntegerValue("PacketCounterWidth", 100, 100, 300)
    private val packetCounterUpdateDelay = IntegerValue("PacketCounterUpdateDelay",500,0,2000)
    private val packetCounterMessage = ListValue("PacketCounterMessageMode", arrayOf("None","Right","Up"),"Right")

    override fun drawElement(): Border {
        val height = packetCounterHeight
        val width = packetCounterWidth.get()
        val delay = packetCounterUpdateDelay.get()
        val tickdelay = delay/50
        val messageMode = packetCounterMessage.get()
        if (Timer.hasTimePassed(delay.toLong())) {
            Timer.reset()
            sentPacketsList.add(sentPackets)
            receivedPacketsList.add(receivedPackets)
            sentPackets = 0
            receivedPackets = 0
            while (sentPacketsList.size > width) {
                sentPacketsList.removeAt(0)
            }
            while (receivedPacketsList.size > width) {
                receivedPacketsList.removeAt(0)
            }
        }
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(2F)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)

        GL11.glBegin(GL11.GL_LINES)

        val sentSize = sentPacketsList.size
        val receivedSize = receivedPacketsList.size
        val sentStart = (if (sentSize > width) sentSize - width else 0)
        val receivedStart = (if (receivedSize > width) receivedSize - width else 0)
        for (i in sentStart until sentSize - 1) {
            val y = sentPacketsList[i] * 10 * 0.25F / tickdelay
            val y1 = sentPacketsList[i + 1] * 10 * 0.25F / tickdelay

            RenderUtils.glColor(Color(255, 0, 0, 255))
            GL11.glVertex2d(i.toDouble() - sentStart, height.get() + 1 - y.coerceAtMost(height.get().toFloat()).toDouble())
            GL11.glVertex2d(i + 1.0 - sentStart, height.get() + 1 - y1.coerceAtMost(height.get().toFloat()).toDouble())
        }
        for (i in receivedStart until receivedSize - 1) {
            val y = receivedPacketsList[i] * 10 * 0.03F / tickdelay
            val y1 = receivedPacketsList[i + 1] * 10 * 0.03F / tickdelay

            RenderUtils.glColor(Color(0, 255, 0, 255))
            GL11.glVertex2d(i.toDouble() - receivedStart, height.get()*2 + 1 - y.coerceAtMost(height.get().toFloat()).toDouble() + if (messageMode.equals("Up",true)) KevinClient.fontManager.font35!!.fontHeight else 0)
            GL11.glVertex2d(i + 1.0 - receivedStart, height.get()*2 + 1 - y1.coerceAtMost(height.get().toFloat()).toDouble() + if (messageMode.equals("Up",true)) KevinClient.fontManager.font35!!.fontHeight else 0)
        }

        GL11.glEnd()

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GlStateManager.resetColor()


        if (!messageMode.equals("None",true)) {
            GL11.glPushMatrix()
            GL11.glScaled(0.6, 0.6, 0.6)
            if (messageMode.equals("Right",true)) {
                val y1 = sentPacketsList.last() * 10 * 0.25F / tickdelay
                val y12 = receivedPacketsList.last() * 10 * 0.03F / tickdelay
                KevinClient.fontManager.font35!!.drawString(
                    "Sent ${sentPacketsList.last()} packets in the past $delay MS.",
                    (sentPacketsList.lastIndex + 4F - sentStart) / 0.6F,
                    (height.get() + 1 - y1.coerceAtMost(height.get().toFloat())) / 0.6F,
                    Color(255, 0, 0, 255).rgb
                )
                KevinClient.fontManager.font35!!.drawString(
                    "Received ${receivedPacketsList.last()} packets in the past $delay MS.",
                    (receivedPacketsList.lastIndex + 4F - receivedStart) / 0.6F,
                    (height.get() * 2 + 1 - y12.coerceAtMost(height.get().toFloat())) / 0.6F,
                    Color(0, 255, 0, 255).rgb
                )
            }else if (messageMode.equals("Up",true)){
                KevinClient.fontManager.font35!!.drawString(
                    "Sent ${sentPacketsList.last()} packets in the past $delay MS.",
                    0F,
                    (-KevinClient.fontManager.font35!!.fontHeight/2) / 0.6F,
                    Color(255, 0, 0, 255).rgb
                )
                KevinClient.fontManager.font35!!.drawString(
                    "Received ${receivedPacketsList.last()} packets in the past $delay MS.",
                    0F,
                    (height.get() + KevinClient.fontManager.font35!!.fontHeight/2) / 0.6F,
                    Color(0, 255, 0, 255).rgb
                )
            }
            GL11.glPopMatrix()
        }
        val x2 = if (!messageMode.equals("Up",true)) KevinClient.fontManager.font35!!.getStringWidth("Received ${receivedPacketsList.last()} packets in the past $delay MS.").toFloat() else (receivedPacketsList.size - receivedStart).toFloat()
        val y2 = height.get()*2 + 2F + if (!messageMode.equals("Up",true)) KevinClient.fontManager.font35!!.fontHeight else 0

        return Border(0F,0F,x2,y2)
    }
}