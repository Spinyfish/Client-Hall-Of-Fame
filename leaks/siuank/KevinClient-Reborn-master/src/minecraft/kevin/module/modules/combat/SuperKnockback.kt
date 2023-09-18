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
import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.movement.Sprint
import kevin.module.modules.render.FreeCam
import kevin.utils.ChatUtils
import kevin.utils.MSTimer
import kevin.utils.MovementUtils
import kevin.utils.TimeUtils
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C0BPacketEntityAction

class SuperKnockback : Module("SuperKnockback", "Increases knockback dealt to other entities.", category = ModuleCategory.COMBAT) {
    private val modeValue = ListValue("Mode", arrayOf("ExtraPacket", "Packet", "W-Tap", "Legit", "LegitFast"), "ExtraPacket")
    private val maxDelay: IntegerValue = object : IntegerValue("Legit-MaxDelay", 60, 0, 100) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelay.get()
            if (i > newValue) set(i)

            delay = TimeUtils.randomDelay(minDelay.get(), this.get())
        }
    }
    private val minDelay: IntegerValue = object : IntegerValue("Legit-MinDelay", 50, 0, 100) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelay.get()
            if (i < newValue) set(i)

            delay = TimeUtils.randomDelay(this.get(), maxDelay.get())
        }
    }
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val delayValue = IntegerValue("Delay", 0, 0, 500)
    private val onlyMoveValue = BooleanValue("OnlyMove", true)
    private val onlyGroundValue = BooleanValue("OnlyGround", false)

    private val timer = MSTimer()
    var delay = 0L
    var stopSprint = false
    val stopTimer = MSTimer()
    private var isHit = false
    private val attackTimer = MSTimer()

    override fun onEnable() {
        isHit = false
        if (modeValue equal "Legit" && !KevinClient.moduleManager.getModule(Sprint::class.java).state) {
            ChatUtils.messageWithStart("§cError: You must turn on sprint to use the legit mode SuperKnockBack.")
            this.state = false
        }
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            val player = mc.thePlayer ?: return
            if (event.targetEntity.hurtTime > hurtTimeValue.get()
                || !timer.hasTimePassed(delayValue.get().toLong())
                || (!MovementUtils.isMoving && onlyMoveValue.get())
                || (!mc.thePlayer.onGround && onlyGroundValue.get())
                || KevinClient.moduleManager.getModule(FreeCam::class.java).state)
                return

            when (modeValue.get()) {
                "ExtraPacket" -> {
                    if (player.isSprinting) mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SPRINTING))
                    //player.isSprinting = true
                    player.serverSprintState = true
                }
                "Packet" -> {
                    if (player.isSprinting) mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SPRINTING))
                    player.serverSprintState = true
                }
                "W-Tap" -> {
                    if (mc.thePlayer.isSprinting) {
                        mc.thePlayer.isSprinting = false
                    }
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.thePlayer.serverSprintState = true
                }
                "Legit", "LegitFast" -> {
                    if (!isHit) {
                        isHit = true
                        attackTimer.reset()
                        delay = TimeUtils.randomDelay(minDelay.get(), maxDelay.get())
                    }
                }
            }
            timer.reset()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if  (modeValue equal "LegitFast") {
            if (isHit) {
                mc.thePlayer.sprintingTicksLeft = 0
                stopTimer.reset()
            }
        }
        if (modeValue equal "Legit") {
            if (isHit && attackTimer.hasTimePassed(delay / 2)) {
                isHit = false
                mc.thePlayer.isSprinting = false
                stopSprint = true
                stopTimer.reset()
            }
            if (!KevinClient.moduleManager.getModule(Sprint::class.java).state) {
                ChatUtils.messageWithStart("§cError: You must turn on sprint to use the legit mode SuperKnockBack.")
                this.state = false
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}