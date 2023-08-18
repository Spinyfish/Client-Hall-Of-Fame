/* November.lol © 2023 */
package lol.november.protect.ws;

import com.google.gson.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import lol.november.November;
import lol.november.feature.module.Module;
import lol.november.feature.module.impl.player.IRCChatModule;
import lol.november.protect.Protection;
import lol.november.utility.chat.Printer;
import lol.november.utility.fs.FileUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
public class NovemberWebsocket extends WebSocketClient {

  /**
   * If to allow features to be downloaded
   */
  public static boolean ALLOW_FEATURES = false;

  /**
   * The {@link URI} to the november websocket
   */
  private static final URI SERVER;

  static {
    try {
      SERVER = new URI("ws://localhost:7755");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private IRCChatModule ircChatModule;

  private boolean featuresRequest;

  public NovemberWebsocket() {
    super(SERVER);
    setTcpNoDelay(true);
    setConnectionLostTimeout(10000);

    // as a separate note, this socket server should NOT be initially connected to on a VPN
    // since we should check IPS for things like feature requests, then after the first auth they can
    // use a VPN to of course cheat

    // if the user has not created this file, it will not allow external features
    // this will intentionally not be documented to a normal user
    // this is meant for people like developers to use staff detector on a public build of november
    ALLOW_FEATURES =
      new File(FileUtils.directory, "allow_external_features").exists();

    try {
      connect();
    } catch (Exception e) {
      // TODO: crash client
      log.error("Failed to connect to websocket server");
      e.printStackTrace();
    }
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    log.info("Opened websocket with status {}", handshakedata.getHttpStatus());
  }

  @Override
  public void onMessage(String message) {
    JsonElement element;
    try {
      element = FileUtils.jsonParser.parse(message);
    } catch (JsonSyntaxException e) {
      log.error("Server sent malformed JSON data");
      e.printStackTrace();
      return;
    }

    // weird
    if (!element.isJsonObject()) return;
    JsonObject o = element.getAsJsonObject();

    if (o.has("op") && o.has("data")) {
      OpCode opCode = OpCode.of(o.get("op").getAsInt());
      if (opCode == null) {
        log.warn(
          "Server sent op code " +
            o.get("op").getAsInt() +
            ", which is not registered as a valid opcode"
        );
        return;
      }

      JsonElement data = o.get("data");

      switch (opCode) {
        case AUTHENTICATE -> {
          log.info("Authenticate request from server");
        }
        case CHAT -> {
          if (data.isJsonPrimitive()) {
            if (ircChatModule == null) {
              ircChatModule =
                November.instance().modules().get(IRCChatModule.class);
            }

            if (ircChatModule == null || !ircChatModule.toggled()) return;
            Printer.irc(data.getAsString());
          }
        }
        case FEATURE_REQUEST -> {
          if (!ALLOW_FEATURES) return;

          if (!featuresRequest) {
            log.error(
              "Server attempted to send features without request! Closing client for security. Please report this to the developers ASAP!"
            );
            log.error("Data sent by server: " + data.toString());
            log.error("Repeat: report to the developers ASAP");
            Protection.crashJVM();
            return;
          }

          featuresRequest = false;

          if (data.isJsonArray()) {
            JsonArray features = data.getAsJsonArray();
            for (JsonElement e : features) {
              if (!e.isJsonObject()) continue;

              JsonObject object = e.getAsJsonObject();

              if (!object.has("checksum")) {
                log.error(
                  "Server failed to provide checksum with feature request!"
                );
                return;
              }

              String byteChecksum = object.get("checksum").getAsString();

              String name = object.get("className").getAsString();
              JsonArray arr = object.get("bytes").getAsJsonArray();

              // so to explain this, the server sends a "feature" and sends class bytes back
              // this needs to be improved security wise as directly adding to a classloader seems super insecure
              // in the future, we should also decrypt this byte array instead of sending the obfuscated class via ws

              byte[] bytes = new byte[arr.size()];
              int index = 0;
              for (JsonElement x : arr) {
                if (!x.isJsonPrimitive()) {
                  log.warn(
                    "Malformed class byte data from server via feature request at index {}",
                    index
                  );
                  return;
                }

                bytes[index] = x.getAsByte();
                ++index;
              }

              String checksum = Protection.checksum(bytes);
              if (!checksum.equals(byteChecksum)) {
                log.error(
                  "Checksum {} does not match {}! STOP!",
                  byteChecksum,
                  checksum
                );
                return;
              }

              try {
                _defineClass(name, bytes);
                Class<?> clazz = Class.forName(name);
                if (Module.class.isAssignableFrom(clazz)) {
                  November
                    .instance()
                    .modules()
                    .add((Module) clazz.getConstructors()[0].newInstance());
                }
              } catch (Exception ex) {
                log.error("Failed to get loaded feature class");
                ex.printStackTrace();
              }
            }
          }
        }
      }
    }
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    log.info(
      "Closed websocket with code {}. (data={}, r={})",
      code,
      reason,
      remote
    );
  }

  @Override
  public void onError(Exception ex) {
    log.error("An exception occurred");
    ex.printStackTrace();
  }

  public void requestFeatures(String[] features) {
    if (!isOpen() || features.length == 0 || featuresRequest) return;

    JsonObject object = new JsonObject();
    object.addProperty("op", OpCode.FEATURE_REQUEST.getOp());
    JsonArray array = new JsonArray();
    for (String f : features) array.add(new JsonPrimitive(f));
    object.add("data", array);

    featuresRequest = true;
    sendMessage(object);
  }

  public void sendIrcChat(String message) {
    if (!isOpen()) {
      // no theres just no websocket server :skull:
      Printer.irc("Websocket not connected! Are you connected to the internet");
      return;
    }

    if (message.isEmpty()) return;

    message = message.substring(0, 256);

    JsonObject object = new JsonObject();
    object.addProperty("op", OpCode.CHAT.getOp());
    object.addProperty("data", message);

    sendMessage(object);
  }

  public void sendMessage(JsonElement element) {
    String content = element.toString();
    send(content.getBytes(StandardCharsets.UTF_8));
  }

  @SneakyThrows
  private void _defineClass(String name, byte[] bytes) {
    ClassLoader classLoader = NovemberWebsocket.class.getClassLoader();
    Method defineClassMethod = classLoader
      .getClass()
      .getDeclaredMethod(
        "defineClass",
        String.class,
        byte[].class,
        int.class,
        int.class
      );
    defineClassMethod.invoke(classLoader, name, bytes, 0, bytes.length);
  }
}
