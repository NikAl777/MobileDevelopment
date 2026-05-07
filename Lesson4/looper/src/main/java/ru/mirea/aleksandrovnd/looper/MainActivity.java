package ru.mirea.aleksandrovnd.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.aleksandrovnd.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализируем ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Обработчик для приёма результатов из фонового потока
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("result");
                Log.d("MainActivity", "Получен результат: " + result);
                binding.tvResult.setText(result);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        };

        // Запускаем фоновый поток с Looper
        myLooper = new MyLooper(mainThreadHandler);
        myLooper.start();

        // Обработчик нажатия кнопки
        binding.btnSend.setOnClickListener(v -> {
            String ageStr = binding.editTextAge.getText().toString().trim();
            String job = binding.etJob.getText().toString().trim();

            if (ageStr.isEmpty() || job.isEmpty()) {
                Toast.makeText(MainActivity.this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
                return;
            }

            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("KEY", job);
            bundle.putString("AGE", ageStr);
            msg.setData(bundle);
            myLooper.mHandler.sendMessage(msg);

            Toast.makeText(MainActivity.this, "Сообщение отправлено в фоновый поток", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Останавливаем Looper (для выхода из потока)
        if (myLooper != null && myLooper.mHandler != null) {
            myLooper.mHandler.getLooper().quit();
        }
    }
}