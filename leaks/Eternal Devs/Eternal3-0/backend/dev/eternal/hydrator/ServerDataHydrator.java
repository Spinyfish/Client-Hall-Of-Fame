package dev.eternal.hydrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.eternal.structures.ServerData;
import dev.eternal.structures.User;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ServerDataHydrator implements Hydrator<ServerData> {

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Override
  public CompletableFuture<ServerData> hydrate(Supplier<CompletableFuture<HttpResponse<String>>> supplier) {
    var future = supplier.get();
    return future.thenApply(stringHttpResponse -> gson.fromJson(stringHttpResponse.body(), ServerData.class));
  }
}