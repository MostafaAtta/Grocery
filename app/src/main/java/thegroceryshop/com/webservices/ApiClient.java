package thegroceryshop.com.webservices;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import thegroceryshop.com.BuildConfig;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;

public class ApiClient {


    // Development Server
    /*private static String DEVELOPMENT_URL = "https://octprod.thegroceryshop.com/web_services13/";
    public static String DEVELOPMENT_PAGE_URL = "https://octprod.thegroceryshop.com/contents/view?";*/

    private static String DEVELOPMENT_URL = "https://uat.thegroceryshop.com/web_services14/";
    public static String DEVELOPMENT_PAGE_URL = "https://uat.thegroceryshop.com/contents/view?";

    /*
     * Live URL
     * */

    // Production Server
    private static String PRODUCTION_URL = "https://uat.thegroceryshop.com/web_services14/";
    public static String PRODUCTION_PAGE_URL = "https://uat.thegroceryshop.com/contents/view?";

    private static Retrofit retrofit;

    /**
     * @param serviceClass interface class
     * @param context      context of activity
     * @param <S>          Type of Class
     * @return interface class object
     */
    public static <S> S createService1(Class<S> serviceClass, final Context context) {
        // initialize the request queue, the queue instance will be created when it is accessed for the first time
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

        builder.connectTimeout(100, TimeUnit.SECONDS);
        builder.readTimeout(100, TimeUnit.SECONDS);
        builder.writeTimeout(100, TimeUnit.SECONDS);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // add logging as last interceptor
        builder.addInterceptor(httpLoggingInterceptor); // <-- this is the important line!

        OkHttpClient okHttpClient = enableTls12OnPreLollipop(builder).build();
        //OkHttpClient okHttpClient = builder.build();

        Retrofit retrofit = null;
        if (BuildConfig.BUILD_TYPE.contentEquals("release")) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(PRODUCTION_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }else{
            retrofit = new Retrofit.Builder()
                    .baseUrl(OnlineMartApplication.isLiveUrl ? PRODUCTION_URL : DEVELOPMENT_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        try {
            builder.sslSocketFactory(new TLSSocketFactory(), new EasyX509TrustManager(null));
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, final Context context) {
        // initialize the request queue, the queue instance will be created when it is accessed for the first time
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

        httpClient.connectTimeout(100, TimeUnit.SECONDS);
        httpClient.readTimeout(100, TimeUnit.SECONDS);
        httpClient.writeTimeout(100, TimeUnit.SECONDS);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // add logging as last interceptor
        httpClient.addInterceptor(httpLoggingInterceptor); // <-- this is the important line!

        OkHttpClient okHttpClient = enableTls12OnPreLollipop(httpClient).build();
        //OkHttpClient okHttpClient = builder.build();

        Retrofit.Builder retrofitBuilder = null;
        if (BuildConfig.BUILD_TYPE.contentEquals("release")) {

            retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(PRODUCTION_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson));

        }else{
            retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(OnlineMartApplication.isLiveUrl ? PRODUCTION_URL : DEVELOPMENT_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson));
        }

        try{
            httpClient.sslSocketFactory(getSSLSocketFactory1(), new EasyX509TrustManager(null));
            httpClient.hostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = retrofitBuilder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static Retrofit getRequestQueue(String baseUrl) {
        // initialize the request queue, the queue instance will be created when it is accessed for the first time
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();


        builder.connectTimeout(100, TimeUnit.SECONDS);
        builder.readTimeout(100, TimeUnit.SECONDS);
        builder.writeTimeout(1000, TimeUnit.SECONDS);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // add logging as last interceptor
        builder.addInterceptor(httpLoggingInterceptor); // <-- this is the important line!
        //builder.networkInterceptors().add(httpLoggingInterceptor);

        //builder.authenticator(getBasicAuth("The_Grocery_Shop", "3T8I5%bS6H66"));
        OkHttpClient okHttpClient = enableTls12OnPreLollipop(builder).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        // refer from https://github.com/square/okhttp/issues/2372
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, new TrustManager[] { trustManager }, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()), trustManager);

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return client;
    }

    public static String getPageURL(){

        if (BuildConfig.BUILD_TYPE.contentEquals("release")) {
            return PRODUCTION_PAGE_URL;
        }else{
            return (OnlineMartApplication.isLiveUrl ? PRODUCTION_PAGE_URL : DEVELOPMENT_PAGE_URL);
        }
    }

    public static String getAPIURL(){

        if (BuildConfig.BUILD_TYPE.contentEquals("release")) {
            return PRODUCTION_URL;
        }else{
            return (OnlineMartApplication.isLiveUrl ? PRODUCTION_URL : DEVELOPMENT_URL);
        }
    }

    public static String getCallBackURL(){

        if (BuildConfig.BUILD_TYPE.contentEquals("release")) {
            return AppConstants.PROD_CALLBACK_URL;
        }else{
            return (OnlineMartApplication.isLiveUrl ? AppConstants.PROD_CALLBACK_URL : AppConstants.UAT_CALLBACK_URL);
        }
    }

    public static Retrofit createHelpDeskRetrofitService(Activity activity) {
        if (retrofit == null) {
            okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            builder.writeTimeout(60, TimeUnit.SECONDS);

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

            // Can be Level.BASIC, Level.HEADERS, or Level.BODY
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            // add logging as last interceptor
            builder.addInterceptor(httpLoggingInterceptor); // <-- this is the important line!

            builder.addInterceptor(new okhttp3.Interceptor() {
                @Override
                public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {

                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            });


            try {
                builder.sslSocketFactory(getSSLSocketFactory(), new EasyX509TrustManager(null));
                builder.hostnameVerifier(new HostnameVerifier() {

                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

                OkHttpClient client = builder.build();

                String BASE_URL = "http://helpdesk.thegroceryshop.com:8080/sdpapi/request/";
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*builder.sslSocketFactory(getSSLSocketFactory());
            builder.hostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient client = builder.build();

            String BASE_URL = "http://helpdesk.thegroceryshop.com:8080/sdpapi/request/";
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();*/

            /*try {
                try {
                    builder.sslSocketFactory(new TLSSocketFactory(), new EasyX509TrustManager(null));
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession sslSession) {
                    HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify("helpdesk", sslSession);
                }
            });

            okhttp3.OkHttpClient okHttpClient = builder.build();*/

            /*okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession sslSession) {
                    HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify("helpdesk", sslSession);
                }
            });*/


        }
        return retrofit;
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return sslSocketFactory;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }

    }

    public static SSLSocketFactory getSSLSocketFactory1() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return sslSocketFactory;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }

    }

    /*protected SSLSocketFactory getSSL(Context context) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = context.getResources().openRawResource(R.raw.client);
            Certificate ca = cf.generateCertificate(cert);
            cert.close();

            // creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            return new AdditionalKeyStore(keyStore);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
