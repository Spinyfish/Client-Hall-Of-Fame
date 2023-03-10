/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.util.internal.PlatformDependent;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

final class SpdySession {
    private final AtomicInteger activeLocalStreams = new AtomicInteger();
    private final AtomicInteger activeRemoteStreams = new AtomicInteger();
    private final Map<Integer, StreamState> activeStreams = PlatformDependent.newConcurrentHashMap();
    private final StreamComparator streamComparator = new StreamComparator();
    private final AtomicInteger sendWindowSize;
    private final AtomicInteger receiveWindowSize;

    SpdySession(int sendWindowSize, int receiveWindowSize) {
        this.sendWindowSize = new AtomicInteger(sendWindowSize);
        this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
    }

    int numActiveStreams(boolean remote) {
        if (remote) {
            return this.activeRemoteStreams.get();
        }
        return this.activeLocalStreams.get();
    }

    boolean noActiveStreams() {
        return this.activeStreams.isEmpty();
    }

    boolean isActiveStream(int streamId) {
        return this.activeStreams.containsKey(streamId);
    }

    Map<Integer, StreamState> activeStreams() {
        TreeMap<Integer, StreamState> streams = new TreeMap<Integer, StreamState>(this.streamComparator);
        streams.putAll(this.activeStreams);
        return streams;
    }

    void acceptStream(int streamId, byte priority, boolean remoteSideClosed, boolean localSideClosed, int sendWindowSize, int receiveWindowSize, boolean remote) {
        StreamState state;
        if (!(remoteSideClosed && localSideClosed || (state = this.activeStreams.put(streamId, new StreamState(priority, remoteSideClosed, localSideClosed, sendWindowSize, receiveWindowSize))) != null)) {
            if (remote) {
                this.activeRemoteStreams.incrementAndGet();
            } else {
                this.activeLocalStreams.incrementAndGet();
            }
        }
    }

    private StreamState removeActiveStream(int streamId, boolean remote) {
        StreamState state = this.activeStreams.remove(streamId);
        if (state != null) {
            if (remote) {
                this.activeRemoteStreams.decrementAndGet();
            } else {
                this.activeLocalStreams.decrementAndGet();
            }
        }
        return state;
    }

    void removeStream(int streamId, Throwable cause, boolean remote) {
        StreamState state = this.removeActiveStream(streamId, remote);
        if (state != null) {
            state.clearPendingWrites(cause);
        }
    }

    boolean isRemoteSideClosed(int streamId) {
        StreamState state = this.activeStreams.get(streamId);
        return state == null || state.isRemoteSideClosed();
    }

    void closeRemoteSide(int streamId, boolean remote) {
        StreamState state = this.activeStreams.get(streamId);
        if (state != null) {
            state.closeRemoteSide();
            if (state.isLocalSideClosed()) {
                this.removeActiveStream(streamId, remote);
            }
        }
    }

    boolean isLocalSideClosed(int streamId) {
        StreamState state = this.activeStreams.get(streamId);
        return state == null || state.isLocalSideClosed();
    }

    void closeLocalSide(int streamId, boolean remote) {
        StreamState state = this.activeStreams.get(streamId);
        if (state != null) {
            state.closeLocalSide();
            if (state.isRemoteSideClosed()) {
                this.removeActiveStream(streamId, remote);
            }
        }
    }

    boolean hasReceivedReply(int streamId) {
        StreamState state = this.activeStreams.get(streamId);
        return state != null && state.hasReceivedReply();
    }

    void receivedReply(int streamId) {
        StreamState state = this.activeStreams.get(streamId);
        if (state != null) {
            state.receivedReply();
        }
    }

    int getSendWindowSize(int streamId) {
        if (streamId == 0) {
            return this.sendWindowSize.get();
        }
        StreamState state = this.activeStreams.get(streamId);
        return state != null ? state.getSendWindowSize() : -1;
    }

    int updateSendWindowSize(int streamId, int deltaWindowSize) {
        if (streamId == 0) {
            return this.sendWindowSize.addAndGet(deltaWindowSize);
        }
        StreamState state = this.activeStreams.get(streamId);
        return state != null ? state.updateSendWindowSize(deltaWindowSize) : -1;
    }

    int updateReceiveWindowSize(int streamId, int deltaWindowSize) {
        if (streamId == 0) {
            return this.receiveWindowSize.addAndGet(deltaWindowSize);
        }
        StreamState state = this.activeStreams.get(streamId);
        if (state == null) {
            return -1;
        }
        if (deltaWindowSize > 0) {
            state.setReceiveWindowSizeLowerBound(0);
        }
        return state.updateReceiveWindowSize(deltaWindowSize);
    }

    int getReceiveWindowSizeLowerBound(int streamId) {
        if (streamId == 0) {
            return 0;
        }
        StreamState state = this.activeStreams.get(streamId);
        return state != null ? state.getReceiveWindowSizeLowerBound() : 0;
    }

    void updateAllSendWindowSizes(int deltaWindowSize) {
        for (StreamState state : this.activeStreams.values()) {
            state.updateSendWindowSize(deltaWindowSize);
        }
    }

    void updateAllReceiveWindowSizes(int deltaWindowSize) {
        for (StreamState state : this.activeStreams.values()) {
            state.updateReceiveWindowSize(deltaWindowSize);
            if (deltaWindowSize >= 0) continue;
            state.setReceiveWindowSizeLowerBound(deltaWindowSize);
        }
    }

    boolean putPendingWrite(int streamId, PendingWrite pendingWrite) {
        StreamState state = this.activeStreams.get(streamId);
        return state != null && state.putPendingWrite(pendingWrite);
    }

    PendingWrite getPendingWrite(int streamId) {
        if (streamId == 0) {
            for (Map.Entry<Integer, StreamState> e : this.activeStreams().entrySet()) {
                PendingWrite pendingWrite;
                StreamState state = e.getValue();
                if (state.getSendWindowSize() <= 0 || (pendingWrite = state.getPendingWrite()) == null) continue;
                return pendingWrite;
            }
            return null;
        }
        StreamState state = this.activeStreams.get(streamId);
        return state != null ? state.getPendingWrite() : null;
    }

    PendingWrite removePendingWrite(int streamId) {
        StreamState state = this.activeStreams.get(streamId);
        return state != null ? state.removePendingWrite() : null;
    }

    public static final class PendingWrite {
        final SpdyDataFrame spdyDataFrame;
        final ChannelPromise promise;

        PendingWrite(SpdyDataFrame spdyDataFrame, ChannelPromise promise) {
            this.spdyDataFrame = spdyDataFrame;
            this.promise = promise;
        }

        void fail(Throwable cause) {
            this.spdyDataFrame.release();
            this.promise.setFailure(cause);
        }
    }

    private final class StreamComparator
    implements Comparator<Integer>,
    Serializable {
        private static final long serialVersionUID = 1161471649740544848L;

        StreamComparator() {
        }

        @Override
        public int compare(Integer id1, Integer id2) {
            StreamState state1 = (StreamState)SpdySession.this.activeStreams.get(id1);
            StreamState state2 = (StreamState)SpdySession.this.activeStreams.get(id2);
            int result = state1.getPriority() - state2.getPriority();
            if (result != 0) {
                return result;
            }
            return id1 - id2;
        }
    }

    private static final class StreamState {
        private final byte priority;
        private boolean remoteSideClosed;
        private boolean localSideClosed;
        private boolean receivedReply;
        private final AtomicInteger sendWindowSize;
        private final AtomicInteger receiveWindowSize;
        private int receiveWindowSizeLowerBound;
        private final Queue<PendingWrite> pendingWriteQueue = new ConcurrentLinkedQueue<PendingWrite>();

        StreamState(byte priority, boolean remoteSideClosed, boolean localSideClosed, int sendWindowSize, int receiveWindowSize) {
            this.priority = priority;
            this.remoteSideClosed = remoteSideClosed;
            this.localSideClosed = localSideClosed;
            this.sendWindowSize = new AtomicInteger(sendWindowSize);
            this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
        }

        byte getPriority() {
            return this.priority;
        }

        boolean isRemoteSideClosed() {
            return this.remoteSideClosed;
        }

        void closeRemoteSide() {
            this.remoteSideClosed = true;
        }

        boolean isLocalSideClosed() {
            return this.localSideClosed;
        }

        void closeLocalSide() {
            this.localSideClosed = true;
        }

        boolean hasReceivedReply() {
            return this.receivedReply;
        }

        void receivedReply() {
            this.receivedReply = true;
        }

        int getSendWindowSize() {
            return this.sendWindowSize.get();
        }

        int updateSendWindowSize(int deltaWindowSize) {
            return this.sendWindowSize.addAndGet(deltaWindowSize);
        }

        int updateReceiveWindowSize(int deltaWindowSize) {
            return this.receiveWindowSize.addAndGet(deltaWindowSize);
        }

        int getReceiveWindowSizeLowerBound() {
            return this.receiveWindowSizeLowerBound;
        }

        void setReceiveWindowSizeLowerBound(int receiveWindowSizeLowerBound) {
            this.receiveWindowSizeLowerBound = receiveWindowSizeLowerBound;
        }

        boolean putPendingWrite(PendingWrite msg) {
            return this.pendingWriteQueue.offer(msg);
        }

        PendingWrite getPendingWrite() {
            return this.pendingWriteQueue.peek();
        }

        PendingWrite removePendingWrite() {
            return this.pendingWriteQueue.poll();
        }

        void clearPendingWrites(Throwable cause) {
            PendingWrite pendingWrite;
            while ((pendingWrite = this.pendingWriteQueue.poll()) != null) {
                pendingWrite.fail(cause);
            }
        }
    }
}

