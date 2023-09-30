package dev.tenacity.utils.spotify;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.pkce.AuthorizationCodePKCERequest;
import dev.tenacity.Tenacity;
import dev.tenacity.utils.Utils;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.util.EnumChatFormatting;
import org.apache.hc.core5.http.ParseException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

interface SpotifyCallBack {
    void codeCallback(final String code);
}

public class SpotifyAPI implements Utils {
    public final static String CODE_CHALLENGE = "w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo";
    private final static String CODE_VERIFIER = "NlJx4kD4opk4HY7zBM6WfUHxX7HoF8A2TUhOIPGA74w";
    public final static Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
    public final static File CLIENT_ID_DIR = new File(Tenacity.DIRECTORY, "SpotifyID.json");

    private int tokenRefreshInterval = 2;
    public SpotifyApi spotifyApi;
    public AuthorizationCodeUriRequest authCodeUriRequest;
    public Track currentTrack;
    public CurrentlyPlayingContext currentPlayingContext;
    public boolean authenticated;
    private HttpServer callbackServer;

    private final SpotifyCallBack callback = code -> {
        ChatUtil.print("Spotify", EnumChatFormatting.GREEN, "Connecting to Spotify...");
        AuthorizationCodePKCERequest authCodePKCERequest = spotifyApi.authorizationCodePKCE(code, CODE_VERIFIER).build();
        try {
            final AuthorizationCodeCredentials authCredentials = authCodePKCERequest.execute();
            spotifyApi.setAccessToken(authCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authCredentials.getRefreshToken());
            tokenRefreshInterval = authCredentials.getExpiresIn();
            authenticated = true;
            new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        TimeUnit.SECONDS.sleep(tokenRefreshInterval - 2);
                        System.out.println("Refreshing token...");
                        final AuthorizationCodeCredentials refreshRequest = spotifyApi.authorizationCodePKCERefresh().build().execute();
                        spotifyApi.setAccessToken(refreshRequest.getAccessToken());
                        spotifyApi.setRefreshToken(refreshRequest.getRefreshToken());
                        tokenRefreshInterval = refreshRequest.getExpiresIn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        final CurrentlyPlayingContext currentlyPlayingContext = spotifyApi.getInformationAboutUsersCurrentPlayback().build().execute();
                        final String currentTrackId = currentlyPlayingContext.getItem().getId();
                        this.currentTrack = spotifyApi.getTrack(currentTrackId).build().execute();
                        this.currentPlayingContext = currentlyPlayingContext;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void startConnection() {
        if (!authenticated) {
            try {
                //Open the authorization window on the default browser
                Desktop.getDesktop().browse(authCodeUriRequest.execute());
                Tenacity.INSTANCE.getExecutorService().submit(() -> {
                    try {
                        if (callbackServer != null) {
                            // Close the server if the module was disabled and re-enabled, to prevent already bound exception.
                            callbackServer.stop(0);
                        }
                        ChatUtil.print("Spotify", EnumChatFormatting.GREEN, "Please allow access to the application.");
                        callbackServer = HttpServer.create(new InetSocketAddress(4030), 0);
                        callbackServer.createContext("/", context -> {
                            callback.codeCallback(context.getRequestURI().getQuery().split("=")[1]);
                            final String messageSuccess = context.getRequestURI().getQuery().contains("code")
                                    ? "Successfully authorized.\nYou can now close this window, have fun on Tenacity!"
                                    : "Unable to Authorize client, re-toggle the module.";
                            context.sendResponseHeaders(200, messageSuccess.length());
                            OutputStream out = context.getResponseBody();
                            out.write(messageSuccess.getBytes());
                            out.close();
                            callbackServer.stop(0);
                        });
                        callbackServer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void build(String clientID) {
        spotifyApi = new SpotifyApi.Builder().setClientId(clientID)
                .setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:4030"))
                .build();

        authCodeUriRequest = spotifyApi.authorizationCodePKCEUri(CODE_CHALLENGE)
                .code_challenge_method("S256")
                .scope("user-read-playback-state user-read-playback-position user-modify-playback-state user-read-currently-playing")
                .build();

    }

    public void skipToPreviousTrack() {
        try {
            spotifyApi.skipUsersPlaybackToPreviousTrack().build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            ChatUtil.print("Spotify", EnumChatFormatting.RED, e.getMessage());
        }
    }

    public void skipTrack() {
        try {
            spotifyApi.skipUsersPlaybackToNextTrack().build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            ChatUtil.print("Spotify", EnumChatFormatting.RED, e.getMessage());
        }
    }

    public void toggleShuffleState() {
        try {
            spotifyApi.toggleShuffleForUsersPlayback(!currentPlayingContext.getShuffle_state()).build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            ChatUtil.print("Spotify", EnumChatFormatting.RED, e.getMessage());
        }
    }

    public void pausePlayback() {
        try {
            spotifyApi.pauseUsersPlayback().build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            ChatUtil.print("Spotify", EnumChatFormatting.RED, e.getMessage());
        }
    }

    public void resumePlayback() {
        try {
            spotifyApi.startResumeUsersPlayback().build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            ChatUtil.print("Spotify", EnumChatFormatting.RED, e.getMessage());
        }
    }

    public boolean isPlaying(){
        return currentPlayingContext.getIs_playing();
    }


}
