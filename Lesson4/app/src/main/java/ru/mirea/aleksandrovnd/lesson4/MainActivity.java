package ru.mirea.aleksandrovnd.lesson4;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.aleksandrovnd.lesson4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int pairs = Integer.parseInt(binding.editPairs.getText().toString());
                            int days = Integer.parseInt(binding.editDays.getText().toString());

                            // Имитация долгих вычислений
                            Thread.sleep(2000);

                            final double result = (double) pairs / days;

                            // Возврат в UI поток для обновления интерфейса
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvResult.setText(String.format("Среднее кол-во пар в день: %.2f", result));
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}