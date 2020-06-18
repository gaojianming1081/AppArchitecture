package com.lib.network;

import android.annotation.SuppressLint;
import android.util.Log;

import com.lib.network.factory.NetWorkConverterFactory;
import com.lib.network.factory.NobodyConverterFactory;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 网络请求管理类
 */
public abstract class NetworkManager<T> {
    private static final String TAG = NetworkManager.class.getSimpleName();

    private static final long CONNECT_TIME_OUT = 10;
    private static final long READ_TIME_OUT = 10;
    private static final long WRITE_TIME_OUT = 10;

    private static SSLSocketFactory socketFactory;

    private static X509TrustManager trustManager;

    protected T createNetworkApi(String baseURL) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(NetWorkConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createOkHttpClient())
                .build();
        return retrofit.create(getApiClass());
    }

    /**
     * Base Url
     */
//    public static Retrofit getRetrofit() {
//        if (baseUrlRetrofit == null) {
//            synchronized (OkHttpClientProvider.class) {
//                if (baseUrlRetrofit == null) {
//                    baseUrlRetrofit = new Retrofit.Builder()
//                            .baseUrl(RuntimeURLFactory.getBaseURL(BaseCleanApplication.getApplication()))
//                            .addConverterFactory(NobodyConverterFactory.create())
//                            .addConverterFactory(NetWorkConverterFactory.create())
//                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                            .client(createOkHttpClient())
//                            .build();
//                }
//            }
//        }
//        return baseUrlRetrofit;
//    }

    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        SSLSocketFactory socketFactory = createSSLSocketFactory();

        if (socketFactory != null) {
            //忽略https证书验证
            builder.sslSocketFactory(socketFactory, trustManager);
            builder.hostnameVerifier((hostname, session) -> true);
        }

        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);
        builder.addInterceptor(createLogInterceptor());
        //禁止重新连接
        builder.retryOnConnectionFailure(false);
        return builder.build();
    }

    @SuppressLint("TrustAllX509TrustManager")
    private static SSLSocketFactory createSSLSocketFactory() {
        if (socketFactory == null) {
            trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
                socketFactory = sslContext.getSocketFactory();
            } catch (Exception e) {
                trustManager = null;
                Log.e(TAG, "NetworkManager createSSLSocketFactory ", e);
            }

        }

        return socketFactory;
    }

    private static Interceptor createLogInterceptor(){
        return new HttpLoggingInterceptor(message -> Log.i(TAG, message))
                .setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public abstract void reset();

    protected abstract Class<T> getApiClass();

}