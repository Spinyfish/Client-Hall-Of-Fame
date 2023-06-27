package de.jcm.discordgamesdk;

public class NetworkManager
{
    private final long pointer;
    private final Core core;
    
    NetworkManager(final long pointer, final Core core) {
        this.pointer = pointer;
        this.core = core;
    }
    
    public long getPeerId() {
        return this.core.execute(() -> this.getPeerId(this.pointer));
    }
    
    public void flush() {
        final Result result = this.core.execute(() -> this.flush(this.pointer));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void openPeer(final long peerId, final String routeData) {
        final Result result = this.core.execute(() -> this.openPeer(this.pointer, peerId, routeData));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void updatePeer(final long peerId, final String routeData) {
        final Result result = this.core.execute(() -> this.updatePeer(this.pointer, peerId, routeData));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void closePeer(final long peerId) {
        final Result result = this.core.execute(() -> this.closePeer(this.pointer, peerId));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void openChannel(final long peerId, final byte channelId, final boolean reliable) {
        final Result result = this.core.execute(() -> this.openChannel(this.pointer, peerId, channelId, reliable));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void closeChannel(final long peerId, final byte channelId) {
        final Result result = this.core.execute(() -> this.closeChannel(this.pointer, peerId, channelId));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void sendMessage(final long peerId, final byte channelId, final byte[] data) {
        final Result result = this.core.execute(() -> this.sendMessage(this.pointer, peerId, channelId, data, 0, data.length));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    private native long getPeerId(final long p0);
    
    private native Result flush(final long p0);
    
    private native Result openPeer(final long p0, final long p1, final String p2);
    
    private native Result updatePeer(final long p0, final long p1, final String p2);
    
    private native Result closePeer(final long p0, final long p1);
    
    private native Result openChannel(final long p0, final long p1, final byte p2, final boolean p3);
    
    private native Result closeChannel(final long p0, final long p1, final byte p2);
    
    private native Result sendMessage(final long p0, final long p1, final byte p2, final byte[] p3, final int p4, final int p5);
}
