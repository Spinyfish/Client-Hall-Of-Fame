package dev.eternal.hydrator;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface Hydrator<T> {

  CompletableFuture<T> hydrate(Supplier<CompletableFuture<HttpResponse<String>>> supplier);

  class Factory {

    public static <T extends Hydrator<?>> T create(Type type) {
      return switch (type) {
        case USER -> (T) new UserHydrator();
        case USER_LIST -> (T) new UserListHydrator();
        case SERVER -> (T) new ServerDataHydrator();
      };
    }

  }

  enum Type {
    USER,
    USER_LIST,
    SERVER
  }

}