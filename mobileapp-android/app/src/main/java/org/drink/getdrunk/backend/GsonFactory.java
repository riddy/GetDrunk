package org.drink.getdrunk.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class GsonFactory {
   private static Gson gson;

   /**
    * Build a {@link com.google.gson.Gson} object to be used for object de-/serialization
    *
    * @return gson
    */
   static Gson buildGson() {
      if ( gson == null ) {
         gson = new GsonBuilder()
            .create();
      }
      return gson;
   }
}
