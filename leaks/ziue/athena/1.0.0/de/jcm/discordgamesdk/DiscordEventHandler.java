package de.jcm.discordgamesdk;

import java.util.*;
import java.util.concurrent.*;
import de.jcm.discordgamesdk.user.*;

public class DiscordEventHandler extends DiscordEventAdapter
{
    private final List<DiscordEventAdapter> listeners;
    
    public DiscordEventHandler() {
        this.listeners = new CopyOnWriteArrayList<DiscordEventAdapter>();
    }
    
    public void addListener(final DiscordEventAdapter listener) {
        this.listeners.add(listener);
    }
    
    public boolean removeListener(final DiscordEventAdapter listener) {
        return this.listeners.remove(listener);
    }
    
    public void removeAllListeners() {
        this.listeners.clear();
    }
    
    @Override
    public void onActivityJoin(final String secret) {
        this.listeners.forEach(l -> l.onActivityJoin(secret));
    }
    
    @Override
    public void onActivitySpectate(final String secret) {
        this.listeners.forEach(l -> l.onActivitySpectate(secret));
    }
    
    @Override
    public void onActivityJoinRequest(final DiscordUser user) {
        this.listeners.forEach(l -> l.onActivityJoinRequest(user));
    }
    
    @Override
    public void onCurrentUserUpdate() {
        this.listeners.forEach(DiscordEventAdapter::onCurrentUserUpdate);
    }
    
    @Override
    public void onOverlayToggle(final boolean locked) {
        this.listeners.forEach(l -> l.onOverlayToggle(locked));
    }
    
    @Override
    public void onRelationshipRefresh() {
        this.listeners.forEach(DiscordEventAdapter::onRelationshipRefresh);
    }
    
    @Override
    public void onRelationshipUpdate(final Relationship relationship) {
        this.listeners.forEach(l -> l.onRelationshipUpdate(relationship));
    }
    
    @Override
    public void onLobbyUpdate(final long lobbyId) {
        this.listeners.forEach(l -> l.onLobbyUpdate(lobbyId));
    }
    
    @Override
    public void onLobbyDelete(final long lobbyId, final int reason) {
        this.listeners.forEach(l -> l.onLobbyDelete(lobbyId, reason));
    }
    
    @Override
    public void onMemberConnect(final long lobbyId, final long userId) {
        this.listeners.forEach(l -> l.onMemberConnect(lobbyId, userId));
    }
    
    @Override
    public void onMemberUpdate(final long lobbyId, final long userId) {
        this.listeners.forEach(l -> l.onMemberUpdate(lobbyId, userId));
    }
    
    @Override
    public void onMemberDisconnect(final long lobbyId, final long userId) {
        this.listeners.forEach(l -> l.onMemberDisconnect(lobbyId, userId));
    }
    
    @Override
    public void onLobbyMessage(final long lobbyId, final long userId, final byte[] data) {
        this.listeners.forEach(l -> l.onLobbyMessage(lobbyId, userId, data));
    }
    
    @Override
    public void onSpeaking(final long lobbyId, final long userId, final boolean speaking) {
        this.listeners.forEach(l -> l.onSpeaking(lobbyId, userId, speaking));
    }
    
    @Override
    public void onNetworkMessage(final long lobbyId, final long userId, final byte channelId, final byte[] data) {
        this.listeners.forEach(l -> l.onNetworkMessage(lobbyId, userId, channelId, data));
    }
    
    @Override
    public void onMessage(final long peerId, final byte channelId, final byte[] data) {
        this.listeners.forEach(l -> l.onMessage(peerId, channelId, data));
    }
    
    @Override
    public void onRouteUpdate(final String routeData) {
        this.listeners.forEach(l -> l.onRouteUpdate(routeData));
    }
}
