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

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kevin.main.KevinClient
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import org.apache.commons.io.IOUtils
import java.awt.image.BufferedImage
import java.io.*
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO

object ImageManager {
    var saveServerIcon = false
    private val jsonFile = File(KevinClient.fileManager.serverIconsDir,"config.json")

    fun load() {
        if (!jsonFile.exists()) return
        val json = JsonParser().parse(IOUtils.toString(FileInputStream(jsonFile),"utf-8")).asJsonObject
        if (json.has("state")){
            val state = json.get("state")
            saveServerIcon = state.asBoolean
        }
    }
    fun save(){
        val json = JsonObject()
        json.addProperty("state", saveServerIcon)
        val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(jsonFile), StandardCharsets.UTF_8))
        writer.write(FileManager.PRETTY_GSON.toJson(json))
        writer.close()
    }
    fun saveIcon(icon: BufferedImage,serverIP: String){
        try {
            val files = KevinClient.fileManager.serverIconsDir.listFiles()
            val ip = if (serverIP.contains(":")) serverIP.replace(":","_") else serverIP
            val name = "${files?.size} IP-$ip "
            val file = File(KevinClient.fileManager.serverIconsDir,"$name.png")
            var save = true
            if (!files.isNullOrEmpty()) for (f in files){
                if (!f.name.endsWith(".png")) continue
                val image = ImageIO.read(f)
                var same = true
                lx@ for (x in 0..63){
                    for (y in 0..63){
                        if (image.getRGB(x,y)!=icon.getRGB(x,y)) {
                            same = false
                            break@lx
                        }
                    }
                }
                if (same) {
                    save = false
                    break
                }
            }
            if (!file.exists()&&save){
                Minecraft.logger.info("Got the server icon.")
                ImageIO.write(icon,"png",file)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}