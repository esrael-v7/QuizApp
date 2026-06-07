package com.example.quizapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {

    private static final String PREF_NAME = "quiz_secure_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_FULL_NAME = "full_name";

    private static volatile TokenManager instance;
    private SharedPreferences sharedPreferences;

    public static TokenManager getInstance(Context context) {
        if (instance == null) {
            synchronized (TokenManager.class) {
                if (instance == null) {
                    instance = new TokenManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private TokenManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            // FALLBACK: If Encryption fails, use standard SharedPreferences so the app still works!
            sharedPreferences = context.getSharedPreferences(PREF_NAME + "_backup", Context.MODE_PRIVATE);
        }
    }




    public void saveTokens(String access, String refresh, int userId, String role, String name) {
        if (sharedPreferences != null) {
            sharedPreferences.edit()
                    .putString(KEY_ACCESS_TOKEN, access)
                    .putString(KEY_REFRESH_TOKEN, refresh)
                    .putInt(KEY_USER_ID, userId)
                    .putString(KEY_ROLE, role)
                    .putString(KEY_FULL_NAME, name)
                    .commit(); // Changed to commit() for instant save
        }
    }


    public String getAccessToken() {
        return sharedPreferences != null ? sharedPreferences.getString(KEY_ACCESS_TOKEN, null) : null;
    }

    public String getRefreshToken() {
        return sharedPreferences != null ? sharedPreferences.getString(KEY_REFRESH_TOKEN, null) : null;
    }

    public int getUserId() {
        return sharedPreferences != null ? sharedPreferences.getInt(KEY_USER_ID, -1) : -1;
    }

    public String getRole() {
        return sharedPreferences != null ? sharedPreferences.getString(KEY_ROLE, "user") : "user";
    }

    public String getFullName() {
        return sharedPreferences != null ? sharedPreferences.getString(KEY_FULL_NAME, "") : "";
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void clear() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().apply();
        }
    }
}
