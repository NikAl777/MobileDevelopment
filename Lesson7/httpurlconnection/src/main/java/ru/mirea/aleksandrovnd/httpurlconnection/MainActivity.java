package ru.mirea.aleksandrovnd.httpurlconnection;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.mirea.aleksandrovnd.httpurlconnection.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String TAG = "HttpURLConnectionExample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnFetch.setOnClickListener(this::onFetchClicked);
    }

    private void onFetchClicked(View v) {
        if (!NetworkUtils.isConnected(this)) {
            binding.tvStatus.setText("Статус: Нет интернета");
            return;
        }

        binding.tvStatus.setText("Статус: Загружаем...");
        binding.tvIp.setText("IP: ...");
        binding.tvCity.setText("Город: ...");
        binding.tvRegion.setText("Регион: ...");
        binding.tvCoords.setText("Координаты: ...");
        binding.tvWeather.setText("Погода: ...");

        executor.execute(() -> {
            try {
                String ipInfoJson = downloadUrl("https://ipinfo.io/json");
                Log.d(TAG, "ipinfo response: " + ipInfoJson);
                JSONObject ipObj = new JSONObject(ipInfoJson);

                final String ip = ipObj.optString("ip", "N/A");
                final String city = ipObj.optString("city", "N/A");
                final String region = ipObj.optString("region", "N/A");
                final String loc = ipObj.optString("loc", ""); // "lat,lon"

                String latitude = "";
                String longitude = "";
                if (!loc.isEmpty() && loc.contains(",")) {
                    String[] parts = loc.split(",");
                    latitude = parts[0];
                    longitude = parts[1];
                }

                String weatherText = "Нет данных";
                if (!latitude.isEmpty() && !longitude.isEmpty()) {
                    String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude="
                            + latitude + "&longitude=" + longitude + "&current_weather=true";
                    String weatherJson = downloadUrl(weatherUrl);
                    Log.d(TAG, "weather response: " + weatherJson);
                    try {
                        JSONObject wObj = new JSONObject(weatherJson);
                        JSONObject current = wObj.optJSONObject("current_weather");
                        if (current != null) {
                            double temperature = current.optDouble("temperature", Double.NaN);
                            double windspeed = current.optDouble("windspeed", Double.NaN);
                            String weatherStr = "Температура: " + (Double.isNaN(temperature) ? "N/A" : temperature + " °C")
                                    + "\nСкорость ветра: " + (Double.isNaN(windspeed) ? "N/A" : windspeed + " m/s");
                            weatherText = weatherStr;
                        } else {
                            weatherText = "Ошибка: current_weather отсутствует";
                        }
                    } catch (JSONException je) {
                        weatherText = "Ошибка разбора погоды: " + je.getMessage();
                    }
                } else {
                    weatherText = "Координаты не доступны";
                }

                final String finalLatitude = latitude;
                final String finalLongitude = longitude;
                final String finalWeatherText = weatherText;

                runOnUiThread(() -> {
                    binding.tvStatus.setText("Статус: Готово");
                    binding.tvIp.setText("IP: " + ip);
                    binding.tvCity.setText("Город: " + city);
                    binding.tvRegion.setText("Регион: " + region);
                    binding.tvCoords.setText("Координаты: " + finalLatitude + ", " + finalLongitude);
                    binding.tvWeather.setText(finalWeatherText);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error in fetching data", e);
                runOnUiThread(() -> {
                    binding.tvStatus.setText("Статус: Ошибка - " + e.getMessage());
                    binding.tvIp.setText("IP: -");
                    binding.tvCity.setText("Город: -");
                    binding.tvRegion.setText("Регион: -");
                    binding.tvCoords.setText("Координаты: -");
                    binding.tvWeather.setText("Погода: -");
                });
            }
        });
    }

    private String downloadUrl(String urlString) throws IOException {
        HttpURLConnection conn = null;
        InputStream in = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode + " - " + conn.getResponseMessage());
            }

            in = conn.getInputStream();
            return readStream(in);
        } finally {
            if (in != null) try { in.close(); } catch (IOException ignored) {}
            if (conn != null) conn.disconnect();
        }
    }

    private String readStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}