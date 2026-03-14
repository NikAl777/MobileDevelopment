package ru.mirea.aleksandrovnd.toastapp;

import android.os.Bundle;import android.view.View;import android.widget.Button;import android.widget.EditText;import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {



    private static final String STUDENT_NUMBER = "123456";
    private static final String GROUP_NAME = "БСБО-08-23";

    private EditText editTextInput;
    private Button buttonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        editTextInput = findViewById(R.id.editTextInput);
        buttonCount = findViewById(R.id.buttonCount);

        buttonCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editTextInput.getText().toString();
                int charCount = text.length();

                // Формирование сообщения
                String message = String.format(
                        "СТУДЕНТ № %s ГРУППА %s Количество символов - %d",
                        STUDENT_NUMBER,
                        GROUP_NAME,
                        charCount
                );

                // Отображение кастомного Toast
                Toast.makeText(
                        MainActivity.this,
                        message,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}