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

class FontCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args.isNullOrEmpty()) {
            ChatUtils.messageWithStart("§cUsage: .font/fonts <reload/list>")
            return
        }
        val c = args[0]
        when {
            c.equals("list", true) -> listFonts()
            c.equals("reload", true) -> reloadFonts()
            else -> ChatUtils.messageWithStart("§cUsage: .font/fonts <reload/list>")
        }
    }
    private fun listFonts() {
        ChatUtils.messageWithStart("§b<Fonts>")
        KevinClient.fontManager.getFonts()
            .map { KevinClient.fontManager.getFontDetails(it) }
            .forEachIndexed { index, fontInfo ->
                if (fontInfo != null) {
                    ChatUtils.messageWithStart("§c${index+1}.§7Name:§a${fontInfo.name}§7Size:§9${fontInfo.fontSize}")
                }
            }
    }
    private fun reloadFonts() {
        ChatUtils.messageWithStart("§bReloading Fonts...")
        val l = System.currentTimeMillis()
        KevinClient.fontManager.reloadFonts()
        ChatUtils.messageWithStart("§aSuccessfully reload fonts,${System.currentTimeMillis()-l}ms.")
    }
}