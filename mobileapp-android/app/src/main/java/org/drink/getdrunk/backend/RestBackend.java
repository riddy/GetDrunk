package org.drink.getdrunk.backend;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.drink.getdrunk.backend.model.CloseBy;
import org.drink.getdrunk.backend.model.Goal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.text.TextUtils;

import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

public class RestBackend {
   private static final Logger LOG = LoggerFactory.getLogger( RestBackend.class );
   private static final MediaType JSON = MediaType.parse( "application/json; charset=utf-8" );
   //   private static final String CLOUD_URL = "http://drink-4-fit.apps.bosch-iot-cloud.com";
   private static final String LOCAL_URL = "http://192.168.0.211:8080";
   private static RestBackend instance;
   private OkHttpClient client;
   private Gson gson;

   private RestBackend() {
      OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
      clientBuilder = TlsEnabler.trustPrivateCertificate( clientBuilder );
      clientBuilder.addNetworkInterceptor( new LoggingInterceptor() );
      clientBuilder.readTimeout( 20, TimeUnit.SECONDS );
      clientBuilder.writeTimeout( 20, TimeUnit.SECONDS );
      clientBuilder.connectTimeout( 20, TimeUnit.SECONDS );
      client = clientBuilder.build();

      gson = GsonFactory.buildGson();
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

   public Observable<Void> closeBy( CloseBy closeBy ) {
      return Observable.create( new Observable.OnSubscribe<Void>() {
         @Override
         public void call( Subscriber<? super Void> subscriber ) {
            Request request = new Request.Builder()
               .url( String.format( "%s/api/%s/closeby", LOCAL_URL, DeviceIdGenerator.getInstance().getId() ) )
               .put( RequestBody.create( JSON, gson.toJson( closeBy ) ) )
               .build();
            executeRequestWithoutResponse( subscriber, request );
         }
      } );
   }

   public Observable<Goal> goals() {
      return Observable.create( new Observable.OnSubscribe<String>() {
         @Override
         public void call( Subscriber<? super String> subscriber ) {
            Request request = new Request.Builder()
               .url( String.format( "%s/api/%s/goals", LOCAL_URL, DeviceIdGenerator.getInstance().getId() ) )
               .get()
               .build();
            executeRequest( subscriber, request );
         }
      } ).map( result -> gson.fromJson( result, Goal.class ) );
   }

   private void executeRequestWithoutResponse( Subscriber<? super Void> subscriber, Request request ) {
      client.newCall( request ).enqueue( new Callback() {
         @Override
         public void onFailure( Call call, IOException e ) {
            subscriber.onError( e );
         }

         @Override
         public void onResponse( Call call, Response response ) throws IOException {
            if ( response.isSuccessful() ) {
               subscriber.onNext( null );
               subscriber.onCompleted();
            }
            else {
               subscriber.onError( new RuntimeException( "non successful response" ) );
            }
         }
      } );
   }

   private void executeRequest( Subscriber<? super String> subscriber, Request request ) {
      client.newCall( request ).enqueue( new Callback() {
         @Override
         public void onFailure( Call call, IOException e ) {
            subscriber.onError( e );
         }

         @Override
         public void onResponse( Call call, Response response ) throws IOException {
            if ( response.isSuccessful() ) {
               ResponseBody responseBody = response.body();
               String responseBodyAsString = responseBody.string();
               responseBody.close();
               if ( TextUtils.isEmpty( responseBodyAsString ) ) {
                  subscriber.onError( new RuntimeException( "expected a backendresponse" ) );
               }
               LOG.debug( "Received from backend: {}", responseBodyAsString );
               subscriber.onNext( responseBodyAsString );
               subscriber.onCompleted();
            }
            else {
               subscriber.onError( new RuntimeException( "non successful response" ) );
            }
         }
      } );
   }
}
