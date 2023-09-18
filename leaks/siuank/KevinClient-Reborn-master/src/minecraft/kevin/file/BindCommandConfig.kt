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

import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kevin.command.bind.BindCommand
import kevin.command.bind.BindCommandManager
import java.io.*

class BindCommandConfig(file: File) : FileConfig(file) {
    override fun loadConfig() {
        BindCommandManager.bindCommandList.clear()
        val jsonElement = JsonParser().parse(BufferedReader(FileReader(file)))
        if (jsonElement is JsonNull) return
        val list = jsonElement.asJsonObject.entrySet().toMutableList()
        list.sortBy { it.key.toInt() }
        for (entry in list) {
            val jsonModule = entry.value as JsonObject
            BindCommandManager.bindCommandList.add(BindCommand(jsonModule["key"].asInt, jsonModule["command"].asString))
        }
    }
    override fun saveConfig() {
        val jsonObject = JsonObject()

        BindCommandManager.bindCommandList.forEachIndexed { index, it ->
            val jsonMod = JsonObject()
            jsonMod.addProperty("key", it.key)
            jsonMod.addProperty("command", it.command)
            jsonObject.add((index+1).toString(), jsonMod)
        }

        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject))
        printWriter.close()
    }
}