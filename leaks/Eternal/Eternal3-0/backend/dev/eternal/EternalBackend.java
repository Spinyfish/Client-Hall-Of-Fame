package dev.eternal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.eternal.hydrator.Hydrator;
import dev.eternal.structures.User;
import java.net.http.HttpClient.Version;

import dev.eternal.util.HWID;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

public class EternalBackend {

  private static final String BACKEND_URI = "http://192.248.175.93:8000";
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private final HttpClient httpClient =
      HttpClient.newBuilder().version(Version.HTTP_1_1).build();

  private CompletableFuture<HttpResponse<String>> get(Endpoint endpoint) {

    HttpRequest request =
        HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(BACKEND_URI + endpoint.endPoint()))
            .setHeader("User-Agent", "Java 11 HttpClient Bot")
            .build();

    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
  }

  public <T> CompletableFuture<HttpResponse<String>> post(T data, Endpoint endpoint) {
    String body = gson.toJson(data);
    System.out.println(body);
    HttpRequest request =
        HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .uri(URI.create(BACKEND_URI + endpoint.endPoint()))
            .header("Content-Type", "application/json")
            .build();

    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
  }

  @SneakyThrows
  public CompletableFuture<User> getUser(int id) {
    return (CompletableFuture<User>) Hydrator.Factory.create(Hydrator.Type.USER).hydrate(() -> post(id, Endpoint.USER_GET));
  }

  @SneakyThrows
  @Test
  public void test() {

  }

  public CompletableFuture<HttpResponse<String>> addUser(User user) {
    return post(user, Endpoint.USER_ADD);
  }

  @SneakyThrows
  public CompletableFuture<List<User>> getUserList() {
    return (CompletableFuture<List<User>>) Hydrator.Factory.create(Hydrator.Type.USER).hydrate(() -> post(null, Endpoint.USER_LIST));
  }

}