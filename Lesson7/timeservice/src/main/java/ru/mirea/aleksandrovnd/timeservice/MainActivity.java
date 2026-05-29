package ru.mirea.aleksandrovnd.timeservice;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.mirea.aleksandrovnd.timeservice.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final String host = "time.nist.gov";
    private final int port = 13;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(v -> {
            executor.execute(() -> {
                String timeResult = fetchTime();
                runOnUiThread(() -> {
                    if (timeResult != null) {
                        parseAndDisplay(timeResult);
                    } else {
                        binding.textView.setText("Ошибка соединения");
                    }
                });
            });
        });
    }

    private String fetchTime() {
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            BufferedReader reader = SocketUtils.getReader(socket);
            reader.readLine();
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    private void parseAndDisplay(String data) {
        // Пример строки: "55457 10-09-08 21:00:00 00 0 0 478.1 UTC(NIST) *"
        String[] parts = data.split(" ");
        if (parts.length >= 3) {
            String date = parts[1];
            String time = parts[2];
            binding.textView.setText("Дата: " + date + "\nВремя: " + time);
        }
    }
}