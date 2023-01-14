package net.minecraft.util;

public class RegistryDefaulted<K, V> extends RegistrySimple<K, V> {
   private final V defaultObject;

   public RegistryDefaulted(V defaultObjectIn) {
      this.defaultObject = defaultObjectIn;
   }

   @Override
   public V getObject(K name) {
      V v = super.getObject(name);
      return (V)(v == null ? this.defaultObject : v);
   }
}
