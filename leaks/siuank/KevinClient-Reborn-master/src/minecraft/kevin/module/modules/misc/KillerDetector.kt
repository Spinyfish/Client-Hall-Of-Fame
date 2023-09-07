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
package kevin.module.modules.misc

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.event.WorldEvent
import kevin.hud.element.elements.ConnectNotificationType
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.utils.ChatUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSword

class KillerDetector : Module("KillerDetector","Detect who has a sword in his hand.") {
    var killer: EntityPlayer? = null
    private val messageMode = ListValue("MessageMode", arrayOf("Notification","Chat"),"Chat")
    private val autoSay = BooleanValue("AutoSay",true)

    @EventTarget
    fun onWorld(event: WorldEvent){
        killer = null
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if (killer == null) for (e in mc.theWorld.loadedEntityList){
            if (e !is EntityPlayer||e == mc.thePlayer) continue
            if (e.inventory.getCurrentItem()?.item is ItemSword){
                killer = e
                when(messageMode.get()){
                    "Notification" -> KevinClient.hud.addNotification(Notification("Killer is ${e.name}!", "KillerDetector", ConnectNotificationType.Warn))
                    "Chat" -> ChatUtils.messageWithStart("[KillerDetector] §cKiller is ${e.name}!")
                }
                if (autoSay.get()) mc.thePlayer.sendChatMessage("is ${e.name}")
                break
            }
        }
    }
}