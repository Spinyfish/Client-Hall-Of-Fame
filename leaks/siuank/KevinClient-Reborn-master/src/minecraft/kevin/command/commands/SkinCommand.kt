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
import kevin.skin.SkinManager
import kevin.utils.ChatUtils

class SkinCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args==null) {
            usageMessage()
            return
        }
        when(args.size){
            0 -> {
                usageMessage()
            }
            1 -> {
                val v = args[0]
                when{
                    v.equals("Set",true) -> {
                        ChatUtils.messageWithStart("§cUsage: §9.§6skin §b$v §c<SkinName/None>")
                    }
                    v.equals("Mode",true) -> {
                        ChatUtils.messageWithStart("§cUsage: §9.§6skin §b$v §c<Default/Slim>")
                    }
                    v.equals("List",true) -> {
                        SkinManager.list()
                    }
                    v.equals("Clear",true) -> {
                        clearSkin()
                    }
                    v.equals("Reload",true) -> {
                        SkinManager.load()
                        ChatUtils.messageWithStart("§9Reloaded")
                    }
                    else -> {
                        usageMessage()
                    }
                }
            }
            else -> {
                val v = args[0]
                val list = args.toMutableList()
                list.removeFirst()
                var v2 = ""
                var c = 0
                list.forEach {
                    c += 1
                    v2 += if (c==list.size){
                        it
                    } else {
                        "$it "
                    }
                }
                when{
                    v.equals("Set",true) -> {
                        if (v2.equals("None",true)) {
                            clearSkin()
                            return
                        }
                        val state = SkinManager.set(v2)
                        if (state==0){
                            ChatUtils.messageWithStart("§9Skin was set to §b$v2.")
                        } else {
                            ChatUtils.messageWithStart("§cNo skin called §b$v2.")
                        }
                    }
                    v.equals("Mode",true) -> {
                        when{
                            v2.equals("Default",true) -> {
                                SkinManager.setMode(SkinManager.SkinMode.Default)
                                ChatUtils.messageWithStart("§9Skin mode was set to §b$v2.")
                            }
                            v2.equals("Slim",true) -> {
                                SkinManager.setMode(SkinManager.SkinMode.Slim)
                                ChatUtils.messageWithStart("§9Skin mode was set to §b$v2.")
                            }
                            else -> {
                                ChatUtils.messageWithStart("§cUsage: §9.§6skin §b$v §c<Default/Slim>")
                            }
                        }
                    }
                    v.equals("Clear",true) -> {
                        clearSkin()
                    }
                    v.equals("List",true) -> {
                        SkinManager.list()
                    }
                    v.equals("Reload",true) -> {
                        SkinManager.load()
                        ChatUtils.messageWithStart("§9Reloaded")
                    }
                    else -> {
                        usageMessage()
                    }
                }
            }
        }
    }
    private fun clearSkin(){
        SkinManager.nowSkin = null
        SkinManager.save()
        ChatUtils.messageWithStart("§9Skin was set to §bNone.")
    }
    private fun usageMessage() = ChatUtils.messageWithStart("§cUsage: .skin <Set/Clear/List/Reload/Mode>")
}