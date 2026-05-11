package ru.mirea.aleksandrovnd.mirea_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView imageView;
    private EditText editText;
    private Button takePhotoButton, applyTextButton;
    private Uri photoUri;
    private Bitmap originalBitmap;
    private Bitmap collageBitmap;
    private boolean isWork = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        imageView = view.findViewById(R.id.image_collage);
        editText = view.findViewById(R.id.edit_caption);
        takePhotoButton = view.findViewById(R.id.btn_take_photo);
        applyTextButton = view.findViewById(R.id.btn_apply_text);

        // Проверяем разрешение только на камеру (WRITE_EXTERNAL_STORAGE не нужно для API 29+)
        checkAndRequestPermissions();

        takePhotoButton.setOnClickListener(v -> {
            if (!isWork) {
                Toast.makeText(getContext(), "Нет разрешения на камеру", Toast.LENGTH_SHORT).show();
                return;
            }
            dispatchTakePictureIntent();
        });

        applyTextButton.setOnClickListener(v -> {
            if (originalBitmap == null) {
                Toast.makeText(getContext(), "Сначала сделайте фото", Toast.LENGTH_SHORT).show();
                return;
            }
            String text = editText.getText().toString().trim();
            if (text.isEmpty()) text = "Коллаж";
            collageBitmap = addTextToBitmap(originalBitmap, text);
            imageView.setImageBitmap(collageBitmap);
        });

        return view;
    }

    private void checkAndRequestPermissions() {
        List<String> needed = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.CAMERA);
        }
        // Для старых версий добавляем запись, но на API 29+ не требуется
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
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) == null) {
            Toast.makeText(getContext(), "Нет камеры", Toast.LENGTH_SHORT).show();
            return;
        }
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка создания файла", Toast.LENGTH_SHORT).show();
            return;
        }
        if (photoFile != null) {
            String authorities = requireContext().getPackageName() + ".fileprovider";
            photoUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir("Pictures");
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            throw new IOException("Cannot create directory");
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == getActivity().RESULT_OK) {
            try {
                originalBitmap = BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(photoUri));
                imageView.setImageBitmap(originalBitmap);
                collageBitmap = null;
                editText.setText("");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ошибка загрузки фото", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap addTextToBitmap(Bitmap src, String text) {
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(80);
        paint.setShadowLayer(2f, 2f, 2f, Color.BLACK);
        paint.setAntiAlias(true);
        float x = 50;
        float y = result.getHeight() - 100;
        canvas.drawText(text, x, y, paint);
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            boolean allGranted = true;
            for (int res : grantResults) {
                if (res != PackageManager.PERMISSION_GRANTED) allGranted = false;
            }
            isWork = allGranted;
            if (!isWork) {
                Toast.makeText(getContext(), "Камера недоступна", Toast.LENGTH_LONG).show();
            }
        }
    }
}