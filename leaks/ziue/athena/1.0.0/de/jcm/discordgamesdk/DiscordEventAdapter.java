package de.jcm.discordgamesdk;

import de.jcm.discordgamesdk.user.*;

public abstract class DiscordEventAdapter
{
    public void onActivityJoin(final String secret) {
    }
    
    public void onActivitySpectate(final String secret) {
    }
    
    public void onActivityJoinRequest(final DiscordUser user) {
    }
    
    public void onCurrentUserUpdate() {
    }
    
    public void onOverlayToggle(final boolean locked) {
    }
    
    public void onRelationshipRefresh() {
    }
    
    public void onRelationshipUpdate(final Relationship relationship) {
    }
    
    public void onLobbyUpdate(final long lobbyId) {
    }
    
    public void onLobbyDelete(final long lobbyId, final int reason) {
    }
    
    public void onMemberConnect(final long lobbyId, final long userId) {
    }
    
    public void onMemberUpdate(final long lobbyId, final long userId) {
    }
    
    public void onMemberDisconnect(final long lobbyId, final long userId) {
    }
    
    public void onLobbyMessage(final long lobbyId, final long userId, final byte[] data) {
    }
    
    public void onSpeaking(final long lobbyId, final long userId, final boolean speaking) {
    }
    
    public void onNetworkMessage(final long lobbyId, final long userId, final byte channelId, final byte[] data) {
    }
    
    public void onMessage(final long peerId, final byte channelId, final byte[] data) {
    }
    
    public void onRouteUpdate(final String routeData) {
    }
}
