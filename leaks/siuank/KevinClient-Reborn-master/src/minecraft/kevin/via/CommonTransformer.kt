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

import com.viaversion.viaversion.util.PipelineUtil
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.MessageToByteEncoder
import io.netty.handler.codec.MessageToMessageDecoder
import java.lang.reflect.InvocationTargetException

object CommonTransformer {
    const val HANDLER_DECODER_NAME = "via-decoder"
    const val HANDLER_ENCODER_NAME = "via-encoder"
    @Throws(InvocationTargetException::class)
    fun decompress(ctx: ChannelHandlerContext, buf: ByteBuf) {
        val handler: ChannelHandler = ctx.pipeline().get("decompress")
        val decompressed: ByteBuf = if (handler is MessageToMessageDecoder<*>) PipelineUtil.callDecode(handler, ctx, buf)[0] as ByteBuf
        else PipelineUtil.callDecode(handler as ByteToMessageDecoder, ctx, buf)[0] as ByteBuf
        try {
            buf.clear().writeBytes(decompressed)
        } finally {
            decompressed.release()
        }
    }
    @Throws(Exception::class)
    fun compress(ctx: ChannelHandlerContext, buf: ByteBuf) {
        val compressed: ByteBuf = ctx.alloc().buffer()
        try {
            PipelineUtil.callEncode(ctx.pipeline().get("compress") as MessageToByteEncoder<*>, ctx, buf, compressed)
            buf.clear().writeBytes(compressed)
        } finally {
            compressed.release()
        }
    }
}