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
package kevin.script

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.module.modules.render.ClickGui
import net.minecraft.client.Minecraft

object ScriptManager : ICommand {
    private val scripts = arrayListOf<Script>()
    override fun run(args: Array<out String>?) {
        load()
    }
    fun load(){
        val dir = KevinClient.fileManager.scripts
        if (!dir.exists()) return
        val files = dir.listFiles() ?: return
        if (files.isEmpty()) {
            Minecraft.logger.info("[ScriptManager] There is no script to load")
            return
        }
        Minecraft.logger.info("[ScriptManager] Loading scripts...")
        val time = System.currentTimeMillis()
        //KevinClient.fileManager.saveConfig(KevinClient.fileManager.modulesConfig)
        scripts.forEach { script ->
            script.registeredModules.forEach {
                if (it in KevinClient.moduleManager.getModules()) KevinClient.moduleManager.unregisterModule(it)
            }
        }
        scripts.clear()
        files.forEach {
            try {
                val script = Script(it)
                script.initScript()
                scripts += script
            } catch (e: Throwable){
                Minecraft.logger.error("[ScriptManager] Error loading script ${it.name}!",e)
            }
        }
        KevinClient.fileManager.loadConfig(KevinClient.fileManager.modulesConfig)
        Minecraft.logger.info("[ScriptManager] Loaded ${scripts.size} script(s),${System.currentTimeMillis()-time}ms.")
        KevinClient.clickGUI = ClickGui.ClickGUI()
        KevinClient.newClickGui = ClickGui.NewClickGui()
        Minecraft.logger.info("[ScriptManager] Reloaded ClickGui.")
    }

    fun reAdd() {
        val dir = KevinClient.fileManager.scripts
        if (!dir.exists()) return
        val files = dir.listFiles() ?: return
        Minecraft.logger.info("[ScriptManager] Re add scripts...")
        val time = System.currentTimeMillis()
        scripts.clear()
        files.forEach {
            try {
                val script = Script(it)
                script.initScript()
                scripts += script
            } catch (e: Throwable){
                Minecraft.logger.error("[ScriptManager] Error loading script ${it.name}!",e)
            }
        }
        Minecraft.logger.info("[ScriptManager] Re add ${scripts.size} script(s),${System.currentTimeMillis()-time}ms.")
        KevinClient.clickGUI = ClickGui.ClickGUI()
        KevinClient.newClickGui = ClickGui.NewClickGui()
        Minecraft.logger.info("[ScriptManager] Reloaded ClickGui.")
    }
}