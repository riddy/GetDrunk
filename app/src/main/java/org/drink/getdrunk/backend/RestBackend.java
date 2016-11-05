package org.drink.getdrunk.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

public class RestBackend {
   private static final Logger LOG = LoggerFactory.getLogger( RestBackend.class );
   private static RestBackend instance;
   private OkHttpClient client;

   private RestBackend() {
      client = new OkHttpClient();
   }

   public static RestBackend getInstance() {
      if ( instance == null ) {
         synchronized ( RestBackend.class ) {
            if ( instance == null ) {
               instance = new RestBackend();
            }
         }
      }
      return instance;
   }

   public Observable<Void> doSomething() {
      return Observable.create( new Observable.OnSubscribe<Void>() {
         @Override
         public void call( Subscriber<? super Void> subscriber ) {
            Request request = new Request.Builder()
               .url( "" )
               .get()
               .build();
            try {
               Response response = client.newCall( request ).execute();
               if ( response.isSuccessful() ) {
                  subscriber.onNext( null );
               }
               else {
                  subscriber.onError( new RuntimeException( "Response was not successful" ) );
               }
            }
            catch ( IOException e ) {
               subscriber.onError( e );
            }
            subscriber.onCompleted();
         }
      } );
   }
}
