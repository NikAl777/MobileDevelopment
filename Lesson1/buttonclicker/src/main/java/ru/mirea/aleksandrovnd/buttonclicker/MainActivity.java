package ru.mirea.aleksandrovnd.buttonclicker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView textViewStudent;
    private CheckBox checkBox;
    private Button btnWhoAmI;
    private Button btnItIsNotMe;
    public void onbntItIsNotMeClick(View view)
    {
        // выводим сообщение
        textViewStudent.setText("А кто ты");
        checkBox.setText("Кек");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewStudent = findViewById(R.id.textViewStudent);
        checkBox=findViewById(R.id.checkBox);
        btnWhoAmI = findViewById(R.id.btnWhoAmI);
        btnItIsNotMe = findViewById(R.id.btnItIsNotMe);
        View.OnClickListener oclBtnWhoAmI = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewStudent.setText("Мой номер по списку № Х");
                checkBox.setText("Лол");
            }


        };
        btnWhoAmI.setOnClickListener(oclBtnWhoAmI);
    }
}