package ru.mirea.aleksandrovnd.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 200;
    private Button btnRecord, btnPlay;
    private TextView tvStatus;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String filePath;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private boolean isWork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecord = findViewById(R.id.btnRecord);
        btnPlay = findViewById(R.id.btnPlay);
        tvStatus = findViewById(R.id.tvStatus);

        // Путь к файлу: папка Music внутри приложения (не требует WRITE_EXTERNAL_STORAGE на API 29+)
        File recordingsDir = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Recordings");
        if (!recordingsDir.exists() && !recordingsDir.mkdirs()) {
            tvStatus.setText("Ошибка: не удалось создать папку");
        }
        filePath = new File(recordingsDir, "audio_record.3gp").getAbsolutePath();

        // --- Запрос разрешений (только нужные) ---
        List<String> neededPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.RECORD_AUDIO);
        }
        // WRITE_EXTERNAL_STORAGE только для Android 9 и ниже
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        if (neededPermissions.isEmpty()) {
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    neededPermissions.toArray(new String[0]),
                    REQUEST_CODE_PERMISSION);
        }

        // --- Кнопка записи ---
        btnRecord.setOnClickListener(v -> {
            if (!isWork) {
                Toast.makeText(this, "Нет разрешения на запись аудио", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        // --- Кнопка воспроизведения ---
        btnPlay.setOnClickListener(v -> {
            if (!isWork) {
                Toast.makeText(this, "Нет разрешения на запись аудио", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isPlaying) {
                stopPlaying();
            } else {
                startPlaying();
            }
        });
    }

    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(filePath);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
            recorder.start();

            isRecording = true;
            btnRecord.setText("Остановить запись");
            btnPlay.setEnabled(false);
            tvStatus.setText("Идёт запись...");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при записи: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            releaseRecorder();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            releaseRecorder();
        }
        isRecording = false;
        btnRecord.setText("Начать запись");
        btnPlay.setEnabled(true);
        tvStatus.setText("Запись сохранена: " + filePath);
        Toast.makeText(this, "Запись сохранена", Toast.LENGTH_SHORT).show();
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(filePath);
            player.prepare();
            player.start();

            isPlaying = true;
            btnPlay.setText("Остановить воспроизведение");
            btnRecord.setEnabled(false);
            tvStatus.setText("Воспроизведение...");

            player.setOnCompletionListener(mp -> {
                stopPlaying();
                tvStatus.setText("Воспроизведение завершено");
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при воспроизведении", Toast.LENGTH_SHORT).show();
            releasePlayer();
        }
    }

    private void stopPlaying() {
        if (player != null) {
            player.release();
            player = null;
        }
        isPlaying = false;
        btnPlay.setText("Воспроизвести");
        btnRecord.setEnabled(true);
        tvStatus.setText("Готово");
    }

    private void releaseRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
        if (isPlaying) {
            stopPlaying();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            boolean allGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            isWork = allGranted;
            if (!isWork) {
                Toast.makeText(this, "Разрешения не получены. Диктофон недоступен.", Toast.LENGTH_LONG).show();
                tvStatus.setText("Нет разрешений");
                btnRecord.setEnabled(false);
                btnPlay.setEnabled(false);
            } else {
                Toast.makeText(this, "Разрешения получены! Можете записывать.", Toast.LENGTH_SHORT).show();
                btnRecord.setEnabled(true);
            }
        }
    }
}