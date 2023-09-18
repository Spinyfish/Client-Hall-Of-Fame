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
package kevin.command.commands

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

class BindCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args == null || args.isEmpty() || args.size == 1) {
            ChatUtils.messageWithStart("§cUsage: .bind <ModuleName> <Key/None>")
            return
        }
        val module = KevinClient.moduleManager.getModuleByName(args[0])
        if (module == null) {
            ChatUtils.messageWithStart("§9Module §c§l" + args[0] + "§9 not found.")
            return
        }
        val key = Keyboard.getKeyIndex(args[1].uppercase())
        module.keyBind = key
        ChatUtils.messageWithStart("§9Bound module §b§l${module.name}§9 to key §a§l${Keyboard.getKeyName(key)}§3.")
        Minecraft.getMinecraft().soundHandler.playSound(
            PositionedSoundRecord.create(
            ResourceLocation("random.anvil_use"),
                1F
        ))
        return
    }
}