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
package kevin.file

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kevin.command.bind.BindCommand
import kevin.command.bind.BindCommandManager
import kevin.main.KevinClient
import kevin.module.Module
import kevin.module.Value
import kevin.utils.ChatUtils
import org.apache.commons.io.FileUtils
import java.io.*
import java.util.function.Consumer

object ConfigManager {
    val configList = arrayListOf<File>()
    val hudConfigList = arrayListOf<File>()
    private val configDir = KevinClient.fileManager.configsDir
    fun load(){
        configList.clear()
        hudConfigList.clear()
        val configs = configDir.listFiles()
        if (configs.isNullOrEmpty()) return
        for (c in configs){
            if (!c.isFile||!c.name.endsWith(".json")) continue
            if (c.name.endsWith("-HUD.json"))
                hudConfigList.add(c)
            else
                configList.add(c)
        }
    }
    fun saveConfig(name: String){
        val file = File(configDir,"$name.json")
        val hudFile = File(configDir,"$name-HUD.json")
        if (!file.exists()||!file.isFile) file.createNewFile()
        if (!hudFile.exists()||!hudFile.isFile) hudFile.createNewFile()
        val modulesConfig = JsonObject()
        //BindCommands
        val bindCommandJsonObject = JsonObject()
        BindCommandManager.bindCommandList.forEachIndexed { index, bindCommand ->
            val jsonMod = JsonObject()
            jsonMod.addProperty("key", bindCommand.key)
            jsonMod.addProperty("command", bindCommand.command)
            bindCommandJsonObject.add((index+1).toString(), jsonMod)
        }
        modulesConfig.add("BindCommand-List", bindCommandJsonObject)
        //Modules
        KevinClient.moduleManager.getModules().forEach {
            val jsonMod = JsonObject()
            jsonMod.addProperty("State", it.state)
            jsonMod.addProperty("KeyBind", it.keyBind)
            jsonMod.addProperty("Hide", !it.array)
            jsonMod.addProperty("AutoDisable", if (it.autoDisable.first) it.autoDisable.second else "Disable")
            it.values.forEach(Consumer { value: Value<*> -> jsonMod.add(value.name, value.toJson())})
            modulesConfig.add(it.name, jsonMod)
        }
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(modulesConfig))
        printWriter.close()
        //HUD
        val hudPrintWriter = PrintWriter(FileWriter(hudFile))
        hudPrintWriter.println(Config(KevinClient.hud).toJson())
        hudPrintWriter.close()
        //Add to config list
        if (!configList.contains(file)) configList.add(file)
        if (!hudConfigList.contains(hudFile)) hudConfigList.add(hudFile)
    }
    fun loadConfig(name: String):Int{
        val config = configList.filter { it.name=="$name.json" }
        val hudConfig = hudConfigList.filter { it.name=="$name-HUD.json" }//File(configDir,"$name-HUD.json")
        if (config.isEmpty()&&hudConfig.isEmpty()) return 3
        val configEx = config.isNotEmpty() && config[0].exists() && config[0].isFile
        val hudConfigEx = hudConfig.isNotEmpty() && hudConfig[0].exists() && hudConfig[0].isFile
        var returnValue = 0
        val warns = mutableMapOf<String,String>()
        val setModules = arrayListOf<Module>()
        var bindCommandEx = false
        BindCommandManager.bindCommandList.clear()
        //LoadModules
        if (configEx){
            val modulesConfig = config[0]
            val jsonElement = JsonParser().parse(BufferedReader(FileReader(modulesConfig)))
            if (jsonElement !is JsonNull) {
                val entryIterator: Iterator<Map.Entry<String, JsonElement>> =
                    jsonElement.asJsonObject.entrySet().iterator()
                while (entryIterator.hasNext()) {
                    val (key, value) = entryIterator.next()
                    //BindCommand
                    if (key == "BindCommand-List") {
                        bindCommandEx = true
                        val list = value.asJsonObject.entrySet().toMutableList()
                        list.sortBy { it.key.toInt() }
                        for (entry in list) {
                            val jsonModule = entry.value as JsonObject
                            BindCommandManager.bindCommandList.add(BindCommand(jsonModule["key"].asInt, jsonModule["command"].asString))
                        }
                        continue
                    }
                    //Modules
                    val module = KevinClient.moduleManager.getModuleByName(key)
                    if (module != null) {
                        setModules.add(module)
                        val jsonModule = value as JsonObject
                        module.state = jsonModule["State"].asBoolean
                        module.keyBind = jsonModule["KeyBind"].asInt
                        if (jsonModule["Hide"] != null)
                            module.array = !jsonModule["Hide"].asBoolean
                        else
                            warns["$key-HideValue"] = "The hide attribute of the module is not saved in the config file(OldConfig?)."
                        if (jsonModule["AutoDisable"] != null)
                            module.autoDisable = Pair(
                            jsonModule["AutoDisable"].asString != "Disable",
                            if (jsonModule["AutoDisable"].asString == "Disable") "" else jsonModule["AutoDisable"].asString
                        )
                        else
                            warns["$key-AutoDisableValue"] = "The AutoDisable attribute of the module is not saved in the config file(OldConfig?)."
                        for (moduleValue in module.values) {
                            val element = jsonModule[moduleValue.name]
                            if (element != null) moduleValue.fromJson(element) else warns["$key-${moduleValue.name}"] = "The config file does not have a value for this option."
                        }
                    } else warns[key] = "Module does not exist."
                }
            }
            if (!bindCommandEx) {
                warns["BindCommandManager"] = "Bind command list not exist."
            }
            KevinClient.moduleManager.getModules().forEach {
                if (it !in setModules) {
                    warns[it.name] = "The parameters for this module are not saved in the config file(OldConfig?)."
                }
            }
        } else returnValue = 1
        KevinClient.fileManager.saveConfig(KevinClient.fileManager.bindCommandConfig)
        if (warns.isNotEmpty()) {
            ChatUtils.messageWithStart("There were some warnings when loading the configuration:")
            warns.forEach { (t, u) ->
                ChatUtils.message("§7[§9$t§7]: §e$u")
            }
        }
        //LoadHUD
        if (hudConfigEx){
            val hudConfigFile = hudConfig[0]
            KevinClient.hud.clearElements()
            KevinClient.hud = Config(FileUtils.readFileToString(hudConfigFile)).toHUD()
        } else if (returnValue==1) returnValue = 3 else returnValue = 2
        return returnValue
    }
    fun deleteConfig(name: String): Boolean {
        val config = configList.filter { it.name=="$name.json" }
        val hudConfig = hudConfigList.filter { it.name=="$name-HUD.json" }
        val configEx = config.isNotEmpty() && config[0].isFile
        val hudConfigEx = hudConfig.isNotEmpty() && hudConfig[0].isFile
        return if (!configEx&&!hudConfigEx)
            false
        else {
            if (configEx) {
                config[0].delete()
                configList.remove(config[0])
            }
            if (hudConfigEx) {
                hudConfig[0].delete()
                hudConfigList.remove(hudConfig[0])
            }
            true
        }
    }
    //0:成功 1:源文件不存在 2:目标文件已存在
    fun renameConfig(from: String, to: String): Int {
        val fromFile = configList.find { it.name == "$from.json" }
        val fromHUDFile = hudConfigList.find { it.name == "$from-HUD.json" }
        val fromFileEx = fromFile != null && fromFile.exists() && fromFile.isFile
        val fromHUDFileEx = fromHUDFile != null && fromHUDFile.exists() && fromHUDFile.isFile
        if (!fromFileEx&&!fromHUDFileEx) return 1
        val toFile = File(configDir, "$to.json")
        val toHUDFile = File(configDir, "$to-HUD.json")
        val toFileEx = toFile.exists() && toFile.isFile
        val toHUDFileEx = toHUDFile.exists() && toHUDFile.isFile
        if ((toFileEx&&fromFileEx)||(toHUDFileEx&&fromHUDFileEx)) return 2
        if (fromFileEx) {
            configList.remove(fromFile)
            fromFile!!.renameTo(toFile)
            configList.add(toFile)
        }
        if (fromHUDFileEx) {
            hudConfigList.remove(fromHUDFile)
            fromHUDFile!!.renameTo(toHUDFile)
            hudConfigList.add(toHUDFile)
        }
        return 0
    }
    fun copyConfig(from: String, to: String): Int {
        val fromFile = configList.find { it.name == "$from.json" }
        val fromHUDFile = hudConfigList.find { it.name == "$from-HUD.json" }
        val fromFileEx = fromFile != null && fromFile.exists() && fromFile.isFile
        val fromHUDFileEx = fromHUDFile != null && fromHUDFile.exists() && fromHUDFile.isFile
        if (!fromFileEx&&!fromHUDFileEx) return 1
        val toFile = File(configDir, "$to.json")
        val toHUDFile = File(configDir, "$to-HUD.json")
        val toFileEx = toFile.exists() && toFile.isFile
        val toHUDFileEx = toHUDFile.exists() && toHUDFile.isFile
        if ((toFileEx&&fromFileEx)||(toHUDFileEx&&fromHUDFileEx)) return 2
        if (fromFileEx) {
            fromFile!!.copyTo(toFile)
            configList.add(toFile)
        }
        if (fromHUDFileEx) {
            fromHUDFile!!.copyTo(toHUDFile)
            hudConfigList.add(toHUDFile)
        }
        return 0
    }
}