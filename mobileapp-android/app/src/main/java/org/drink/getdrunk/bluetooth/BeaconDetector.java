package org.drink.getdrunk.bluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.drink.getdrunk.application.GetDrunkApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mway.bluerange.android.sdk.core.logging.BeaconMessageLogger;
import com.mway.bluerange.android.sdk.core.scanning.BeaconMessageScanner;
import com.mway.bluerange.android.sdk.core.scanning.messages.BeaconMessage;
import com.mway.bluerange.android.sdk.core.streaming.BeaconMessageStreamNode;
import com.mway.bluerange.android.sdk.core.streaming.BeaconMessageStreamNodeReceiver;
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
         policyTrigger.addIBeaconTrigger( UUID.fromString( "ca2f81f3-4c67-4f15-9731-eb7204d9d143" ), 1, 1 );
         policyTrigger.addIBeaconTrigger( UUID.fromString( "43d1d90472eb-3197-154f-674c-f3812fca" ), 1, 1 );
         policyTrigger.addRelutionTagTrigger( 7 );
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

         scanner.addReceiver( new BeaconMessageStreamNodeReceiver() {

            @Override
            public void onMeshActive( BeaconMessageStreamNode beaconMessageStreamNode ) {
               LOG.debug( "onMeshActive" );
            }

            @Override
            public void onReceivedMessage( BeaconMessageStreamNode beaconMessageStreamNode, BeaconMessage beaconMessage ) {
               LOG.debug( "onReceivedMessage" );
            }

            @Override
            public void onMeshInactive( BeaconMessageStreamNode beaconMessageStreamNode ) {
               LOG.debug( "onMeshInactive" );
            }
         } );

         BeaconMessageLogger logger = new BeaconMessageLogger( scanner, GetDrunkApp.getContext() );
         logger.addReceiver( new BeaconMessageStreamNodeReceiver() {
            @Override
            public void onMeshActive( BeaconMessageStreamNode senderNode ) {
               LOG.debug( "onMeshActive" );
            }

            @Override
            public void onReceivedMessage( BeaconMessageStreamNode senderNode, BeaconMessage
               message ) {
               LOG.debug( "onReceivedMessage" );
            }

            @Override
            public void onMeshInactive( BeaconMessageStreamNode senderNode ) {
               LOG.debug( "onMeshInactive" );
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
