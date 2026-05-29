package ru.mirea.aleksandrovnd.mirea_project;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.aleksandrovnd.mirea_project.databinding.FragmentNetworkBinding;

public class NetworkFragment extends Fragment {

    private FragmentNetworkBinding binding;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final String WEATHER_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=55.75&longitude=37.62&current_weather=true";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentNetworkBinding.inflate(inflater, container, false);

        binding.loadButton.setOnClickListener(view -> loadWeather());

        return binding.getRoot();
    }

    private void loadWeather() {
        binding.resultTextView.setText("Загрузка данных из сети...");

        executorService.execute(() -> {
            try {
                String json = downloadUrl(WEATHER_URL);

                JSONObject root = new JSONObject(json);
                JSONObject currentWeather = root.getJSONObject("current_weather");

                String result =
                        "Источник: Open-Meteo\n" +
                                "Город: Москва\n" +
                                "Широта: 55.75\n" +
                                "Долгота: 37.62\n\n" +
                                "Температура: " + currentWeather.optDouble("temperature") + " °C\n" +
                                "Скорость ветра: " + currentWeather.optDouble("windspeed") + " км/ч\n" +
                                "Направление ветра: " + currentWeather.optDouble("winddirection") + "°\n" +
                                "Код погоды: " + currentWeather.optInt("weathercode") + "\n" +
                                "Время измерения: " + currentWeather.optString("time");

                mainHandler.post(() -> {
                    if (binding != null) {
                        binding.resultTextView.setText(result);
                    }
                });

            } catch (Exception exception) {
                mainHandler.post(() -> {
                    if (binding != null) {
                        binding.resultTextView.setText(
                                "Ошибка загрузки: " + exception.getMessage()
                        );
                    }
                });
            }
        });
    }

    private String downloadUrl(String address) throws Exception {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IllegalStateException("HTTP error: " + responseCode);
            }

            inputStream = connection.getInputStream();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            return outputStream.toString(StandardCharsets.UTF_8.name());

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
    }
}