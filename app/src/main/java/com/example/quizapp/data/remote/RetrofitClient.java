package com.example.quizapp.data.remote;

import com.example.quizapp.QuizApplication;
import com.example.quizapp.utils.TokenManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.1.11:5000/api/";
    private static ApiService apiService = null;

    public static ApiService getApi() {
        if (apiService == null) {
            TokenManager tokenManager = new TokenManager(QuizApplication.getInstance());

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    
                    // Add bypass header for localtunnel to avoid the warning screen
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Bypass-Tunnel-Reminder", "true");
                    
                    // Don't add token for auth routes
                    if (original.url().encodedPath().contains("/auth/")) {
                        return chain.proceed(requestBuilder.build());
                    }

                    String token = tokenManager.getAccessToken();
                    if (token != null) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }
                    return chain.proceed(requestBuilder.build());
                }
            };

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(authInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
