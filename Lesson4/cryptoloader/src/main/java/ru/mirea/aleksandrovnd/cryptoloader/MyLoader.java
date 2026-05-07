package ru.mirea.aleksandrovnd.cryptoloader;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {
    public static final String ARG_WORD = "crypt";
    private byte[] cryptText;
    private byte[] keyBytes;

    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);
        if (args != null) {
            cryptText = args.getByteArray(ARG_WORD);
            keyBytes = args.getByteArray("key");
        }
    }

    @Override
    public String loadInBackground() {
        try {
            SecretKey originalKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] decrypted = cipher.doFinal(cryptText);
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка дешифрования";
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad(); // запустить loadInBackground
    }
}