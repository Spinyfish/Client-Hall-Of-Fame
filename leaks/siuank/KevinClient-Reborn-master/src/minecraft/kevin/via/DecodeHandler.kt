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
package kevin.via

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.exception.CancelCodecException
import com.viaversion.viaversion.exception.CancelDecoderException
import com.viaversion.viaversion.util.PipelineUtil
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import kevin.via.CommonTransformer.compress
import kevin.via.CommonTransformer.decompress
import java.lang.reflect.InvocationTargetException

@ChannelHandler.Sharable
class DecodeHandler(val info: UserConnection) : MessageToMessageDecoder<ByteBuf?>() {
    private var handledCompression = false
    private var skipDoubleTransform = false

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext?, p1: ByteBuf?, out: MutableList<Any>?) {
        if (skipDoubleTransform) {
            skipDoubleTransform = false
            out!!.add(p1!!.retain())
            return
        }
        if (!info.checkIncomingPacket()) throw CancelDecoderException.generate(null)
        if (!info.shouldTransformPacket()) {
            out!!.add(p1!!.retain())
            return
        }
        val transformedBuf: ByteBuf = ctx!!.alloc().buffer().writeBytes(p1!!)
        try {
            val needsCompress = handleCompressionOrder(ctx, transformedBuf)
            info.transformIncoming(
                transformedBuf
            ) { cause: Throwable? ->
                CancelDecoderException.generate(
                    cause
                )
            }
            if (needsCompress) {
                compress(ctx, transformedBuf)
                skipDoubleTransform = true
            }
            out!!.add(transformedBuf.retain())
        } finally {
            transformedBuf.release()
        }
    }

    @Throws(InvocationTargetException::class)
    private fun handleCompressionOrder(ctx: ChannelHandlerContext, buf: ByteBuf): Boolean {
        if (handledCompression) return false
        val decoderIndex: Int = ctx.pipeline().names().indexOf("decompress")
        if (decoderIndex == -1) return false
        handledCompression = true
        if (decoderIndex > ctx.pipeline().names().indexOf("via-decoder")) {
            // Need to decompress this packet due to bad order
            decompress(ctx, buf)
            val encoder: ChannelHandler = ctx.pipeline().get("via-encoder")
            val decoder: ChannelHandler = ctx.pipeline().get("via-decoder")
            ctx.pipeline().remove(encoder)
            ctx.pipeline().remove(decoder)
            ctx.pipeline().addAfter("compress", "via-encoder", encoder)
            ctx.pipeline().addAfter("decompress", "via-decoder", decoder)
            return true
        }
        return false
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        if (PipelineUtil.containsCause(cause, CancelCodecException::class.java)) return
        super.exceptionCaught(ctx, cause)
    }
}