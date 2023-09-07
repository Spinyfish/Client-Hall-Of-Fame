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
package kevin.utils

import kevin.main.KevinClient
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

object ChatUtils {
    fun message(message: String){
        Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(ChatComponentText(message))
    }
    fun messageWithStart(message: String){
        Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(ChatComponentText("${KevinClient.cStart} $message"))
    }
}