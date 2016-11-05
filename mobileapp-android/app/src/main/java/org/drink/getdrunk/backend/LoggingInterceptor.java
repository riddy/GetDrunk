package org.drink.getdrunk.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class LoggingInterceptor implements Interceptor {
   private static final Logger LOG = LoggerFactory.getLogger( LoggingInterceptor.class );

   @Override
   public Response intercept( Chain chain ) throws IOException {
      Request request = chain.request();

      LOG.debug( "--> Sending {} {}\n{}", request.method(), request.url(), request.headers() );

      Response response = chain.proceed( request );

      LOG.debug( "<-- Received response {} {} {}\n{}",
         response.code(), response.request().method(), response.request().url(),
         new String( response.peekBody( 10000 ).bytes() ) );

      return response;
   }
}
