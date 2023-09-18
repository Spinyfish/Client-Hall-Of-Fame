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

import org.lwjgl.opengl.GL20
import java.io.Closeable

class RainbowShader : FontManager.Shader("rainbow_shader.frag"), Closeable {
    var isInUse = false
        private set

    var strengthX = 0f
    var strengthY = 0f
    var offset = 0f

    override fun setupUniforms() {
        setupUniform("offset")
        setupUniform("strength")
    }

    override fun updateUniforms() {
        GL20.glUniform2f(getUniform("strength"), strengthX, strengthY)
        GL20.glUniform1f(getUniform("offset"), offset)
    }

    override fun startShader() {
        super.startShader()

        isInUse = true
    }

    override fun stopShader() {
        super.stopShader()

        isInUse = false
    }

    override fun close() {
        if (isInUse)
            stopShader()
    }

    companion object {
        @JvmField
        val INSTANCE = RainbowShader()

        @Suppress("NOTHING_TO_INLINE")
        inline fun begin(enable: Boolean, x: Float, y: Float, offset: Float): RainbowShader {
            val instance = INSTANCE

            if (enable) {
                instance.strengthX = x
                instance.strengthY = y
                instance.offset = offset

                instance.startShader()
            }

            return instance
        }
    }
}