package org.drink.getdrunk.bluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.drink.getdrunk.application.GetDrunkApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mway.bluerange.android.sdk.core.scanning.BeaconMessageScanner;
import com.mway.bluerange.android.sdk.core.scanning.messages.BeaconMessage;
import com.mway.bluerange.android.sdk.core.scanning.messages.IBeacon;
import com.mway.bluerange.android.sdk.services.trigger.BeaconTrigger;

import rx.Observable;

public class BeaconDetector {
   private static final Logger LOG = LoggerFactory.getLogger( BeaconDetector.class );
   private static BeaconDetector instance;

   private BeaconMessageScanner scanner;

   private BeaconDetector() {
      LOG.debug( "Initializing scanner" );
      scanner = new BeaconMessageScanner( GetDrunkApp.getContext() );
   }

   public static BeaconDetector getInstance() {
      if ( instance == null ) {
         synchronized ( BeaconDetector.class ) {
            if ( instance == null ) {
               instance = new BeaconDetector();
            }
         }
      }
      return instance;
   }

   public Observable<Float> getRangeToBeacon() {
      return Observable.<Float> create( subscriber -> {

         BeaconTrigger policyTrigger = new BeaconTrigger( scanner, GetDrunkApp.getContext() );
         List<IBeacon> iBeacons = new ArrayList<>();
         iBeacons.add( new IBeacon( UUID.fromString( "ca2f81f3-4c67-4f15-9731-eb7204d9d143" ), 1, 1 ) );
         policyTrigger.addIBeaconTriggers( iBeacons );

         policyTrigger.addObserver( new BeaconTrigger.BeaconTriggerObserver() {
            @Override
            public void onBeaconActive( BeaconMessage beaconMessage ) {
               LOG.debug( "onBeaconActive" );
            }

            @Override
            public void onBeaconInactive( BeaconMessage beaconMessage ) {
               LOG.debug( "onBeaconInactive" );
            }

            @Override
            public void onNewDistance( BeaconMessage beaconMessage, float v ) {
               LOG.debug( "onNewDistance {}", v );
               subscriber.onNext( v );
            }
         } );

         LOG.debug( "Let's start scanning..." );
         scanner.startScanning();

      } ).doOnUnsubscribe( ( ) -> {
         LOG.debug( "Stop scanner because of unsubscribe" );
         scanner.stopScanning();
      } );
   }
}
