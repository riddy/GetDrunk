package org.drink.getdrunk.backend.model;

import com.google.gson.annotations.SerializedName;

public class CloseBy {
   @SerializedName( "is_close_by" )
   private boolean isCloseBy;
   private int rssi;

   public static class Builder {
      private CloseBy obj;

      public Builder() {
         obj = new CloseBy();
      }

      public Builder withIsCloseBy( boolean isCloseBy ) {
         obj.isCloseBy = isCloseBy;
         return this;
      }

      public Builder withRssi( int rssi ) {
         obj.rssi = rssi;
         return this;
      }

      public CloseBy build() {
         return obj;
      }
   }
}
