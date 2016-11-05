package org.drink.getdrunk.backend.model;

import com.google.gson.annotations.SerializedName;

public class Goal {
   @SerializedName( "hydration_alert" )
   private boolean hydrationAlert;
   @SerializedName( "goal" )
   private int percentageReached;

   public boolean getHydrationAlert() {
      return hydrationAlert;
   }

   public int getPercentageReached() {
      return percentageReached;
   }
}
