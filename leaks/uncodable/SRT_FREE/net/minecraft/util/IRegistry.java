package net.minecraft.util;

public interface IRegistry<K, V> extends Iterable<V> {
   V getObject(K var1);

   void putObject(K var1, V var2);
}
