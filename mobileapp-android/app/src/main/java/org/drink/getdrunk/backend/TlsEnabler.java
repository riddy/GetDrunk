package org.drink.getdrunk.backend;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;

public class TlsEnabler {

   private static final Logger LOG = LoggerFactory.getLogger( TlsEnabler.class );

   private TlsEnabler() {
   } // protect class from being instantiated

   ///////////////////////////////////////////////////////////////////////////////////////////////
   // HTTPS accept unsigned certificates
   ///////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * For test environments like I and Q we have https connections with self signed certificates
    * Flavours using those connections must have a trust store in the bundle that contains the
    * certificate to be used and trusted.
    * <p>
    * This method configures an OkHTTPClient to trust these certificate if available.
    * <p>
    * Aligned to this documentation
    * http://developer.android.com/training/articles/security-ssl.html#HttpsExample we are following
    * this example
    * http://stackoverflow.com/questions/21047414/javax-net-ssl-sslhandshakeexception-java
    * -security-cert-certpathvalidatorexcepti
    * <p>
    * The provider's jar bcprov-jdk16-1.46.jar can be found on maven:
    * http://repo1.maven.org/maven2/org/bouncycastle/bcprov-jdk16/1.46/
    */
   public static OkHttpClient.Builder trustPrivateCertificate( OkHttpClient.Builder okHttpClientBuilder ) {

      // Temporary fix for the upcoming field test
      // We will accept all certificates
      // !!!! DANGEROUS - THIS ACCEPPTS ALL CERTS
      // ToDo: Change it in a way that our self signed certificate is accepted.

      try {
         X509TrustManager trustManager = new X509TrustManager() {
            public void checkClientTrusted( X509Certificate[] chain, String authType ) {
            }

            public void checkServerTrusted( X509Certificate[] chain, String authType ) {
            }

            public X509Certificate[] getAcceptedIssuers() {
               return new X509Certificate[] {};
            }
         };
         SSLContext sslContext = SSLContext.getInstance( "TLS" );
         sslContext.init( null, new TrustManager[] {
            trustManager
         }, null );

         // Make the http client use our sslContext
         okHttpClientBuilder.sslSocketFactory( sslContext.getSocketFactory(), trustManager );
      }
      catch ( NoSuchAlgorithmException e ) {
         LOG.error( "No such algorithm", e );
      }
      catch ( KeyManagementException e ) {
         LOG.error( "Error managing TSL keys", e );
      }

      return okHttpClientBuilder;
   }
}
