package org.drink.getdrunk.application;

import android.app.Application;
import android.content.Context;

public class GetDrunkApp extends Application {
   private static GetDrunkApp app;

   public GetDrunkApp() {
      super();
      app = this;
   }

   /**
    * @return ApplicationContext
    */
   public static Context getContext() {
      return app;
   }
}
