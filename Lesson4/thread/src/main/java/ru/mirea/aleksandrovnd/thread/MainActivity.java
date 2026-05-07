package ru.mirea.aleksandrovnd.thread;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.aleksandrovnd.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Правильный способ: inflate binding и установить его корень
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Обработчик кнопки расчёта среднего
        binding.calcBtn.setOnClickListener(v -> {
            String totalStr = binding.editTextTotal.getText().toString();
            String daysStr = binding.editTextDays.getText().toString();

            if (totalStr.isEmpty() || daysStr.isEmpty()) {
                binding.textViewResult.setText("Заполните все поля");
                return;
            }

            int totalPairs = Integer.parseInt(totalStr);
            int days = Integer.parseInt(daysStr);

            new Thread(() -> {
                double avg = (double) totalPairs / days;
                // Обновляем UI в главном потоке
                runOnUiThread(() -> binding.textViewResult.setText("Среднее пар в день: " + avg));
            }).start();
        });

        // Демонстрация имени и приоритета главного потока
        Thread mainThread = Thread.currentThread();
        String oldName = mainThread.getName();
        mainThread.setName("MyMainThread");
        Log.d("Main", "Имя до: " + oldName);
        Log.d("Main", "Имя после: " + mainThread.getName());
        Log.d("Main", "Приоритет: " + mainThread.getPriority());
        Log.d("Main", "Группа: " + mainThread.getThreadGroup());

        // Кнопка, которая вешает UI (для демонстрации ANR)
        binding.btnMirea.setOnClickListener(v -> {
            long endTime = System.currentTimeMillis() + 20 * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}