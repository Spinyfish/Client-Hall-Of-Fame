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
package kevin.module.modules.combat

import kevin.event.AttackEvent
import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.movement.Fly
import kevin.utils.ChatUtils
import kevin.utils.MSTimer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S0BPacketAnimation

class Criticals : Module(name = "Criticals", description = "Automatically deals critical hits.", category = ModuleCategory.COMBAT) {
    val modeValue = ListValue("Mode", arrayOf("Packet", "NcpPacket", "AACPacket", "NoGround", "Hop", "Jump", "LowJump", "Visual", "MineMora", "Hypixel", "BlocksMC", "Edit", "Edit2"), "Packet")
    val delayValue = IntegerValue("Delay", 0, 0, 500)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val debug = BooleanValue("Debug", false)

    val msTimer = MSTimer()
    private var target = -114514
    private var editPacket = false

    override fun onEnable() {
        if (modeValue.get().equals("NoGround", ignoreCase = true) && mc.thePlayer!!.onGround)
            mc.thePlayer!!.jump()
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            val thePlayer = mc.thePlayer ?: return
            val entity = event.targetEntity
            target = entity.entityId

            if (!thePlayer.onGround || thePlayer.isOnLadder || thePlayer.inWeb || thePlayer.isInWater ||
                thePlayer.isInLava || thePlayer.ridingEntity != null || entity.hurtTime > hurtTimeValue.get() ||
                KevinClient.moduleManager.getModule(Fly::class.java).state || !msTimer.hasTimePassed(delayValue.get().toLong()))
                return

            val x = thePlayer.posX
            val y = thePlayer.posY
            val z = thePlayer.posZ

            when (modeValue.get().lowercase()) {
                "aacpacket" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.05250000001304, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.01400000001304, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                }

                "packet" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0625, z, true))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.1E-5, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false))
                    thePlayer.onCriticalHit(entity)
                }

                "ncppacket" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.1100013579, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0000013579, z, false))
                    thePlayer.onCriticalHit(entity)
                }

                "hop" -> {
                    thePlayer.motionY = 0.1
                    thePlayer.fallDistance = 0.1f
                    thePlayer.onGround = false
                }

//                "jump" -> thePlayer.motionY = 0.42
                "jump" -> mc.thePlayer.jump()
                "lowjump" -> thePlayer.motionY = 0.3425
                "visual" -> thePlayer.onCriticalHit(entity)
                "minemora" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x,y + 0.01145141919810,z,false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x,y + 0.0010999999940395355,z,false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x,y + 0.00150000001304,z,false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x,y + 0.0012016413,z,false))
                }
                "hypixel" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.00430602200102120014, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0410881200712020195, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.00511681200107142817, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.00231121200209234615, z, false))
                }
                "blocksmc" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.05250000001304, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                    thePlayer.onCriticalHit(entity)
                }
                "edit" -> {
                    editPacket = true
                }
                "edit2" -> {
                    editPacket = true
                }
            }

            msTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            if (modeValue equal "Edit2" && editPacket) {
                packet.y -= 1e-8
            }
            if (modeValue.get().equals("NoGround", ignoreCase = true) || editPacket) {
                packet.isOnGround = false
                editPacket = false
            }
        }

        if (debug.get() && packet is S0BPacketAnimation && packet.animationType == 4 && target == packet.entityID) { // Debug
            ChatUtils.messageWithStart("§7[§bCriticals§7] §2Crit: §a${packet.entityID}")
        }
    }

    override val tag: String
        get() = modeValue.get()
}