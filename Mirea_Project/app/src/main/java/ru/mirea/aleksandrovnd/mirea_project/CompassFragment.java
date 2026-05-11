package ru.mirea.aleksandrovnd.mirea_project;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CompassFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private ImageView arrowImage;
    private TextView directionText, logicText;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private float currentDegree = 0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compass, container, false);
        arrowImage = view.findViewById(R.id.arrow_image);
        directionText = view.findViewById(R.id.direction_text);
        logicText = view.findViewById(R.id.logic_text);

        sensorManager = (SensorManager) requireActivity().getSystemService(requireContext().SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Логическая подсказка
        logicText.setText("Мох на деревьях чаще растёт с северной стороны.\n" +
                "Проверьте: если стрелка показывает на север – вы смотрите на север.");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);
            float azimuthInRadians = orientation[0]; // радианы, от -π до π
            float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
            azimuthInDegrees = (azimuthInDegrees + 360) % 360; // нормализуем в 0..360

            // Поворачиваем стрелку (противоположное направление, чтобы указывала на север)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -azimuthInDegrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);
            arrowImage.startAnimation(ra);
            currentDegree = -azimuthInDegrees;

            // Текстовое направление
            String direction;
            if (azimuthInDegrees >= 337.5 || azimuthInDegrees < 22.5) direction = "СЕВЕР";
            else if (azimuthInDegrees >= 22.5 && azimuthInDegrees < 67.5) direction = "СЕВЕРО-ВОСТОК";
            else if (azimuthInDegrees >= 67.5 && azimuthInDegrees < 112.5) direction = "ВОСТОК";
            else if (azimuthInDegrees >= 112.5 && azimuthInDegrees < 157.5) direction = "ЮГО-ВОСТОК";
            else if (azimuthInDegrees >= 157.5 && azimuthInDegrees < 202.5) direction = "ЮГ";
            else if (azimuthInDegrees >= 202.5 && azimuthInDegrees < 247.5) direction = "ЮГО-ЗАПАД";
            else if (azimuthInDegrees >= 247.5 && azimuthInDegrees < 292.5) direction = "ЗАПАД";
            else direction = "СЕВЕРО-ЗАПАД";

            directionText.setText("Направление: " + direction + " (" + (int) azimuthInDegrees + "°)");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}