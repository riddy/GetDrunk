package org.drink.getdrunk.bluetooth;

import java.util.List;

import org.drink.getdrunk.application.GetDrunkApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;

public class BleScanner {
   private static final Logger LOG = LoggerFactory.getLogger( BleScanner.class );
   private static final String BEACON_ADDRESS = "C0:4E:52:E8:16:45";
   private BluetoothAdapter bluetoothAdapter;
   private BluetoothLeScanner bluetoothLeScanner;
   private final PublishSubject<Integer> subject = PublishSubject.create();
   private Subscriber<? super Integer> subscriber;
   private final ScanCallback callback = new ScanCallback() {
      @Override
      public void onScanResult( int callbackType, ScanResult result ) {
         processScanResult( result );
      }

      @Override
      public void onBatchScanResults( List<ScanResult> results ) {
         if ( results != null ) {
            for ( ScanResult result : results ) {
               processScanResult( result );
            }
         }
      }

      private void processScanResult( ScanResult result ) {
         if ( subscriber != null && !subscriber.isUnsubscribed()
            && result != null && result.getScanRecord() != null )
         {
            if ( result.getDevice().getAddress().equals( BEACON_ADDRESS ) ) {
               subscriber.onNext( result.getRssi() );
            }
         }
         else {
            LOG.warn( "No subscriber available" );
         }
      }

      @Override
      public void onScanFailed( int errorCode ) {
         LOG.debug( "scanFailed {}", errorCode );
         if ( subscriber != null && !subscriber.isUnsubscribed() ) {
            if ( errorCode == ScanCallback.SCAN_FAILED_ALREADY_STARTED ) {
               LOG.debug( "Scan was already started" );
            }
            else {
               subscriber.onError( new RuntimeException( "" + errorCode ) );
            }
         }
      }
   };

   public BleScanner() {
      final BluetoothManager bluetoothManager = (BluetoothManager) GetDrunkApp.getContext()
         .getSystemService( Context.BLUETOOTH_SERVICE );
      if ( bluetoothManager != null ) {
         bluetoothAdapter = bluetoothManager.getAdapter();
      }
      if ( bluetoothAdapter != null ) {
         bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
      }
      Observable.<Integer> create( subscriber -> BleScanner.this.subscriber = subscriber )
         .onBackpressureBuffer()
         .subscribe( subject );
   }

   public Observable<Integer> findRssiForOurBeaconInfinite() {
      return subject
         .doOnSubscribe( this::tryToStartLeScan )
         .doOnUnsubscribe( this::stopLeScan );
   }

   private void tryToStartLeScan() {
      ScanSettings settings = new ScanSettings.Builder()
         .setReportDelay( 0 )
         .setScanMode( ScanSettings.SCAN_MODE_LOW_LATENCY )
         .build();
      if ( bluetoothLeScanner != null ) {
         LOG.debug( "start scan" );
         bluetoothLeScanner.startScan( null, settings, callback );
      }
      else {
         LOG.debug( "bluetoothLeScanner is null ??? -> trying to reinitialize" );
      }
   }

   private void stopLeScan() {
      LOG.debug( "stop scan" );
      if ( bluetoothLeScanner != null ) {
         bluetoothLeScanner.stopScan( callback );
      }
   }
}
