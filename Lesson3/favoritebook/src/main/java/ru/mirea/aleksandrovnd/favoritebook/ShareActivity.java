package ru.mirea.aleksandrovnd.favoritebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ru.mirea.aleksandrovnd.favoritebook.databinding.ActivityShareBinding;

public class ShareActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        editText = findViewById(R.id.editTextBook);
    }

    public void sendData(View view) {
        Intent data = new Intent();
        data.putExtra(MainActivity.USER_MESSAGE, editText.getText().toString());
        setResult(Activity.RESULT_OK, data);
        finish();
    }


}