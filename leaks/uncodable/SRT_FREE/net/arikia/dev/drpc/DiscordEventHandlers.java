package net.arikia.dev.drpc;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import net.arikia.dev.drpc.callbacks.DisconnectedCallback;
import net.arikia.dev.drpc.callbacks.ErroredCallback;
import net.arikia.dev.drpc.callbacks.JoinGameCallback;
import net.arikia.dev.drpc.callbacks.JoinRequestCallback;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.arikia.dev.drpc.callbacks.SpectateGameCallback;

public class DiscordEventHandlers extends Structure {
   public ReadyCallback ready;
   public DisconnectedCallback disconnected;
   public ErroredCallback errored;
   public JoinGameCallback joinGame;
   public SpectateGameCallback spectateGame;
   public JoinRequestCallback joinRequest;

   @Override
   public List<String> getFieldOrder() {
      return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
   }

   public static class Builder {
      DiscordEventHandlers h = new DiscordEventHandlers();

      public DiscordEventHandlers.Builder setReadyEventHandler(ReadyCallback r) {
         this.h.ready = r;
         return this;
      }

      public DiscordEventHandlers.Builder setDisconnectedEventHandler(DisconnectedCallback d) {
         this.h.disconnected = d;
         return this;
      }

      public DiscordEventHandlers.Builder setErroredEventHandler(ErroredCallback e) {
         this.h.errored = e;
         return this;
      }

      public DiscordEventHandlers.Builder setJoinGameEventHandler(JoinGameCallback j) {
         this.h.joinGame = j;
         return this;
      }

      public DiscordEventHandlers.Builder setSpectateGameEventHandler(SpectateGameCallback s) {
         this.h.spectateGame = s;
         return this;
      }

      public DiscordEventHandlers.Builder setJoinRequestEventHandler(JoinRequestCallback j) {
         this.h.joinRequest = j;
         return this;
      }

      public DiscordEventHandlers build() {
         return this.h;
      }
   }
}
