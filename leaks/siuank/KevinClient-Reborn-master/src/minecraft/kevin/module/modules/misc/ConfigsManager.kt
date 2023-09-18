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

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kevin.command.bind.BindCommand
import kevin.command.bind.BindCommandManager
import kevin.file.ConfigManager
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.utils.ChatUtils
import kevin.utils.ServerUtils
import kevin.utils.proxy.ProxyManager
import net.minecraft.client.Minecraft
import java.net.Proxy

object ConfigsManager : Module("ConfigsManager", "Manage configs") { // good code? lol
    private var localConfigs: ListValue = ListValue("LocalConfigs", arrayOf(""), "")
    private var cloudConfigs: ListValue = ListValue("CloudConfigs", arrayOf(""), "")

    private val loadLocal: BooleanValue = object : BooleanValue("LoadLocalConfig", false) {
        override fun onChanged(oldValue: Boolean, newValue: Boolean) {
            if (newValue) {
                set(false)
                loadLocal()
            }
        }
    }
    private val loadCloud: BooleanValue = object : BooleanValue("LoadCloudConfig", false) {
        override fun onChanged(oldValue: Boolean, newValue: Boolean) {
            if (newValue) {
                set(false)
                loadCloud(cloudConfigs.get())
            }
        }
    }
    private val loadWithProxy = BooleanValue("WithProxy", false)
    private val PreferredAPI = ListValue("PreferredAPI", arrayOf("https://raw.githubusercontent.com/", "https://raw.fastgit.org/"), "https://raw.fastgit.org/")
    private val refresh: BooleanValue = object : BooleanValue("Refresh", false) {
        override fun onChanged(oldValue: Boolean, newValue: Boolean) {
            if (newValue) {
                set(false)
                updateValue()
            }
        }
    }

    private val apiFirst: String
    get() = PreferredAPI.get()

    private val apiSecond: String
    get() = "https://raw.githubusercontent.com/https://raw.fastgit.org/".replace(PreferredAPI.get(), "")

    private val proxy: Proxy?
        get() = if (loadWithProxy.get()) ProxyManager.proxyInstance else null

    fun loadLocal() {
        try {
            KevinClient.moduleManager.getModules().forEach { it.state = false }
            val name = localConfigs.get()
            when (ConfigManager.loadConfig(name)) {
            0 -> {
                ChatUtils.messageWithStart("§aSuccessfully loaded config §b$name.")
            }
            1 -> {
                ChatUtils.messageWithStart("§eWarning: §eThe §eModules §econfig §efile §eis §emissing.§eSuccessfully §eloaded §eHUD §econfig §b$name.")
            }
            2 -> {
                ChatUtils.messageWithStart("§eWarning: §eThe §eHUD §econfig §efile §eis §emissing.§eSuccessfully §eloaded §eModules §econfig §b$name.")
            }
            else -> {
                ChatUtils.messageWithStart("§cFailed to load config §b$name. §cFile not found.")
            }
        }
        } catch (_: Exception) { }
    }

    fun loadCloud(name: String) {
        var res = ServerUtils.sendGet("${apiFirst}siuank/KevinClient-Reborn/master/cfg/$name.json", proxy)
        if (res.second > 0) {
            res = ServerUtils.sendGet("${apiSecond}siuank/KevinClient-Reborn/master/cfg/$name.json", proxy)
        }
        if (res.second > 0) {
            ChatUtils.messageWithStart("§cFailed to load config §b${name}.§cFile not found.")
            return
        }
        val jsonElement = JsonParser().parse(res.first)
        val setModules = arrayListOf<Module>()
        val warns = mutableMapOf<String,String>()
        if (jsonElement !is JsonNull) {
            KevinClient.moduleManager.getModules().forEach { it.state = false }
            val entryIterator: Iterator<Map.Entry<String, JsonElement>> =
                jsonElement.asJsonObject.entrySet().iterator()
            while (entryIterator.hasNext()) {
                val (key, value) = entryIterator.next()
                //BindCommand
                if (key == "BindCommand-List") {
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
                        warns["$key-HideValue"] = "The hide attribute of the module is not saved in the config file(old config?)."
                    if (jsonModule["AutoDisable"] != null)
                        module.autoDisable = Pair(
                            jsonModule["AutoDisable"].asString != "Disable",
                            if (jsonModule["AutoDisable"].asString == "Disable") "" else jsonModule["AutoDisable"].asString
                        )
                    else
                        warns["$key-AutoDisableValue"] = "The AutoDisable attribute of the module is not saved in the config file(old config?)."
                    for (moduleValue in module.values) {
                        val element = jsonModule[moduleValue.name]
                        if (element != null) moduleValue.fromJson(element) else warns["$key-${moduleValue.name}"] = "The config file does not have a value for this option."
                    }
                } else warns[key] = "Module does not exist."
                KevinClient.fileManager.saveConfig(KevinClient.fileManager.bindCommandConfig)
            }
            if (warns.isNotEmpty()) {
                ChatUtils.messageWithStart("There were some warnings when loading the configuration:")
                warns.forEach { (t, u) ->
                    ChatUtils.message("§7[§9$t§7]: §e$u")
                }
            }
        }
    }

    fun updateValue() {
        try {
            val configFileList = ConfigManager.configList
            val arrayList = ArrayList<String>()
            for (file in configFileList) {
                arrayList.add(file.name.removeSuffix(".json"))
            }
            if (arrayList.isNotEmpty())
                localConfigs = ListValue("LocalConfigs", arrayList.toTypedArray(), arrayList[0])
        } catch (e: Exception) {
            logError(e)
        }
        Thread {
            try {
                val resStrArray: Array<String>
                var res = ServerUtils.sendGet("${apiFirst}siuank/KevinClient-Reborn/master/cfg/configs.bb", proxy)
                if (res.second > 0) {
                    res = ServerUtils.sendGet("${apiSecond}siuank/KevinClient-Reborn/master/cfg/configs.bb", proxy)
                }
                if (res.second == 0) {
                    resStrArray = res.first.split("\n").toTypedArray()
                    cloudConfigs = ListValue("CloudConfigs", resStrArray, resStrArray[0])
                } else {
                    KevinClient.hud.addNotification(Notification("Cannot load ConfigList from cloud: Exception found when ${if (res.second == 1) "Get" else if (res.second == 2) "Close connection" else "Connect"}"))
                }
            } catch (e: Exception) {
                logError(e)
            }
        }.start()
    }

    override fun onEnable() {
        state = false
    }

    fun logError(e: Throwable) {
        Minecraft.logger.warn("Error caught in ConfigsManager: ${e.stackTraceToString()}")
    }
}