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
package kevin.module.modules.movement.flys.matrix

import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.FloatValue
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MSTimer
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import java.util.concurrent.LinkedBlockingQueue

object NewMatrixClip : FlyMode("NewMatrixClip")  { //from FDP
    private val clipMode = ListValue("${valuePrefix}BypassMode", arrayOf("Clip1","Clip2","Clip3","CustomClip"), "Clip2")
    //    private val clipSmart = BoolValue("${valuePrefix}Clip2-SmartClip", true).displayable { clipMode.equals("Clip2") }
    private val customClip = IntegerValue("${valuePrefix}Custom-ClipDelay",736,500,1500)
    private val customBlink = IntegerValue("${valuePrefix}Custom-BlinkDelay",909,500,1500)
    private val yclip = FloatValue("${valuePrefix}YClip", 10f, 5f, 20f)
    private val packets = LinkedBlockingQueue<Packet<INetHandlerPlayServer>>()
    private val timer = MSTimer()
    private val timer2 = MSTimer()

    private var blinkTime = 0
    private var clipTime = 0
    //    private var clipTimes = 0
    private var disableLogger = false
//    private var shouldClip = true

    override fun onEnable() {
        timer.reset()
        timer2.reset()
//        clipTimes = 0
//        shouldClip = true
    }

    override fun onUpdate(event: UpdateEvent) {
//        if (!shouldClip) return

        when (clipMode.get().lowercase()) {
            "clip1" -> {
                blinkTime = 736
                clipTime = 909
            }
            "clip2" -> {
                blinkTime = 1000
                clipTime = 909
/*                if (clipTimes == 2) {
                    if (!clipSmart.get()) {
                        LiquidBounce.hud.addNotification(Notification("Clip success", "To successfully clip disable fly now", NotifyType.SUCCESS, 2000))
                    } else {
                        if (timer2.hasTimePassed(150)) {
                            shouldClip = false
                            LiquidBounce.hud.addNotification(Notification("Smart Clip", "Smart Clip stopped cliping, you can disable fly now.", NotifyType.WARNING, 5000))
                            LiquidBounce.hud.addNotification(Notification("Smart Clip", "If you have tped back, disable Smart Clip or try again.", NotifyType.WARNING, 5000))
                } else if (clipTimes > 2) {
                    if (!clipSmart.get()) {
                        LiquidBounce.hud.addNotification(Notification("Clip fail", "Clipped too many times, disable fly and try again", NotifyType.ERROR, 3000))
                    }
                }
*/
            }
            "clip3" -> {
                blinkTime = 909
                clipTime = 1000
            }
            "CustomClip" -> {
                blinkTime = customBlink.get()
                clipTime = customClip.get()
            }
        }
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionZ = 0.0


        if(timer.hasTimePassed(blinkTime.toLong())) {
            timer.reset()
            try {
                disableLogger = true
                while (!packets.isEmpty()) {
                    mc.netHandler.addToSendQueue(packets.take())
                }
                disableLogger = false
            } finally {
                disableLogger = false
            }
        }
        if(timer2.hasTimePassed((clipTime.toLong()))) {
            timer2.reset()
//            clipTimes ++
            mc.thePlayer.setPosition(mc.thePlayer.posX , mc.thePlayer.posY + yclip.get(), mc.thePlayer.posZ)
        }
    }

    override fun onDisable() {
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (mc.thePlayer == null || disableLogger) return
        if (packet is C03PacketPlayer) {
            event.cancelEvent()
        }
        if (packet is C03PacketPlayer.C04PacketPlayerPosition || packet is C03PacketPlayer.C06PacketPlayerPosLook ||
            packet is C08PacketPlayerBlockPlacement ||
            packet is C0APacketAnimation ||
            packet is C0BPacketEntityAction || packet is C02PacketUseEntity
        ) {
            event.cancelEvent()
            packets.add(packet as Packet<INetHandlerPlayServer>)
        }
    }
}