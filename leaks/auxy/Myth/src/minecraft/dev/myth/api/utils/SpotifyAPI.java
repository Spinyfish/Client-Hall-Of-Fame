/**
 * @project Myth
 * @author CodeMan
 * @at 27.10.22, 19:55
 */
package dev.myth.api.utils;

import com.sun.net.httpserver.HttpServer;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.pkce.AuthorizationCodePKCERequest;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToNextTrackRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import lombok.Getter;

import java.awt.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class SpotifyAPI {

    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId("5d6bf1de9d7847e3a22250e4ef64059c")
            .setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:1337"))
            .build();

    private final String verifier = StringUtil.generateRandomString(43);
    private final String challengeHash = hash(verifier);

    private final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi
            .authorizationCodePKCEUri(challengeHash)
            .scope("user-read-playback-state user-read-currently-playing user-modify-playback-state streaming user-read-private")
            .build();

    private HttpServer callbackServer;
    private int tokenExpiration = 30;

    @Getter
    private boolean authenticated = false;
    private boolean shouldRun;

    @Getter
    private Track currentTrack;
    @Getter
    private CurrentlyPlayingContext currentPlayingContext;

    public SpotifyAPI() {
        shouldRun = true;
        init();
    }

    public void close() {
        shouldRun = false;
    }

    private void init() {
        if (authenticated) return;
        try {
            Desktop.getDesktop().browse(authorizationCodeUriRequest.execute());
            if (callbackServer != null) callbackServer.stop(0);
            callbackServer = HttpServer.create(new InetSocketAddress(1337), 0);
            callbackServer.createContext("/", httpExchange -> {
                final String query = httpExchange.getRequestURI().getQuery();
                if (query == null || !query.contains("code=")) {
                    httpExchange.sendResponseHeaders(400, 0);
                    httpExchange.close();
                    return;
                } else {
                    final String httpResponse = "Successfully connected to Spotify!\nYou may now close this window.";
                    httpExchange.sendResponseHeaders(200, httpResponse.length());
                    httpExchange.getResponseBody().write(httpResponse.getBytes(StandardCharsets.UTF_8));
                    httpExchange.close();
                    callbackServer.stop(0);
                }
                final String code = query.split("=")[1];
                final AuthorizationCodePKCERequest authCodePKCERequest = spotifyApi.authorizationCodePKCE(code, verifier).build();
                try {
                    final AuthorizationCodeCredentials authorizationCodeCredentials = authCodePKCERequest.execute();
                    spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
                    spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
                    authenticated = true;
                    tokenExpiration = authorizationCodeCredentials.getExpiresIn();
                    new Thread(() -> {
                        while (shouldRun) {
                            try {
                                TimeUnit.SECONDS.sleep(tokenExpiration - 30);
                                final AuthorizationCodeCredentials refreshRequest = spotifyApi.authorizationCodePKCERefresh().build().execute();
                                spotifyApi.setAccessToken(refreshRequest.getAccessToken());
                                spotifyApi.setRefreshToken(refreshRequest.getRefreshToken());
                                tokenExpiration = refreshRequest.getExpiresIn();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    new Thread(() -> {
                        while (shouldRun) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(500);

                                final GetInformationAboutUsersCurrentPlaybackRequest getCurrentPlaybackInfo = spotifyApi.getInformationAboutUsersCurrentPlayback().build();
                                final CurrentlyPlayingContext currentlyPlayingContext = getCurrentPlaybackInfo.execute();
                                final String currentTrackId = currentlyPlayingContext.getItem().getId();
                                final GetTrackRequest getTrackRequest = spotifyApi.getTrack(currentTrackId).build();
                                this.currentTrack = getTrackRequest.execute();
                                this.currentPlayingContext = currentlyPlayingContext;

                            } catch (Exception ignored) {
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            callbackServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void skip() {
        if (!authenticated) return;
        try {
            SkipUsersPlaybackToNextTrackRequest skipRequest = spotifyApi.skipUsersPlaybackToNextTrack()
                    .device_id(currentPlayingContext.getDevice().getId())
                    .build();
            skipRequest.executeAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String hash(String verifier) {
        try {
            return Base64.getUrlEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.UTF_8))).replace("=", "");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
