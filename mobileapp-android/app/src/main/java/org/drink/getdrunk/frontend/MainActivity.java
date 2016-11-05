package org.drink.getdrunk.frontend;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.drink.getdrunk.R;
import org.drink.getdrunk.backend.RestBackend;
import org.drink.getdrunk.background.BackgroundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@EActivity( R.layout.activity_main )
public class MainActivity extends AppCompatActivity {
   private static final Logger LOG = LoggerFactory.getLogger( MainActivity.class );

   private CompositeSubscription compositeSubscription = new CompositeSubscription();
   private ProgressDialog progressDialog;
   private int currentGlassFill = 0;
   private final List<Integer> images = Arrays.asList( R.drawable.glas_00,
      R.drawable.glas_01,
      R.drawable.glas_02,
      R.drawable.glas_03,
      R.drawable.glas_04,
      R.drawable.glas_05,
      R.drawable.glas_06,
      R.drawable.glas_07,
      R.drawable.glas_08,
      R.drawable.glas_09,
      R.drawable.glas_10 );

   @ViewById( R.id.activity_main )
   RelativeLayout layout;

   @ViewById
   ViewSwitcher eyes;
   @ViewById
   ImageView eyes1;
   @ViewById
   ImageView eyes2;
   @ViewById
   ImageView eyes3;

   @ViewById
   ImageView glas;

   @Override
   protected void onCreate( @Nullable Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      progressDialog = ProgressDialogBuilder.build( this );
      startService( new Intent( this, BackgroundService.class ) );
   }

   @AfterViews
   void init() {
      layout.removeView( eyes1 );
      layout.removeView( eyes2 );
      layout.removeView( eyes3 );

      layout.setOnClickListener( v -> updateUi() );
      eyes.setOnClickListener( v -> updateUi() );
      glas.setOnClickListener( v -> updateUi() );
   }

   @Override
   protected void onResume() {
      super.onResume();
      startPolling();
   }

   @Override
   protected void onPause() {
      super.onPause();
      compositeSubscription.clear();
   }

   private void startPolling() {
      LOG.debug( "start polling" );
      compositeSubscription.add( Observable.interval( 0, 1, TimeUnit.SECONDS )
         .doOnNext( aVoid -> LOG.debug( "ping..." ) )
         .concatMap( any -> RestBackend.getInstance().goals().onErrorReturn( null ) )
         .doOnNext( aVoid -> LOG.debug( "   ...pong" ) )
         .onBackpressureBuffer()
         .subscribeOn( Schedulers.io() )
         .observeOn( AndroidSchedulers.mainThread() )
         .subscribe( goal -> {
            if ( goal != null ) {
               if ( goal.getHydrationAlert() ) {
                  layout.setBackgroundResource( R.drawable.bkg_duerre );
               }
               else {
                  layout.setBackgroundResource( R.drawable.bkg );
               }

               fillGlas( goal.getPercentageReached() / 10 );
            }
         }, throwable -> {
            LOG.debug( "cannot read goals from backend", throwable );
            startPolling(); // restart
         } ) );
   }

   private void updateUi() {
      progressDialog.show();
      compositeSubscription.add( RestBackend.getInstance().goals()
         .subscribeOn( Schedulers.io() )
         .observeOn( AndroidSchedulers.mainThread() )
         .subscribe( goal -> {
            progressDialog.dismiss();
            if ( goal.getHydrationAlert() ) {
               layout.setBackgroundResource( R.drawable.bkg_duerre );
            }
            else {
               layout.setBackgroundResource( R.drawable.bkg );
            }

            fillGlas( goal.getPercentageReached() / 10 );

         }, throwable -> {
            progressDialog.dismiss();
            Toast.makeText( this, "Backend made a poo poo", Toast.LENGTH_SHORT ).show();
            LOG.debug( "cannot read goals from backend", throwable );
         } ) );
   }

   private void fillGlas( int level ) {
      if ( currentGlassFill == level ) {
         return;
      }

      AnimationDrawable mframeAnimation = new AnimationDrawable();
      mframeAnimation.setOneShot( true );

      if ( currentGlassFill < level ) {
         for ( int i = currentGlassFill; i <= level; i++ ) {
            mframeAnimation.addFrame( getResources().getDrawable( images.get( i ), null ), 50 );
         }
      }
      else {
         for ( int i = currentGlassFill; i >= level; i-- ) {
            mframeAnimation.addFrame( getResources().getDrawable( images.get( i ), null ), 50 );
         }
      }

      updateEyesFor( level );

      glas.setImageDrawable( mframeAnimation );
      mframeAnimation.setVisible( true, true );
      mframeAnimation.start();
      currentGlassFill = level;
   }

   private void updateEyesFor( int level ) {
      if ( level <= 4 ) {
         showEyes1();
      }
      else if ( level <= 6 ) {
         showEyes2();
      }
      else {
         showEyes3();
      }
   }

   private void resetView() {
      View currentView = eyes.getNextView();
      eyes.removeAllViews();
      if ( currentView != null ) {
         eyes.addView( currentView );
      }
   }

   private void showEyes1() {
      resetView();
      eyes.addView( eyes1 );
      eyes.showNext();
   }

   private void showEyes2() {
      resetView();
      eyes.addView( eyes2 );
      eyes.showNext();
   }

   private void showEyes3() {
      resetView();
      eyes.addView( eyes3 );
      eyes.showNext();
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();

      progressDialog.dismiss();
   }
}
