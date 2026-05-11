package ru.mirea.aleksandrovnd.mirea_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ru.mirea.aleksandrovnd.mirea_project.R;

public class ProfileFragment extends Fragment {

    private EditText editTextFullName;
    private EditText editTextGroup;
    private EditText editTextNumber;
    private EditText editTextLanguage;
    private EditText editTextGame;
    private Button buttonSaveProfile;

    private static final String PREF_NAME = "profile_settings";
    private static final String KEY_FULL_NAME = "FULL_NAME";
    private static final String KEY_GROUP = "GROUP";
    private static final String KEY_NUMBER = "NUMBER";
    private static final String KEY_LANGUAGE = "LANGUAGE";
    private static final String KEY_GAME = "Game";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editTextFullName = view.findViewById(R.id.editTextFullName);
        editTextGroup = view.findViewById(R.id.editTextGroup);
        editTextNumber = view.findViewById(R.id.editTextNumber);
        editTextLanguage = view.findViewById(R.id.editTextLanguage);
        editTextGame = view.findViewById(R.id.editTextGame);
        buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile);

        SharedPreferences preferences = requireContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        editTextFullName.setText(preferences.getString(KEY_FULL_NAME, "Alexandrov Nikita Dmitrievich"));
        editTextGroup.setText(preferences.getString(KEY_GROUP, "БСБО-08-23"));
        editTextNumber.setText(String.valueOf(preferences.getInt(KEY_NUMBER, 1)));
        editTextLanguage.setText(preferences.getString(KEY_LANGUAGE, "C#"));
        editTextGame.setText(preferences.getString(KEY_GAME, "Fortnite"));

        buttonSaveProfile.setOnClickListener(v -> {
            String fullName = editTextFullName.getText().toString().trim();
            String group = editTextGroup.getText().toString().trim();
            String numberText = editTextNumber.getText().toString().trim();
            String language = editTextLanguage.getText().toString().trim();
            String Game = editTextGame.getText().toString().trim();

            if (TextUtils.isEmpty(fullName) ||
                    TextUtils.isEmpty(group) ||
                    TextUtils.isEmpty(numberText) ||
                    TextUtils.isEmpty(language) ||
                    TextUtils.isEmpty(Game)) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            int number = Integer.parseInt(numberText);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_FULL_NAME, fullName);
            editor.putString(KEY_GROUP, group);
            editor.putInt(KEY_NUMBER, number);
            editor.putString(KEY_LANGUAGE, language);
            editor.putString(KEY_GAME, Game);
            editor.apply();

            Toast.makeText(requireContext(), "Профиль сохранён", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}