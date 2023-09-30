package alos.stella.module.modules.player

import alos.stella.event.EventTarget
import alos.stella.event.events.MoveEvent
import alos.stella.event.events.UpdateEvent
import alos.stella.module.Module
import alos.stella.module.ModuleCategory
import alos.stella.module.ModuleInfo
import alos.stella.utils.timer.MSTimer
import alos.stella.value.BoolValue
import alos.stella.value.FloatValue
import alos.stella.value.IntegerValue
import alos.stella.value.ListValue
import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "FastUse", spacedName = "Fast Use", description = "Allows you to use items faster.", category = ModuleCategory.PLAYER)
class FastUse : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Instant", "NCP", "AAC" ,"CustomDelay", "AACv4_2"), "NCP")

    private val noMoveValue = BoolValue("NoMove", false)

    private val delayValue = IntegerValue("CustomDelay", 0, 0, 300, { modeValue.get().equals("customdelay", true) })
    private val customSpeedValue = IntegerValue("CustomSpeed", 2, 0, 35, " packet", { modeValue.get().equals("customdelay", true) })
    private val customTimer = FloatValue("CustomTimer", 1.1f, 0.5f, 2f, "x", { modeValue.get().equals("customdelay", true) })

    private val msTimer = MSTimer()
    private var usedTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }

        if (!mc.thePlayer.isUsingItem) {
            msTimer.reset()
            return
        }

        val usingItem = mc.thePlayer.itemInUse.item

        if (usingItem is ItemFood || usingItem is ItemBucketMilk || usingItem is ItemPotion) {
            when (modeValue.get().toLowerCase()) {
                "instant" -> {
                    repeat(32) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                    }

                    mc.playerController.onStoppedUsingItem(mc.thePlayer)
                }

                "ncp" -> if (mc.thePlayer.itemInUseDuration > 14) {
                    repeat(20) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                    }

                    mc.playerController.onStoppedUsingItem(mc.thePlayer)
                }

                "aac" -> {
                    mc.timer.timerSpeed = 1.1F
                    usedTimer = true
                }

                "customdelay" -> {
                    mc.timer.timerSpeed = customTimer.get()
                    usedTimer = true

                    if (!msTimer.hasTimePassed(delayValue.get().toLong()))
                        return

                    repeat(customSpeedValue.get()) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                    }

                    msTimer.reset()
                }
                //move while eating -> flag. recommend enable noMove
                "aacv4_2" -> {
                    mc.timer.timerSpeed = 0.49F
                    usedTimer = true
                    if (mc.thePlayer.itemInUseDuration > 13) {
                        repeat(23) {
                            mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                        }

                        mc.playerController.onStoppedUsingItem(mc.thePlayer)
                    }
                }
            }
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        if (event == null) return

        if (!state || !mc.thePlayer.isUsingItem || !noMoveValue.get()) return
        val usingItem = mc.thePlayer.itemInUse.item
        if ((usingItem is ItemFood || usingItem is ItemBucketMilk || usingItem is ItemPotion))
            event.zero()
    }

    override fun onDisable() {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
    }

    override val tag: String?
        get() = modeValue.get()
}
