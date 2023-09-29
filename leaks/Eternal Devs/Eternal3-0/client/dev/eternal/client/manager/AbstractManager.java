package dev.eternal.client.manager;

import java.util.HashSet;

public abstract class AbstractManager<T> extends HashSet<T> {

  public abstract void init();

  public abstract <U extends T> U getByName(String name);

}
