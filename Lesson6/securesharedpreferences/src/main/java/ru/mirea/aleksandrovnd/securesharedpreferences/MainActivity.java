package ru.mirea.aleksandrovnd.securesharedpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;
    private Button buttonSaveSecure;

    private static final String SECURE_PREFS_NAME = "secret_shared_prefs";
    private static final String KEY_SECURE = "secure";
    private static final String POET_NAME = "Jason Statham";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textViewResult);
        buttonSaveSecure = findViewById(R.id.buttonSaveSecure);

        loadSecureData();

        buttonSaveSecure.setOnClickListener(view -> {
            saveSecureData();
            loadSecureData();
        });
    }

    private SharedPreferences getSecureSharedPreferences()
            throws GeneralSecurityException, IOException {

        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

        return EncryptedSharedPreferences.create(
                SECURE_PREFS_NAME,
                mainKeyAlias,
                getBaseContext(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    private void saveSecureData() {
        try {
            SharedPreferences secureSharedPreferences = getSecureSharedPreferences();

            secureSharedPreferences
                    .edit()
                    .putString(KEY_SECURE, POET_NAME)
                    .apply();

            Toast.makeText(this, "Данные защищённо сохранены", Toast.LENGTH_SHORT).show();

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSecureData() {
        try {
            SharedPreferences secureSharedPreferences = getSecureSharedPreferences();

            String result = secureSharedPreferences.getString(KEY_SECURE, "Данные ещё не сохранены");

            textViewResult.setText("Значение из памяти: " + result);

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}