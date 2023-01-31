package net.java.games.input;

class UsagePair {
   private final UsagePage usage_page;
   private final Usage usage;

   public UsagePair(UsagePage usage_page, Usage usage) {
      this.usage_page = usage_page;
      this.usage = usage;
   }

   public final UsagePage getUsagePage() {
      return this.usage_page;
   }

   public final Usage getUsage() {
      return this.usage;
   }

   public final int hashCode() {
      return this.usage.hashCode() ^ this.usage_page.hashCode();
   }

   public final boolean equals(Object other) {
      if (!(other instanceof UsagePair)) {
         return false;
      } else {
         UsagePair other_pair = (UsagePair)other;
         return other_pair.usage.equals(this.usage) && other_pair.usage_page.equals(this.usage_page);
      }
   }

   public final String toString() {
      return "UsagePair: (page = " + this.usage_page + ", usage = " + this.usage + ")";
   }
}
