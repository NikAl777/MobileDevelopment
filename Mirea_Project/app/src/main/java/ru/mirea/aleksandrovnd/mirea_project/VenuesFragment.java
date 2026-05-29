package ru.mirea.aleksandrovnd.mirea_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import java.util.ArrayList;

import ru.mirea.aleksandrovnd.mirea_project.R;

public class VenuesFragment extends Fragment {

    private MapView mapView;
    private final ArrayList<GeoPoint> placePoints = new ArrayList<>();

    public VenuesFragment() {
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_venues, container, false);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        Configuration.getInstance().load(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
        );

        mapView = view.findViewById(R.id.mapPlaces);

        setupMap();
        addCompass();
        addScaleBar();
        addPlaces();

        view.findViewById(R.id.buttonShowAllPlaces).setOnClickListener(v -> showAllPlaces());

        return view;
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setZoomRounding(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(12.0);

        GeoPoint center = new GeoPoint(55.751574, 37.617635);
        mapController.setCenter(center);
    }

    private void addCompass() {
        CompassOverlay compassOverlay = new CompassOverlay(
                requireContext(),
                new InternalCompassOrientationProvider(requireContext()),
                mapView
        );

        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
    }

    private void addScaleBar() {
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(300, 20);
        mapView.getOverlays().add(scaleBarOverlay);
    }

    private void addPlaces() {
        addPlaceMarker(
                55.752120,
                37.592370,
                "Кофейня на Арбате",
                "Москва, улица Арбат, 19",
                "Кофейня с десертами и горячими напитками. Подходит для встречи после прогулки."
        );

        addPlaceMarker(
                55.731190,
                37.603810,
                "Пиццерия у парка Горького",
                "Москва, улица Крымский Вал, 9",
                "Заведение с пиццей, салатами и напитками рядом с парковой зоной."
        );

        addPlaceMarker(
                55.764220,
                37.605590,
                "Бургерная на Тверской",
                "Москва, Тверская улица, 18",
                "Фастфуд-заведение для быстрого обеда в центре города."
        );

        addPlaceMarker(
                55.765490,
                37.638900,
                "Кафе на Чистых прудах",
                "Москва, район Чистые пруды",
                "Спокойное кафе рядом с бульваром. Подходит для встречи и отдыха."
        );

        addPlaceMarker(
                55.731610,
                37.636520,
                "Столовая у Павелецкой",
                "Москва, район Павелецкой площади",
                "Недорогое место для полноценного обеда: горячие блюда, салаты и напитки."
        );

        mapView.invalidate();
    }

    private void addPlaceMarker(
            double latitude,
            double longitude,
            String title,
            String address,
            String description
    ) {
        GeoPoint point = new GeoPoint(latitude, longitude);
        placePoints.add(point);

        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setSnippet(address + "\n" + description);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setOnMarkerClickListener((clickedMarker, clickedMapView) -> {
            clickedMarker.showInfoWindow();

            Toast.makeText(
                    requireContext(),
                    title + "\n" + address + "\n" + description,
                    Toast.LENGTH_LONG
            ).show();

            return true;
        });

        mapView.getOverlays().add(marker);
    }

    private void showAllPlaces() {
        if (placePoints.isEmpty()) {
            return;
        }

        double north = placePoints.get(0).getLatitude();
        double south = placePoints.get(0).getLatitude();
        double east = placePoints.get(0).getLongitude();
        double west = placePoints.get(0).getLongitude();

        for (GeoPoint point : placePoints) {
            north = Math.max(north, point.getLatitude());
            south = Math.min(south, point.getLatitude());
            east = Math.max(east, point.getLongitude());
            west = Math.min(west, point.getLongitude());
        }

        BoundingBox boundingBox = new BoundingBox(
                north + 0.01,
                east + 0.01,
                south - 0.01,
                west - 0.01
        );

        mapView.post(() -> mapView.zoomToBoundingBox(boundingBox, true));

        Toast.makeText(
                requireContext(),
                "Показаны все заведения на карте",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mapView != null) {
            Configuration.getInstance().load(
                    requireContext(),
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
            );

            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mapView != null) {
            Configuration.getInstance().save(
                    requireContext(),
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
            );

            mapView.onPause();
        }
    }
}