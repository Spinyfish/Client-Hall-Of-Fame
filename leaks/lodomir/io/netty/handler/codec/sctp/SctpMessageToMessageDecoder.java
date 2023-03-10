/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.sctp;

import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.sctp.SctpMessageCompletionHandler;

public abstract class SctpMessageToMessageDecoder
extends MessageToMessageDecoder<SctpMessage> {
    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        if (msg instanceof SctpMessage) {
            SctpMessage sctpMsg = (SctpMessage)msg;
            if (sctpMsg.isComplete()) {
                return true;
            }
            throw new CodecException(String.format("Received SctpMessage is not complete, please add %s in the pipeline before this handler", SctpMessageCompletionHandler.class.getSimpleName()));
        }
        return false;
    }
}

