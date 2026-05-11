package ru.mirea.aleksandrovnd.internalfilestorage;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private EditText editTextHistoryEvent;
    private Button buttonSaveFile;

    private static final String FILE_NAME = "history_event.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextHistoryEvent = findViewById(R.id.editTextHistoryEvent);
        buttonSaveFile = findViewById(R.id.buttonSaveFile);

        editTextHistoryEvent.setText("9 мая 1945 года — День Победы.");

        buttonSaveFile.setOnClickListener(view -> saveTextToInternalStorage());
    }

    private void saveTextToInternalStorage() {
        String text = editTextHistoryEvent.getText().toString();

        if (text.isEmpty()) {
            Toast.makeText(this, "Введите текст для сохранения", Toast.LENGTH_SHORT).show();
            return;
        }

        FileOutputStream outputStream = null;

        try {
            outputStream = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            Toast.makeText(this, "Файл сохранён во внутреннее хранилище", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка сохранения файла", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}