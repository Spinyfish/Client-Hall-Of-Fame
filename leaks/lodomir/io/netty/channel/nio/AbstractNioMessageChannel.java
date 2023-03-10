/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.AbstractNioChannel;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNioMessageChannel
extends AbstractNioChannel {
    protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super(parent, ch, readInterestOp);
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new NioMessageUnsafe();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        SelectionKey key = this.selectionKey();
        int interestOps = key.interestOps();
        while (true) {
            Object msg;
            if ((msg = in.current()) == null) {
                if ((interestOps & 4) == 0) break;
                key.interestOps(interestOps & 0xFFFFFFFB);
                break;
            }
            try {
                boolean done = false;
                for (int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
                    if (!this.doWriteMessage(msg, in)) continue;
                    done = true;
                    break;
                }
                if (done) {
                    in.remove();
                    continue;
                }
                if ((interestOps & 4) != 0) break;
                key.interestOps(interestOps | 4);
            }
            catch (IOException e) {
                if (this.continueOnWriteError()) {
                    in.remove(e);
                    continue;
                }
                throw e;
            }
            break;
        }
    }

    protected boolean continueOnWriteError() {
        return false;
    }

    protected abstract int doReadMessages(List<Object> var1) throws Exception;

    protected abstract boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception;

    private final class NioMessageUnsafe
    extends AbstractNioChannel.AbstractNioUnsafe {
        private final List<Object> readBuf = new ArrayList<Object>();

        private NioMessageUnsafe() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void read() {
            assert (AbstractNioMessageChannel.this.eventLoop().inEventLoop());
            ChannelConfig config = AbstractNioMessageChannel.this.config();
            if (!config.isAutoRead() && !AbstractNioMessageChannel.this.isReadPending()) {
                this.removeReadOp();
                return;
            }
            int maxMessagesPerRead = config.getMaxMessagesPerRead();
            ChannelPipeline pipeline = AbstractNioMessageChannel.this.pipeline();
            boolean closed = false;
            Throwable exception = null;
            try {
                try {
                    int localRead;
                    while ((localRead = AbstractNioMessageChannel.this.doReadMessages(this.readBuf)) != 0) {
                        if (localRead < 0) {
                            closed = true;
                        } else if (config.isAutoRead() && this.readBuf.size() < maxMessagesPerRead) continue;
                        break;
                    }
                }
                catch (Throwable t) {
                    exception = t;
                }
                AbstractNioMessageChannel.this.setReadPending(false);
                int size = this.readBuf.size();
                for (int i = 0; i < size; ++i) {
                    pipeline.fireChannelRead(this.readBuf.get(i));
                }
                this.readBuf.clear();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    if (exception instanceof IOException) {
                        closed = !(AbstractNioMessageChannel.this instanceof ServerChannel);
                    }
                    pipeline.fireExceptionCaught(exception);
                }
                if (closed && AbstractNioMessageChannel.this.isOpen()) {
                    this.close(this.voidPromise());
                }
            }
            finally {
                if (!config.isAutoRead() && !AbstractNioMessageChannel.this.isReadPending()) {
                    this.removeReadOp();
                }
            }
        }
    }
}

