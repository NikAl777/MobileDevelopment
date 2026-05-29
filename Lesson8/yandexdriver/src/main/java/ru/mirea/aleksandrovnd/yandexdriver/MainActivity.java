package ru.mirea.aleksandrovnd.yandexdriver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.Route;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.mapkit.transport.masstransit.TimeOptions;
import com.yandex.mapkit.transport.masstransit.Weight;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DrivingSession.DrivingRouteListener {

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private Point userLocation = null;
    private final Point redSquarePoint = new Point(55.753994, 37.622093);
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    private final int[] routeColors = {
            0xFF0000FF,
            0xFF00FF00,
            0xFFFF0000,
            0xFFFFA500
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);
        mapView.getMap().move(new CameraPosition(new Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Исправление 1: создание DrivingRouter через DirectionsFactory
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED);

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        } else {
            getUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            Toast.makeText(this, "Геолокация запрещена. Маршрут не будет построен.", Toast.LENGTH_LONG).show();
            userLocation = new Point(55.751574, 37.573856);
            buildRoute();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLocation = new Point(location.getLatitude(), location.getLongitude());
                        mapView.getMap().move(new CameraPosition(userLocation, 15.0f, 0.0f, 0.0f));
                    } else {
                        userLocation = new Point(55.751574, 37.573856);
                        Toast.makeText(this, "Не удалось определить местоположение, используется центр Москвы", Toast.LENGTH_SHORT).show();
                    }
                    buildRoute();
                })
                .addOnFailureListener(e -> {
                    userLocation = new Point(55.751574, 37.573856);
                    buildRoute();
                });
    }

    private void buildRoute() {
        userLocation = new Point(55.670005, 37.479894);
        if (userLocation == null) return;

        // Исправление 2: создание RequestPoint для начальной и конечной точек
        List<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(
                userLocation,
                RequestPointType.WAYPOINT,
                null,
                null,
                null));
        requestPoints.add(new RequestPoint(
                redSquarePoint,
                RequestPointType.WAYPOINT,
                null,
                null,
                null));

        // Исправление 3: настройка параметров маршрута (количество альтернатив)
        DrivingOptions drivingOptions = new DrivingOptions();
        drivingOptions.setRoutesCount(3); // Запрашиваем до 3 маршрутов

        // Исправление 4: добавление параметров транспортного средства
        VehicleOptions vehicleOptions = new VehicleOptions();

        // Исправление 5: запрос маршрутов с полным набором параметров
        drivingSession = drivingRouter.requestRoutes(
                requestPoints,
                drivingOptions,
                vehicleOptions,
                this
        );
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
        if (routes == null || routes.isEmpty()) {
            Toast.makeText(this, "Маршруты не найдены", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < routes.size() && i < routeColors.length; i++) {
            DrivingRoute route = routes.get(i);
            mapView.getMap().getMapObjects().addPolyline(route.getGeometry())
                    .setStrokeColor(routeColors[i]);
        }
        addMarkerToRedSquare();
    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        String msg = "Ошибка построения маршрута: " + error.toString();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void addMarkerToRedSquare() {
        com.yandex.mapkit.map.PlacemarkMapObject marker =
                mapView.getMap().getMapObjects().addPlacemark(
                        redSquarePoint,
                        ImageProvider.fromResource(this, android.R.drawable.btn_star)
                );
        marker.setDraggable(false);
        marker.addTapListener((mapObject, point) -> {
            Toast.makeText(MainActivity.this,
                    "Красная площадь — главная площадь Москвы.\nАдрес: Москва, Красная площадь",
                    Toast.LENGTH_LONG).show();
            return true;
        });
    }

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