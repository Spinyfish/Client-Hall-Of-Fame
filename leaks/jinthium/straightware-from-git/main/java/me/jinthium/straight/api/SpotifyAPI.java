package me.jinthium.straight.api;

import com.sun.net.httpserver.HttpServer;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.impl.utils.ChatUtil;
import me.jinthium.straight.impl.utils.Multithreading;
import net.minecraft.util.ChatComponentText;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.pkce.AuthorizationCodePKCERequest;
import se.michaelthelin.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

interface SpotifyCallback {
    void codeCallback(final String code);
}

public class SpotifyAPI implements MinecraftInstance {
    private static final String CLIENT_ID = "c95f8a13cb3942d9b6c38436c8510969";
    private static final String REDIRECT_URI = "http://localhost:4030";

    private final SpotifyApi SPOTIFY = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setRedirectUri(SpotifyHttpManager.makeUri(REDIRECT_URI))
            .build();

    private final String CODE_VERIFIER = randomString();
    private final String CODE_CHALLENGE = getChallengeHash(CODE_VERIFIER);
    private final AuthorizationCodeUriRequest AUTH_CODE_URI = SPOTIFY.authorizationCodePKCEUri(CODE_CHALLENGE)
            .scope("user-read-playback-state user-read-playback-position user-modify-playback-state user-read-currently-playing")
            .build();
    public Track currentTrack;
    public CurrentlyPlayingContext currentPlayingContext;
    private boolean authenticated;
    private HttpServer callbackServer;
    private int tokenRefreshInterval = 2;

    public void init() {
        if (!authenticated) {
            try {
                Desktop.getDesktop().browse(AUTH_CODE_URI.execute());
                Multithreading.runAsync(() -> {
                    try {
                        stopCallbackServer();
                        ChatUtil.print("Waiting for Spotify confirmation...");
                        startCallbackServer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCallbackServer() throws IOException {
        callbackServer = HttpServer.create(new InetSocketAddress(4030), 0);
        callbackServer.createContext("/", context -> {
            callback.codeCallback(context.getRequestURI().getQuery().split("=")[1]);
            final String infoMessage = context.getRequestURI().getQuery().contains("code")
                    ? "<h1>Successfully authorized.\nYou can now close this window, have fun!</h1>"
                    : "<h1>Unable to Authorize client, re-toggle the module.</h1>";
            context.sendResponseHeaders(200, infoMessage.length());
            try (OutputStream out = context.getResponseBody()) {
                out.write(infoMessage.getBytes());
            }
            stopCallbackServer();
        });
        callbackServer.start();
    }

    private void stopCallbackServer() {
        if (callbackServer != null) {
            callbackServer.stop(0);
        }
    }

    private String getChallengeHash(String codeVerifier) {
        byte[] bytes = codeVerifier.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getUrlEncoder().encodeToString(digest.digest(bytes)).replace("=", "");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String randomString() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 43; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void updateToken() {
        try {
            final AuthorizationCodeCredentials refreshRequest = SPOTIFY.authorizationCodePKCERefresh().build().execute();
            SPOTIFY.setAccessToken(refreshRequest.getAccessToken());
            SPOTIFY.setRefreshToken(refreshRequest.getRefreshToken());
            tokenRefreshInterval = refreshRequest.getExpiresIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startUpdatingToken() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::updateToken, tokenRefreshInterval - 2, tokenRefreshInterval - 2, TimeUnit.SECONDS);
    }

    public void startFetchingPlaybackInfo() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::fetchPlaybackInfo, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void fetchPlaybackInfo() {
        try {
            final GetInformationAboutUsersCurrentPlaybackRequest getCurrentPlaybackInfo = SPOTIFY.getInformationAboutUsersCurrentPlayback().build();
            final CurrentlyPlayingContext currentlyPlayingContext = getCurrentPlaybackInfo.execute();
            final String currentTrackId = currentlyPlayingContext.getItem().getId();
            GetTrackRequest getTrackRequest = SPOTIFY.getTrack(currentTrackId).build();
            this.currentTrack = getTrackRequest.execute();
            this.currentPlayingContext = currentlyPlayingContext;
        } catch (Exception ignored) {
        }
    }

    private final SpotifyCallback callback = code -> {
        ChatUtil.print("Connecting to Spotify ;)");
        AuthorizationCodePKCERequest authCodePKCERequest = SPOTIFY.authorizationCodePKCE(code, CODE_VERIFIER).build();
        try {
            final AuthorizationCodeCredentials authCredentials = authCodePKCERequest.execute();
            SPOTIFY.setAccessToken(authCredentials.getAccessToken());
            SPOTIFY.setRefreshToken(authCredentials.getRefreshToken());
            tokenRefreshInterval = authCredentials.getExpiresIn();
            authenticated = true;
            startUpdatingToken();
            startFetchingPlaybackInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}