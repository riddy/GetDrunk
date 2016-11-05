package org.drink.getdrunk.background;

import org.drink.getdrunk.backend.RestBackend;
import org.drink.getdrunk.bluetooth.BleScanner;
import org.drink.getdrunk.backend.model.CloseBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class BackgroundService extends Service {
   private static final Logger LOG = LoggerFactory.getLogger( BackgroundService.class );
   private CompositeSubscription subscriptions = new CompositeSubscription();
   private static final int MIN_RSSI_WHEN_IN_RANGE = -75;
   private boolean backendNotifiedAboutCloseByFalse;

   @Nullable
   @Override
   public IBinder onBind( Intent intent ) {
      return null;
   }

   @Override
   public int onStartCommand( Intent intent, int flags, int startId ) {
      return START_STICKY;
   }

   @Override
   public void onCreate() {
      super.onCreate();
      LOG.debug( "Service onCreate" );
      start();
   }

   private void start() {
      LOG.debug( "Start background feature..." );
      subscriptions.add( new BleScanner().findRssiForOurBeaconInfinite()
         .sample( 2, TimeUnit.SECONDS ) // prevent DoS...
         .doOnNext( item -> LOG.debug( "Found new RSSI value" ) )
         .concatMap( (Func1<Integer, Observable<?>>) rssiValue -> {
            LOG.debug( "found new rssiValue {}", rssiValue );
            if ( rssiValue > MIN_RSSI_WHEN_IN_RANGE ) {
               return notifyBackendAboutCloseBy( rssiValue );
            }
            return notifyBackendAboutAway();
         } )
         .timeout( 10, TimeUnit.SECONDS, notifyBackendAboutAway().map( aVoid -> 0 ) )
         .doOnError( throwable -> LOG.error( "Something unexpected happened - ignoring", throwable ) )
         .onErrorReturn( null )
         .subscribeOn( Schedulers.io() )
         .observeOn( Schedulers.io() )
         .subscribe( distance -> {
            LOG.debug( "In range!!! -> distance to beacon: {}", distance );
         }, throwable -> {
            LOG.error( "Something unexpected happened", throwable );
            start(); // restart
         }, ( ) -> {
            LOG.debug( "Observable has completed -> restarting" );
            start();
         } ) );
   }

   private Observable<Void> notifyBackendAboutCloseBy( int rssiValue ) {
      LOG.debug( "notifyBackendAboutCloseBy" );
      backendNotifiedAboutCloseByFalse = false;
      return RestBackend.getInstance().closeBy( new CloseBy.Builder()
         .withIsCloseBy( true )
         .withRssi( rssiValue )
         .build() );
   }

   private Observable<Void> notifyBackendAboutAway() {
      if ( !backendNotifiedAboutCloseByFalse ) {
         LOG.debug( "notifyBackendAboutAway" );
         backendNotifiedAboutCloseByFalse = true;
         return RestBackend.getInstance().closeBy( new CloseBy.Builder()
            .withIsCloseBy( false )
            .build() );
      }
      else {
         LOG.debug( "notifyBackendAboutAway aborted, because already notified" );
         return Observable.just( null );
      }
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      LOG.debug( "Service onDestroy" );
      subscriptions.clear();
   }
}
