package ru.mirea.aleksandrovnd.data_thread;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import ru.mirea.aleksandrovnd.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "DataThread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStart.setOnClickListener(v -> {
            Log.d(TAG, "Кнопка нажата");
            // Очищаем TextView
            binding.tVResult.setText("");
            Log.d(TAG, "TextView очищен");

            // 1. runOnUiThread
            runOnUiThread(() -> {
                Log.d(TAG, "runOnUiThread выполняется");
                appendText("1. runOnUiThread (немедленно)");
            });

            // 2. post
            binding.tVResult.post(() -> {
                Log.d(TAG, "post выполняется");
                appendText("2. post (тоже немедленно в очередь UI)");
            });

            // 3. postDelayed
            binding.tVResult.postDelayed(() -> {
                Log.d(TAG, "postDelayed выполняется");
                appendText("3. postDelayed (выполнен через 2 секунды)");
            }, 2000);

            appendText("--- Запуск теста ---");
        });
    }

    private void appendText(String text) {
        Log.d(TAG, "appendText: " + text);
        String current = binding.tVResult.getText().toString();
        String newText;
        if (current.isEmpty()) {
            newText = text;
        } else {
            newText = current + "\n" + text;
        }
        binding.tVResult.setText(newText);
        Log.d(TAG, "TextView теперь содержит: " + newText);
    }
}