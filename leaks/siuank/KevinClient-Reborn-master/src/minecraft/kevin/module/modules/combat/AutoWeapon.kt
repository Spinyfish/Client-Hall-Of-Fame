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
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.ItemUtils
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemSword
import net.minecraft.item.ItemTool
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C09PacketHeldItemChange

class AutoWeapon : Module("AutoWeapon", "Automatically selects the best weapon in your hotbar.", category = ModuleCategory.COMBAT) {
    private val mode = ListValue("Mode", arrayOf("Direct", "Spoof"), "Direct")
    private val silentValue : Boolean
        get() = mode equal "Spoof"
    private val switchBack = BooleanValue("SwitchBack", true)
    private val ticksValue = IntegerValue("WaitTicks", 10, 1, 20)
    private val swordsFirst = BooleanValue("SwordsFirst",false)
    private var attackEnemy = false

    private var spoofedSlot = 0

    private var beforeSlot = -1

    @EventTarget
    fun onAttack(event: AttackEvent) {
        attackEnemy = true
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet !is C02PacketUseEntity)
            return

        val thePlayer = mc.thePlayer ?: return

        val packet = event.packet

        if (packet.action == C02PacketUseEntity.Action.ATTACK && attackEnemy) {
            attackEnemy = false

            // Find the best weapon in hotbar (#Kotlin Style)
            var (slot, _) = (0..8)
                .map { Pair(it, thePlayer.inventory.getStackInSlot(it)) }
                .filter { it.second != null && (it.second?.item is ItemSword || it.second?.item is ItemTool) }
                .maxByOrNull {
                    it.second!!.attributeModifiers["generic.attackDamage"].first().amount + 1.25 * ItemUtils.getEnchantment(
                        it.second,
                        Enchantment.sharpness
                    )
                } ?: return

            if (swordsFirst.get()){
                val bestSword = (0..8)
                    .map { Pair(it, thePlayer.inventory.getStackInSlot(it)) }
                    .filter { it.second != null && it.second?.item is ItemSword }
                    .maxByOrNull {
                        it.second!!.attributeModifiers["generic.attackDamage"].first().amount + 1.25 * ItemUtils.getEnchantment(
                            it.second,
                            Enchantment.sharpness
                        )
                    }
                if (bestSword!=null){
                    slot = bestSword.first
                }
            }

            if (slot == thePlayer.inventory.currentItem) // If in hand no need to swap
                return

            // Switch to best weapon
            if (silentValue) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(slot))
                spoofedSlot = ticksValue.get()
            } else {
                beforeSlot = thePlayer.inventory.currentItem
                thePlayer.inventory.currentItem = slot
                if (switchBack.get()) spoofedSlot = ticksValue.get()
                mc.playerController.updateController()
            }

            // Resend attack packet
            mc.netHandler.addToSendQueue(packet)
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(update: UpdateEvent) {
        // Switch back to old item after some time
        if (spoofedSlot > 0) {
            if (spoofedSlot == 1)
                if (silentValue) {
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
                } else {
                    mc.thePlayer.inventory.currentItem = beforeSlot
                    mc.playerController.updateController()
                }
            spoofedSlot--
        }
    }
}