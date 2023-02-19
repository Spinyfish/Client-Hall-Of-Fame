package alos.stella.file.configs

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import alos.stella.Stella
import alos.stella.command.Command
import alos.stella.command.shortcuts.Shortcut
import alos.stella.file.FileConfig
import alos.stella.file.FileManager
import java.io.File
import java.io.IOException

class ShortcutsConfig(file: File) : FileConfig(file) {

    /**
     * Load config from file
     *
     * @throws IOException
     */
    override fun loadConfig() {
        val jsonElement = JsonParser().parse(file.readText())

        if (jsonElement !is JsonArray)
            return

        for (shortcutJson in jsonElement) {
            if (shortcutJson !is JsonObject)
                continue

            val name = shortcutJson.get("name")?.asString ?: continue
            val scriptJson = shortcutJson.get("script")?.asJsonArray ?: continue

            val script = mutableListOf<Pair<Command, Array<String>>>()

            for (scriptCommand in scriptJson) {
                if (scriptCommand !is JsonObject)
                    continue

                val commandName = scriptCommand.get("name")?.asString ?: continue
                val arguments = scriptCommand.get("arguments")?.asJsonArray ?: continue

                val command = Stella.commandManager.getCommand(commandName) ?: continue

                script.add(Pair(command, arguments.map { it.asString }.toTypedArray()))
            }

            Stella.commandManager.registerCommand(Shortcut(name, script))
        }
    }

    /**
     * Save config to file
     *
     * @throws IOException
     */
    override fun saveConfig() {
        val jsonArray = JsonArray()

        for (command in Stella.commandManager.commands) {
            if (command !is Shortcut)
                continue

            val jsonCommand = JsonObject()
            jsonCommand.addProperty("name", command.command)

            val scriptArray = JsonArray()

            for (pair in command.script) {
                val pairObject = JsonObject()

                pairObject.addProperty("name", pair.first.command)

                val argumentsObject = JsonArray()
                for (argument in pair.second) {
                    // argumentsObject.add(argument)
                }

                pairObject.add("arguments", argumentsObject)

                scriptArray.add(pairObject)
            }

            jsonCommand.add("script", scriptArray)

            jsonArray.add(jsonCommand)
        }

        file.writeText(FileManager.PRETTY_GSON.toJson(jsonArray))
    }

}