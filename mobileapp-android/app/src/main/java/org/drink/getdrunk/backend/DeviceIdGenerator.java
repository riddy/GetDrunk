package org.drink.getdrunk.backend;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;

import org.drink.getdrunk.application.GetDrunkApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;

class DeviceIdGenerator {
   private static final Logger LOG = LoggerFactory.getLogger( DeviceIdGenerator.class );
   private static DeviceIdGenerator instance;
   private String deviceId;

   private DeviceIdGenerator() {
      // singleton
   }

   public static DeviceIdGenerator getInstance() {
      if ( instance == null ) {
         synchronized ( DeviceIdGenerator.class ) {
            if ( instance == null ) {
               instance = new DeviceIdGenerator();
            }
         }
      }
      return instance;
   }

   public String getId() {
      if ( deviceId == null ) {
         TelephonyManager tMgr = (TelephonyManager) GetDrunkApp.getContext().getSystemService(
            Context.TELEPHONY_SERVICE );
         deviceId = tMgr.getLine1Number();
         if(deviceId == null) return "default_marita";
      }
      return deviceId;
   }
}
