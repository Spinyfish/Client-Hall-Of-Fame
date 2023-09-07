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
package kevin.utils

import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.BlockPos
import net.minecraft.world.chunk.Chunk
import java.util.concurrent.atomic.AtomicBoolean

object MiniMapRegister : MinecraftInstance() {
    private val chunkTextureMap = HashMap<ChunkLocation, MiniMapTexture>()
    private val queuedChunkUpdates = HashSet<Chunk>(256)
    private val queuedChunkDeletions = HashSet<ChunkLocation>(256)
    private val deleteAllChunks = AtomicBoolean(false)
    // 不知道杰哥有没有想过：如果玩家不用Radar,那么储存在queuedChunkUpdates的区块可能会导致OutOfMemory
    var radarEnabled = false

    fun updateChunk(chunk: Chunk) {
        if (!radarEnabled) return
        synchronized(queuedChunkUpdates) {
            queuedChunkUpdates.add(chunk)
        }
    }

    fun getChunkTextureAt(x: Int, z: Int): MiniMapTexture? {
        return chunkTextureMap[ChunkLocation(x, z)]
    }

    fun updateChunks() {
        synchronized(queuedChunkUpdates) {
            if (deleteAllChunks.get()) {
                synchronized(queuedChunkDeletions) {
                    queuedChunkDeletions.clear()
                }
                queuedChunkUpdates.clear()

                chunkTextureMap.forEach { it.value.delete() }

                chunkTextureMap.clear()

                deleteAllChunks.set(false)
            } else {
                synchronized(queuedChunkDeletions) {
                    queuedChunkDeletions.forEach {
                        chunkTextureMap.remove(it)?.delete()
                    }
                    queuedChunkDeletions.clear()
                }
            }

            queuedChunkUpdates.forEach {
                chunkTextureMap.computeIfAbsent(ChunkLocation(it.xPosition, it.zPosition)) {
                    MiniMapTexture()
                }.updateChunkData(it)
            }

            queuedChunkUpdates.clear()
        }
    }

    fun getLoadedChunkCount(): Int {
        return chunkTextureMap.size
    }

    fun unloadChunk(x: Int, z: Int) {
        synchronized(queuedChunkDeletions) {
            queuedChunkDeletions.add(ChunkLocation(x, z))
        }
    }

    fun unloadAllChunks() {
        deleteAllChunks.set(true)
        if (!radarEnabled) {
            synchronized(queuedChunkDeletions) {
                queuedChunkDeletions.clear()
            }
            synchronized(queuedChunkUpdates) {
                queuedChunkUpdates.clear()
            }
            chunkTextureMap.forEach { it.value.delete() }
            chunkTextureMap.clear()
        }
        radarEnabled = false
    }

    class MiniMapTexture {
        val texture = DynamicTexture(16, 16)
        var deleted = false

        fun updateChunkData(chunk: Chunk) {
            val rgbValues = texture.textureData

            for (x in 0..15) {
                for (z in 0..15) {
                    val bp = BlockPos(x, chunk.getHeightValue(x, z) - 1, z)
                    val blockState = chunk.getBlockState(bp)

                    rgbValues[rgbValues.size - (z * 16 + x + 1)] = blockState.block.getMapColor(blockState).colorValue or (0xFF shl 24)
                }
            }

            texture.updateDynamicTexture()
        }

        internal fun delete() {
            if (!deleted) {
                texture.deleteGlTexture()
                deleted = true
            }
        }

        protected fun finalize() {
            // We don't need to set deleted to true since the object is deleted after this method call
            if (!deleted)
                texture.deleteGlTexture()
        }
    }

    data class ChunkLocation(val x: Int, val z: Int)
}