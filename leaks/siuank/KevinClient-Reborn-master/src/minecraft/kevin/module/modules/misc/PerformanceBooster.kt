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

import kevin.module.BooleanValue
import kevin.module.Module
import kevin.utils.LogUtils.DebugLogger
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockModelShapes
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.resources.model.IBakedModel
import net.minecraft.client.resources.model.ModelManager
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.util.EnumFacing
import net.minecraft.util.RegistrySimple
import java.util.*


object PerformanceBooster : Module("PerformanceBooster", "Optimize to improve performance.") {
    private val staticParticleColorValue = BooleanValue("StaticParticleColor", false)
    private val fastEntityLightningValue = BooleanValue("FastEntityLightning", false)
    private val fastBlockLightningValue = BooleanValue("FastBlockLightning", false)
    val staticParticleColor
        get() = this.state && staticParticleColorValue.get()
    val fastEntityLightning
        get() = this.state && fastEntityLightningValue.get()
    val fastBlockLightning
        get() = this.state && fastBlockLightningValue.get()


    /**
     * By FoamFix
     */
    val DUMMY_MODEL: IBakedModel = object : IBakedModel {
        override fun getFaceQuads(facing: EnumFacing): List<BakedQuad> {
            return Collections.emptyList()
        }

        override fun getGeneralQuads(): List<BakedQuad> {
            return Collections.emptyList()
        }

        override fun isAmbientOcclusion(): Boolean {
            return false
        }

        override fun isGui3d(): Boolean {
            return false
        }

        override fun isBuiltInRenderer(): Boolean {
            return false
        }

        override fun getParticleTexture(): TextureAtlasSprite {
            return Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(TextureMap.LOCATION_MISSING_TEXTURE.toString())
        }

        override fun getItemCameraTransforms(): ItemCameraTransforms {
            return ItemCameraTransforms.DEFAULT
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun postTextureStitch() {
        val debug = DebugLogger("PerformanceBooster")
        debug.step() // 1
        val brd = mc.blockRendererDispatcher?: return
        debug.step() // 2
        val bms = brd.blockModelShapes!!
        debug.step() // 3
        val mgr = bms.modelManager!!
        debug.step() // 4
        try {
            debug.child("ClearModelRegistry")
            // kt的class玩不起来，这是不好的
            val fieldModelRegistry = ModelManager::class.java.getDeclaredField("modelRegistry")
            debug.step() // 5
            val accessible = fieldModelRegistry.isAccessible
            debug.step() // 6
            fieldModelRegistry.isAccessible = true
            debug.step() // 7
            val registry = fieldModelRegistry.get(mgr) as RegistrySimple<ModelResourceLocation, IBakedModel>
            debug.step() // 8
            Minecraft.logger.info("Clearing unnecessary model registry(${registry.keys.size})...")
            debug.step() // 9
            for (key in registry.keys) {
                registry.putObject(key, DUMMY_MODEL)
            }
            debug.step()
            fieldModelRegistry.isAccessible = accessible
            debug.step()
        } catch (e: Exception) { Minecraft.logger.warn("Error when clearing unnecessary model registry: \n${e.stackTraceToString()}") }
        debug.shrinkChild()
        try {
            debug.child("ClearModelStore")
            debug.step()
            val fieldBakedModelStore = BlockModelShapes::class.java.getDeclaredField("bakedModelStore")
            debug.step()
            val accessible = fieldBakedModelStore.isAccessible
            debug.step()
            fieldBakedModelStore.isAccessible = true
            debug.step()
            val bakedModelStore = fieldBakedModelStore.get(bms) as MutableMap<IBlockState, IBakedModel>
            debug.step()
            Minecraft.logger.info("Clearing unnecessary model store(${bakedModelStore.size})...")
            debug.step()
            bakedModelStore.clear()
            debug.step()
            fieldBakedModelStore.isAccessible = accessible
            debug.step()
        } catch (e: Exception) { Minecraft.logger.warn("Error when clearing unnecessary model store: \n${e.stackTraceToString()}") }
        debug.close()
    }
}