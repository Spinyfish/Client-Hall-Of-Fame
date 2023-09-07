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

import de.gerrygames.viarewind.utils.PacketUtil
import kevin.event.*
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.combat.KillAura
import kevin.utils.MSTimer
import kevin.utils.MovementUtils
import kevin.utils.PacketUtils
import net.minecraft.item.*
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*


@Suppress("unused_parameter", "unchecked_cast")
class NoSlow : Module("NoSlow", "Cancels slowness effects caused by soulsand and using items.", category = ModuleCategory.MOVEMENT) {

    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val packetMode = ListValue("PacketMode", arrayOf("None","AntiCheat","AntiCheat2", "SwitchItem", "ReverseEventSwitchItem", "ReverseNCP","AAC","AAC5","Delay","Matrix","Vulcan", "Intave", "GrimAC", "GrimACSwitch", "Bug"),"None")

    val soulsandValue = BooleanValue("Soulsand", true)
    val liquidPushValue = BooleanValue("LiquidPush", true)

    private var lastBlockingStat = false
    private var nextTemp = false
    private var waitC03 = false
    private var funnyBoolean = false
    private var delay = 100L
    private val msTimer = MSTimer()
    private var packetBuf = LinkedList<Packet<INetHandlerPlayServer>>()
    private var aura: KillAura? = null
    private var count = 0
    private var lastItem: ItemStack? = null
    private val isBlocking: Boolean
        get() = (mc.thePlayer.isUsingItem || (KevinClient.moduleManager.getModule(KillAura::class.java)).blockingStatus) && mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword

    override fun onDisable() {
        msTimer.reset()
        nextTemp = false
        waitC03 = false
        packetBuf.clear()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return
        val heldItem = thePlayer.heldItem ?: return

        if (!MovementUtils.isMoving)
            return

        if (aura == null) aura = KevinClient.moduleManager.getModule(KillAura::class.java)
        val aura = this.aura!!
        val isBlocking = (thePlayer.isBlocking || aura.blockingStatus) && (heldItem.item) is ItemSword
        val isUsing = thePlayer.isUsingItem
        if (packetMode equal "GrimACSwitch" && event.eventState == EventState.PRE) {
            if (thePlayer.isUsingItem || thePlayer.isBlocking || aura.blockingStatus) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            }
        }
        if (!isBlocking && !isUsing) return
        val isUsingNotBlocking = !isBlocking

        when(packetMode.get()){
            "AntiCheat" -> if (isBlocking) {
                when (event.eventState) {
                    EventState.PRE -> {
                        mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(0, 0, 0), EnumFacing.DOWN))
                    }
                    EventState.POST -> {
                        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer!!.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F))
                    }
                }
            }
            "AntiCheat2" -> if (isBlocking)  {
                if (event.eventState == EventState.PRE) {
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                } else {
                    mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f))
                }
            }
            "SwitchItem" -> if (event.eventState == EventState.PRE) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            }
            "ReverseEventSwitchItem" -> if (event.eventState == EventState.PRE) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
            } else {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            }
            "ReverseNCP" -> if (isBlocking)  { // from Rise
                when (event.eventState) {
                    EventState.PRE -> {
                        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer!!.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F))
                    }
                    EventState.POST -> {
                        mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                    }
                }
            }
            "AAC" -> if (isBlocking) {
                if (mc.thePlayer.ticksExisted % 3 == 0 && event.eventState == EventState.PRE) {
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(-1, -1, -1), EnumFacing.DOWN))
                } else if (mc.thePlayer.ticksExisted % 3 == 1 && event.eventState == EventState.POST) {
                    C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem())
                }
            }
            "AAC5" -> {
                if (event.eventState == EventState.POST && (mc.thePlayer.isUsingItem || mc.thePlayer.isBlocking || aura.blockingStatus)) {
                    mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f))
                }
            }
            "Delay" -> if (isBlocking) {
                if (event.eventState == EventState.POST) return
                if (!mc.thePlayer.isBlocking) funnyBoolean = false

                if (mc.thePlayer.isBlocking && mc.thePlayer.ticksExisted % 5 == 0 && funnyBoolean) {
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                    funnyBoolean = false
                }

                if (mc.thePlayer.isBlocking && mc.thePlayer.ticksExisted % 5 == 1 && !funnyBoolean) {
                    mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.currentEquippedItem))
                    funnyBoolean = true
                }
            }
            "Intave" -> if (isBlocking) { // from Rise
                if ((mc.thePlayer.isBlocking || aura.blockingStatus) && msTimer.hasTimePassed(delay)) {
                    when (event.eventState) {
                        EventState.PRE -> {
                            mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))

                        }
                        EventState.POST -> {
                            mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                            delay = if (funnyBoolean) {
                                100L
                            } else {
                                200L
                            }
                            funnyBoolean = !funnyBoolean
                            msTimer.reset()
                        }
                    }
                }
            }
            "GrimAC" -> if (isBlocking) {
                if (event.eventState == EventState.PRE) mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(mc.thePlayer == null || mc.theWorld == null)
            return
        if((packetMode equal "Matrix" || packetMode equal "Vulcan") && (lastBlockingStat || isBlocking)) {
            if(msTimer.hasTimePassed(230) && nextTemp) {
                nextTemp = false
                PacketUtils.sendPacketNoEvent(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(-1, -1, -1), EnumFacing.DOWN))
                if(packetBuf.isNotEmpty()) {
                    var canAttack = false
                    for(packet in packetBuf) {
                        if(packet is C03PacketPlayer) {
                            canAttack = true
                        }
                        if(!((packet is C02PacketUseEntity || packet is C0APacketAnimation) && !canAttack)) {
                            PacketUtils.sendPacketNoEvent(packet)
                        }
                    }
                    packetBuf.clear()
                }
            }
            if(!nextTemp) {
                lastBlockingStat = isBlocking
                if (!isBlocking) {
                    return
                }
                PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f))
                nextTemp = true
                waitC03 = packetMode equal "Vulcan"
                msTimer.reset()
            }
        }
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        mc.thePlayer?: return
        mc.theWorld ?: return
        if (packetMode equal "GrimAC") {
            if (mc.thePlayer.isBlocking || aura?.blockingStatus == true) mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer!!.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F))
            else if (mc.thePlayer.isUsingItem) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if(mc.thePlayer == null || mc.theWorld == null)
            return
        val packet = event.packet
        if((packetMode equal "Matrix" || packetMode equal "Vulcan") && nextTemp) {
            if((packet is C07PacketPlayerDigging || packet is C08PacketPlayerBlockPlacement) && isBlocking) {
                event.cancelEvent()
            }else if (packet is C03PacketPlayer || packet is C0APacketAnimation || packet is C0BPacketEntityAction || packet is C02PacketUseEntity || packet is C07PacketPlayerDigging || packet is C08PacketPlayerBlockPlacement) {
                if (packetMode equal "Vulcan" && waitC03 && packet is C03PacketPlayer) {
                    waitC03 = false
                    return
                }
                packetBuf.add(packet as Packet<INetHandlerPlayServer>)
                event.cancelEvent()
            }
        }
    }

    @EventTarget
    fun onClick(event: ClickUpdateEvent) {
        if (packetMode notEqual "Bug") return
        val player = mc.thePlayer ?: return
        val currentItem = player.currentEquippedItem
        if (currentItem != null && (player.isUsingItem || isBlocking) && (player.moveForward != 0.0f || player.moveStrafing != 0.0f) && currentItem.item !is ItemBow) {
            if (lastItem != null && lastItem!! != currentItem) {
                count = 0
            }
            val state = if (currentItem.item is ItemSword) 1 else 3
            if (count != state) {
                event.cancelEvent()
                mc.netHandler.networkManager.sendPacketNoEvent(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                player.stopUsingItem()
                player.closeScreen()
                count = state
            }
            if (event.isCancelled) mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown && mc.inGameHasFocus)
            lastItem = currentItem
        } else {
            count = 0
        }

    }

    override val tag: String
        get() = packetMode.get()

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer!!.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: Item?, isForward: Boolean): Float {
        return when {
            (item)is ItemFood || (item)is ItemPotion || (item)is ItemBucketMilk -> {
                if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
            }
            (item)is ItemSword -> {
                if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
            }
            (item)is ItemBow -> {
                if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
            }
            else -> 0.2F
        }
    }
}