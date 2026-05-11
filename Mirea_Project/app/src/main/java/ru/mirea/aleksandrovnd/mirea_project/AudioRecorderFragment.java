package ru.mirea.aleksandrovnd.mirea_project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioRecorderFragment extends Fragment {

    private static final int REQUEST_AUDIO_PERMISSION = 300;
    private Button btnRecord, btnPlay;
    private TextView tvStatus;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String filePath;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private boolean isWork = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_recorder, container, false);
        btnRecord = view.findViewById(R.id.btn_record_audio);
        btnPlay = view.findViewById(R.id.btn_play_audio);
        tvStatus = view.findViewById(R.id.tv_audio_status);

        // Путь к файлу
        File recordingsDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Recordings");
        if (!recordingsDir.exists() && !recordingsDir.mkdirs()) {
            tvStatus.setText("Ошибка папки");
        }
        filePath = new File(recordingsDir, "voice_note.3gp").getAbsolutePath();

        requestPermissionsIfNeeded();

        btnRecord.setOnClickListener(v -> {
            if (!isWork) {
                Toast.makeText(getContext(), "Нет разрешения на запись", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isRecording) stopRecording();
            else startRecording();
        });

        btnPlay.setOnClickListener(v -> {
            if (!isWork) {
                Toast.makeText(getContext(), "Нет разрешения на запись", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isPlaying) stopPlaying();
            else startPlaying();
        });

        return view;
    }

    private void requestPermissionsIfNeeded() {
        List<String> needed = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.RECORD_AUDIO);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                needed.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        if (needed.isEmpty()) {
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    needed.toArray(new String[0]),
                    REQUEST_AUDIO_PERMISSION);
        }
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
            tvStatus.setText("Запись...");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка записи", Toast.LENGTH_SHORT).show();
            releaseRecorder();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        isRecording = false;
        btnRecord.setText("Записать");
        btnPlay.setEnabled(true);
        tvStatus.setText("Запись сохранена");
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
            player.setOnCompletionListener(mp -> stopPlaying());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
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
    public void onStop() {
        super.onStop();
        if (isRecording) stopRecording();
        if (isPlaying) stopPlaying();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_AUDIO_PERMISSION) {
            boolean allGranted = true;
            for (int res : grantResults) {
                if (res != PackageManager.PERMISSION_GRANTED) allGranted = false;
            }
            isWork = allGranted;
            if (!isWork) {
                Toast.makeText(getContext(), "Диктофон недоступен", Toast.LENGTH_LONG).show();
                btnRecord.setEnabled(false);
                btnPlay.setEnabled(false);
            }
        }
    }
}