package org.drink.getdrunk.frontend;

import org.androidannotations.annotations.EActivity;
import org.drink.getdrunk.R;
import org.drink.getdrunk.bluetooth.BeaconDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@EActivity( R.layout.activity_main )
public class MainActivity extends AppCompatActivity {
   private static final Logger LOG = LoggerFactory.getLogger( MainActivity.class );

   private CompositeSubscription subscriptions = new CompositeSubscription();

   @Override
   protected void onCreate( @Nullable Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      subscriptions.add( BeaconDetector.getInstance().getRangeToBeacon()
         .subscribeOn( Schedulers.io() )
         .observeOn( AndroidSchedulers.mainThread() )
         .subscribe( distance -> {
            LOG.debug( "Distance to beacon: {}", distance );
         }, throwable -> {
            LOG.error( "Something unexpected happened?", throwable );
         } ) );
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      subscriptions.clear();
   }
}
