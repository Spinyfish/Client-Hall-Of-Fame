/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelPipeline
 */
package viamcp.utils;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

public class NettyUtil {
    public static ChannelPipeline decodeEncodePlacement(ChannelPipeline instance, String base, String newHandler, ChannelHandler handler) {
        switch (base) {
            case "decoder": {
                if (instance.get("via-decoder") == null) break;
                base = "via-decoder";
                break;
            }
            case "encoder": {
                if (instance.get("via-encoder") == null) break;
                base = "via-encoder";
            }
        }
        return instance.addBefore(base, newHandler, handler);
    }
}

