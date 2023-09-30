package alos.stella.module.modules.player

import alos.stella.event.EventTarget
import alos.stella.event.events.PacketEvent
import alos.stella.event.events.UpdateEvent
import alos.stella.event.events.WorldEvent
import alos.stella.module.Module
import alos.stella.module.ModuleCategory
import alos.stella.module.ModuleInfo
import alos.stella.utils.ClientUtils
import alos.stella.utils.MovementUtils
import alos.stella.utils.PacketUtils
import alos.stella.utils.timer.MSTimer
import alos.stella.value.BoolValue
import alos.stella.value.IntegerValue
import alos.stella.value.ListValue
import alos.stella.value.TextValue
import net.minecraft.network.INetHandler
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.*
import org.apache.commons.lang3.RandomUtils

import java.util.LinkedList

@ModuleInfo(name = "FakeLag", spacedName = "Fake Lag", description = "Lagging yourself server-side, and client-side.", category = ModuleCategory.PLAYER)
class FakeLag : Module() {

    private val fakeLagMode = ListValue("Mode", arrayOf("All", "InBound", "OutBound"), "All")
    private val fakeLagMoveOnly = BoolValue("MoveOnly", true)

    private val minRand: IntegerValue = object : IntegerValue("MinDelay", 0, 0, 20000, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = maxRand.get()
            if (v < newValue) set(v)
        }
    }
    private val maxRand: IntegerValue = object : IntegerValue("MaxDelay", 0, 0, 20000, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = minRand.get()
            if (v > newValue) set(v)
        }
    }

    private val fakeLagInclude = BoolValue("Include", false)
    private val fakeLagExclude = BoolValue("Exclude", false)
    private val fakeLagIncludeClasses = TextValue("IncludeClass", "c0f,confirmtransaction,packetplayer,c17", { fakeLagInclude.get() })
    private val fakeLagExcludeClasses = TextValue("ExcludeClass", "c0f,confirmtransaction,packetplayer,c17", { fakeLagExclude.get() })

    // debug
    private val debugValue = BoolValue("Debug", false)

    // variables
    private val outBus = LinkedList<Packet<INetHandlerPlayServer>>()
    private val inBus = LinkedList<Packet<INetHandlerPlayClient>>()

    private val ignoreBus = LinkedList<Packet<out INetHandler>>()

    private val inTimer = MSTimer()
    private val outTimer = MSTimer()

    private var inDelay = 0
    private var outDelay = 0

    fun debug(s: String) {
        if (debugValue.get())
            ClientUtils.displayChatMessage("§7[§6§lFakeLag§7]§f $s")
    }

    override fun onEnable() {
        inBus.clear()
        outBus.clear()
        ignoreBus.clear()

        inTimer.reset()
        outTimer.reset()
    }

    override fun onDisable() {
        while (inBus.size > 0)
            inBus.poll()?.processPacket(mc.netHandler)

        while (outBus.size > 0) {
            val upPacket = outBus.poll() ?: continue
            PacketUtils.sendPacketNoEvent(upPacket)
        }

        inBus.clear()
        outBus.clear()
        ignoreBus.clear()
    }

    @EventTarget(priority = -100)
    fun onPacket(event: PacketEvent) {
        mc.thePlayer ?: return
        mc.theWorld ?: return
        val packet = event.packet ?: return
        if (ignoreBus.remove(packet)) return

        if ((fakeLagMode.get().equals("outbound", true) || fakeLagMode.get().equals("all", true))
            && packet::class.java!!.getSimpleName().startsWith("C", true)
            && (!fakeLagInclude.get() || fakeLagIncludeClasses.get().split(",").find { packet::class.java!!.getSimpleName().contains(it, true) } != null)
            && (!fakeLagExclude.get() || fakeLagExcludeClasses.get().split(",").find { packet::class.java!!.getSimpleName().contains(it, true) } == null)) {
            debug("outbound, ${packet::class.java!!.getSimpleName()}")
            outBus.add(packet as Packet<INetHandlerPlayServer>)
            ignoreBus.add(packet)
            event.cancelEvent()
        }

        if ((fakeLagMode.get().equals("inbound", true) || fakeLagMode.get().equals("all", true))
            && packet::class.java!!.getSimpleName().startsWith("S", true)
            && (!fakeLagInclude.get() || fakeLagIncludeClasses.get().split(",").find { packet::class.java!!.getSimpleName().contains(it, true) } != null)
            && (!fakeLagExclude.get() || fakeLagExcludeClasses.get().split(",").find { packet::class.java!!.getSimpleName().contains(it, true) } == null)) {
            debug("inbound, ${packet::class.java!!.getSimpleName()}")
            inBus.add(packet as Packet<INetHandlerPlayClient>)
            ignoreBus.add(packet)
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        inBus.clear()
        outBus.clear()
        ignoreBus.clear()

        inTimer.reset()
        outTimer.reset()
    }

    @EventTarget(priority = -5)
    fun onUpdate(event: UpdateEvent) {
        mc.netHandler ?: return

        if (!inBus.isEmpty() && ((fakeLagMoveOnly.get() && !MovementUtils.isMoving()) || inTimer.hasTimePassed(inDelay.toLong()))) {
            while (inBus.size > 0)
                inBus.poll()?.processPacket(mc.netHandler)
            inDelay = RandomUtils.nextInt(minRand.get(), maxRand.get())
            inTimer.reset()
            debug("poll (in)")
        }
        if (!outBus.isEmpty() && ((fakeLagMoveOnly.get() && !MovementUtils.isMoving()) || outTimer.hasTimePassed(outDelay.toLong()))) {
            while (outBus.size > 0) {
                val upPacket = outBus.poll() ?: continue
                PacketUtils.sendPacketNoEvent(upPacket)
            }
            outDelay = RandomUtils.nextInt(minRand.get(), maxRand.get())
            outTimer.reset()
            debug("poll (out)")
        }
    }
}