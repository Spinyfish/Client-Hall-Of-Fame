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

import blur.GaussianBlur
import kevin.persional.milk.utils.StencilUtil
import kevin.event.*
import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.hud.element.Side
import kevin.main.KevinClient
import kevin.utils.MSTimer
import kevin.utils.RenderUtils
import kevin.utils.TimeList
import kevin.utils.getPing
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S03PacketTimeUpdate
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList

@ElementInfo("Information")
class Information(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,side: Side = Side(Side.Horizontal.MIDDLE, Side.Vertical.UP)) : Element(x, y, scale, side),Listenable {

    //TPS
    private var tps = 20.0
    private val packetHistoryTime = CopyOnWriteArrayList<Long>()
    private val timeTimer = MSTimer()
    private val timeTimer2 = MSTimer()
    //Kills
    private var kills = 0
    private val attackEntities = CopyOnWriteArrayList<EntityLivingBase>()
    //BPS
    private var bps = 0.0
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0
    //Playtime
    private val startTime: Long
    //Ping
    private var ping = -1
    var cps = TimeList<Int>(1000)

    override fun drawElement(): Border {

        GL11.glTranslated(-renderX, -renderY, 0.0)
        //绘制Bloom & Blur
        StencilUtil.initStencilToWrite()
        RenderUtils.drawRect(renderX,renderY,150 + renderX,58 + renderY, Color(0,0,0,255).rgb)
        StencilUtil.readStencilBuffer(1)
        GaussianBlur.renderBlur(12F)
        StencilUtil.uninitStencilBuffer()

        GL11.glTranslated(renderX, renderY, 0.0)

        RenderUtils.drawRect(0,0,150,70, Color(0,0,0,10).rgb)
        KevinClient.fontManager.font40.drawCenteredString("Session Info",
            150 / 2F,
            5F,Color(255,255,255).rgb)
        var y = KevinClient.fontManager.font40.fontHeight + 0.0F

        GL11.glPushMatrix()
        RenderUtils.drawLineStart(Color(20,100,200,200),4F)
        RenderUtils.drawLine(0.0,0.0,150.0,0.0)
        RenderUtils.drawLineEnd()
        GL11.glPopMatrix()
        KevinClient.fontManager.fontMisans32.drawString("Health: ",
            10F,
            15F,Color(255,255,255).rgb)
        KevinClient.fontManager.fontMisans32.drawString(mc.thePlayer.health.toInt().toString(),
            140F - KevinClient.fontManager.fontMisans32.getStringWidth(mc.thePlayer.health.toInt().toString()),
            15F,Color(255,255,255).rgb)


        KevinClient.fontManager.fontMisans32.drawString("Speed: ",
            10F,
            25F,Color(255,255,255).rgb)
        KevinClient.fontManager.fontMisans32.drawString(bps.toString(),
            140F - KevinClient.fontManager.fontMisans32.getStringWidth(bps.toString()),
            25F,Color(255,255,255).rgb)


        KevinClient.fontManager.fontMisans32.drawString("Kills: ",
            10F,
            35F,Color(255,255,255).rgb)
        KevinClient.fontManager.fontMisans32.drawString(kills.toString(),
            140F - KevinClient.fontManager.fontMisans32.getStringWidth(kills.toString()),
            35F,Color(255,255,255).rgb)

        KevinClient.fontManager.fontMisans32.drawString("Username: ",
            10F,
            45F,Color(255,255,255).rgb)
        KevinClient.fontManager.fontMisans32.drawString(mc.thePlayer.name,
            140F - KevinClient.fontManager.fontMisans32.getStringWidth(mc.thePlayer.name),
            45F,Color(255,255,255).rgb)

        return Border(10F,10F,100F,70F)
    }
    init {
        KevinClient.eventManager.registerListener(this)
        startTime = System.currentTimeMillis()
    }
    override fun handleEvents(): Boolean = true
    @EventTarget
    fun onAttack(event: AttackEvent){
        val entity = event.targetEntity ?: return
        if (entity !is EntityLivingBase || entity.isDead || entity.health == 0F) return
        if (entity !in attackEntities) attackEntities.add(entity)
    }
    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet = event.packet
        if (packet is C0APacketAnimation) cps.add(1)
        if (packet is S03PacketTimeUpdate){
            packetHistoryTime.add(System.currentTimeMillis())
        }
        if (packet !is S02PacketChat && packet.javaClass.name.contains("net.minecraft.network.play.server.",true)) timeTimer.reset()
    }
    @EventTarget
    fun onWorld(event: WorldEvent){
        tps = 20.0
        kills = 0
        ping = -1
        attackEntities.clear()
        packetHistoryTime.clear()
    }
    override fun updateElement() {
        //TPS
        updateTPS()
        //Kills
        attackEntities.forEach {if(it.isDead || it.health == 0F){kills+=1;attackEntities.remove(it)}}
        //BPS
        updateBPS()
        ping = mc.thePlayer?.getPing() ?: -1
    }

    private fun updateTPS(){
        /**
        var count = 0.0
        packetHistoryTime.forEach {if(System.currentTimeMillis()<1000L+it)count++}
        if (count == packetHistoryTime.size.toDouble()) return else if (count + 10 < packetHistoryTime.size) packetHistoryTime.removeFirst()
        if (mc.currentServerData == null || mc.isIntegratedServerRunning) count /= 2.0
        tps = count * 20.0
        **/
        val d = 4
        if (packetHistoryTime.size<2) return
        while(packetHistoryTime.size>d) packetHistoryTime.removeFirst()
        if (System.currentTimeMillis() - packetHistoryTime.last() > 5000L && timeTimer.hasTimePassed(5000L)){
            if (!timeTimer2.hasTimePassed(1000L)) return
            if (tps<1) {tps = 0.0;return}
            tps /= 2
            tps = String.format("%.1f",tps).toDouble()
            timeTimer2.reset()
            return
        } else {
            val p = arrayListOf<Long>()
            if (mc.isIntegratedServerRunning) if (packetHistoryTime.size<3) return
            else p.add(packetHistoryTime[2]-packetHistoryTime[1])
            else packetHistoryTime.forEach {
                if (packetHistoryTime.indexOf(it) + 1 <= packetHistoryTime.size - 1){
                    p.add(packetHistoryTime[packetHistoryTime.indexOf(it) + 1] - it)
                }
            }
            var t = 0L
            p.forEach { t += it }
            tps = String.format("%.1f", 20.0 / ((t / p.size) / 1000.0)).toDouble()
        }
    }

    private fun updateBPS(){
        if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 1) {
            bps = 0.0
            return
        }
        val distance = mc.thePlayer.getDistance(lastX, lastY, lastZ)
        lastX = mc.thePlayer.posX
        lastY = mc.thePlayer.posY
        lastZ = mc.thePlayer.posZ
        bps = String.format("%.2f",distance * (20 * mc.timer.timerSpeed)).toDouble()
    }

    private fun getTime(time:Long): String{
        val h = (time / 1000) / 60 / 60
        val m = (time / 1000) / 60 % 60
        val s = (time / 1000) % 60
        return "${h}h ${m}m ${s}s"
    }
}