package me.uncodable.srt.impl.modules.api;

import java.util.ArrayList;

public class FriendManager {
   private final ArrayList<String> friends = new ArrayList<>();

   public void addFriend(String playerName) {
      if (playerName != null) {
         this.friends.add(playerName);
      }
   }

   public boolean isFriend(String playerName) {
      for(String name : this.friends) {
         if (name.equalsIgnoreCase(playerName)) {
            return true;
         }
      }

      return false;
   }

   public void removeFriend(String playerName) {
      if (playerName != null) {
         this.friends.remove(playerName);
      }
   }
}
