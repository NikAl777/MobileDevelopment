package ru.mirea.aleksandrovnd.mirea_project;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.mirea.aleksandrovnd.mirea_project.R;

public class FileWorkFragment extends Fragment {

    private static final String FILE_NAME = "notes.txt";

    private TextView textViewFileNotes;
    private FloatingActionButton fabAddNote;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_file_work, container, false);

        textViewFileNotes = view.findViewById(R.id.textViewFileNotes);
        fabAddNote = view.findViewById(R.id.fabAddNote);

        loadNotesFromFile();

        fabAddNote.setOnClickListener(v -> showAddNoteDialog());

        return view;
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Новая заметка");

        final EditText input = new EditText(requireContext());
        input.setHint("Введите текст заметки");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String noteText = input.getText().toString().trim();

            if (noteText.isEmpty()) {
                Toast.makeText(requireContext(), "Пустую заметку сохранить нельзя", Toast.LENGTH_SHORT).show();
                return;
            }

            saveNoteToFile(noteText);
            loadNotesFromFile();
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveNoteToFile(String noteText) {
        try {
            FileOutputStream outputStream = requireContext()
                    .openFileOutput(FILE_NAME, Context.MODE_APPEND);

            String textToSave = noteText + "\n";
            outputStream.write(textToSave.getBytes());
            outputStream.close();

            Toast.makeText(requireContext(), "Заметка сохранена в файл", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ошибка записи файла", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNotesFromFile() {
        FileInputStream fin = null;
        try {
            fin = requireContext().openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
            textViewFileNotes.setText(text);

        } catch (IOException e) {
            textViewFileNotes.setText("Файл заметок пока пуст");
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}