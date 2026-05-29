package ru.mirea.aleksandrovnd.yandexmaps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.runtime.image.ImageProvider;

import ru.mirea.aleksandrovnd.yandexmaps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {
    private static final int REQ_FOREGROUND_PERMS = 1001;
    private static final int REQ_BACKGROUND_PERM = 1002;

    private ActivityMainBinding binding;
    private MapView mapView;
    private UserLocationLayer userLocationLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mapView = binding.mapview;

        mapView.getMap().move(
                new CameraPosition(new Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        checkAndRequestForegroundLocation();
    }

    private boolean hasForegroundLocation() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkAndRequestForegroundLocation() {
        if (hasForegroundLocation()) {
            onForegroundLocationGranted();
        } else {
            boolean shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (shouldExplain) {
                new AlertDialog.Builder(this)
                        .setTitle("Доступ к местоположению")
                        .setMessage("Приложению нужен доступ к местоположению, чтобы показывать ваше местоположение на карте.")
                        .setPositiveButton("Разрешить", (d, w) -> requestForegroundPerms())
                        .setNegativeButton("Отмена", null)
                        .show();
            } else {
                requestForegroundPerms();
            }
        }
    }

    private void requestForegroundPerms() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQ_FOREGROUND_PERMS);
    }

    private void onForegroundLocationGranted() {
        loadUserLocationLayer();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            new AlertDialog.Builder(this)
                    .setTitle("Фоновый доступ к местоположению")
                    .setMessage("Если приложению требуется определять ваше местоположение в фоне (например, для трекинга), разрешите фоновый доступ. Это необязательно для показа текущей позиции на карте.")
                    .setPositiveButton("Запросить фон", (d, w) -> requestBackgroundPerm())
                    .setNegativeButton("Не сейчас", null)
                    .show();
        }
    }

    private void requestBackgroundPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Фоновый доступ уже предоставлен", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

            if (shouldExplain) {
                new AlertDialog.Builder(this)
                        .setTitle("Фоновый доступ")
                        .setMessage("Фоновый доступ нужен для работы в фоне. Разрешите, если хотите, чтобы приложение получало данные о местоположении, когда оно не на экране.")
                        .setPositiveButton("Открыть запрос", (d, w) -> ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQ_BACKGROUND_PERM))
                        .setNegativeButton("Отмена", null)
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        REQ_BACKGROUND_PERM);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_FOREGROUND_PERMS) {
            boolean granted = false;
            if (grantResults.length > 0) {
                for (int r : grantResults) {
                    if (r == PackageManager.PERMISSION_GRANTED) { granted = true; break; }
                }
            }
            if (granted) {
                onForegroundLocationGranted();
            } else {
                boolean showRationaleFine = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
                boolean showRationaleCoarse = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                boolean anyShouldShow = showRationaleFine || showRationaleCoarse;

                if (!anyShouldShow) {
                    new AlertDialog.Builder(this)
                            .setTitle("Разрешение заблокировано")
                            .setMessage("Доступ к местоположению был запрещён и больше не запрашивается системой. Откройте настройки приложения и разрешите доступ вручную.")
                            .setPositiveButton("Открыть настройки", (d, w) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                } else {
                    Toast.makeText(this, "Разрешение на местоположение не предоставлено", Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == REQ_BACKGROUND_PERM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Фоновый доступ предоставлен", Toast.LENGTH_SHORT).show();
            } else {
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                if (!showRationale) {
                    new AlertDialog.Builder(this)
                            .setTitle("Фоновый доступ заблокирован")
                            .setMessage("Фоновый доступ был запрещён. Вы можете включить его в настройках приложения.")
                            .setPositiveButton("Открыть настройки", (d, w) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                } else {
                    Toast.makeText(this, "Фоновый доступ не предоставлен", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadUserLocationLayer() {
        MapKitFactory.getInstance().resetLocationManagerToDefault();
        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));
        userLocationView.getArrow().setIcon(ImageProvider.fromResource(this, android.R.drawable.arrow_up_float));
        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();
        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(this, android.R.drawable.ic_menu_mylocation),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)
        );
        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
    }

    @Override public void onObjectRemoved(@NonNull UserLocationView userLocationView) {}
    @Override public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {}

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
}