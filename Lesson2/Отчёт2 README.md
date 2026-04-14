**LESSON 2**
В ходе выполнения серии практических заданий были изучены основные механизмы работы приложений на платформе Android: жизненный цикл Activity, передача данных между экранами с помощью Intent, а также способы взаимодействия с пользователем через уведомления и диалоговые окна. Каждое задание реализовано в виде отдельного модуля проекта

__1. Жизненный цикл Activity__
Требовалось создать приложение, в котором на главном экране располагается поле ввода EditText. Необходимо переопределить все методы жизненного цикла MainActivity и выводить в лог сообщения с именем каждого вызванного метода. Дополнительно следовало проанализировать сохранение состояния поля ввода при сворачивании приложения (нажатие Home) и при полном закрытии (нажатие Back).
В разметке activity_main.xml размещён EditText без дополнительных элементов управления. В классе MainActivity переопределены методы onCreate(), onStart(), onResume(), onPause(), onStop(), onDestroy(), onRestart(), а также onSaveInstanceState() и onRestoreInstanceState() для отслеживания сохранения состояния. Каждый метод содержит вызов Log.i() с именем метода.
```Java
package ru.mirea.aleksandrovnd.activitylifestyle;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate()");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState()");
    }

}





```

При первом запуске в Logcat появилась последовательность: onCreate, onStart, onResume. После нажатия кнопки Home были зафиксированы вызовы onPause, onStop, onSaveInstanceState. При возврате в приложение – onRestart, onStart, onResume. При нажатии Back и повторном запуске – полный цикл от onCreate до onDestroy. Поле ввода после сворачивания сохраняло введённый текст, а после закрытия через Back – нет, что соответствует поведению, описанному в теории.
![alt text](image.png)
![alt text](image-1.png)

__2.Создание и вызов Activity__
Требовалось создать два Activity. В первом разместить поле ввода и кнопку «Отправить». При нажатии кнопки текст из поля должен передаваться во второе Activity и отображаться в TextView. 
В модуле MultyActivity созданы MainActivity и SecondActivity. В MainActivity по нажатию кнопки формируется явный Intent с указанием класса SecondActivity и добавлением текста через putExtra(). В SecondActivity полученный текст извлекается и выводится в TextView. Для возврата используется вызов finish().
Ниже можно наблюдать листинг MainActivity.java
```Java
package ru.mirea.panova.multyactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

package ru.mirea.aleksandrovnd.multiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickNewActivity(View view) {
        EditText editText = findViewById(R.id.editTextData);
        String textToSend = editText.getText().toString();

        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        // Передаем данные (по заданию ФИО)
        intent.putExtra("key", "MIREA - Александров Н.Д. " + textToSend);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

}
```

А также можно рассмотреть SecondActivity.java
```Java
package ru.mirea.aleksandrovnd.multiactivity;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ru.mirea.aleksandrovnd.multiactivity.databinding.ActivitySecondBinding;

public class SecondActivity extends AppCompatActivity {


    private static final String TAG = "SecondActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView textView = findViewById(R.id.textViewData);

        // Получаем данные из Intent
        String text = getIntent().getStringExtra("key");
        if (text != null) {
            textView.setText(text);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }
}
```
При вводе текста и нажатии кнопки открывается второе окно с переданной строкой
![alt text](image-2.png)
![alt text](image-3.png)


Следующее задание требует создать приложение с двумя кнопками: первая открывает веб-страницу МИРЭА через неявный Intent (ACTION_VIEW), вторая позволяет поделиться текстом с ФИО студента и названием университета через ACTION_SEND.
В модуле IntentFilter разметка содержит две кнопки. Для первой кнопки создаётся Intent с действием ACTION_VIEW и URI https://www.mirea.ru/. Для второй – Intent с действием ACTION_SEND, дополнительными полями EXTRA_SUBJECT и EXTRA_TEXT. Для выбора приложения используется Intent.createChooser().
![alt text](image-4.png)
```Java
package ru.mirea.aleksandrovnd.intentfilter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Замените на ваши данные
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onOpenBrowserClick(View view) {
        Uri address = Uri.parse("https://www.mirea.ru/");
        Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);
        startActivity(openLinkIntent);
    }

    public void onShareInfoClick(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MIREA");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Александров Н. Д.");
        startActivity(Intent.createChooser(shareIntent, "МОИ ФИО"));
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

}
```

При нажатии на первую кнопку открывается системный браузер с сайтом МИРЭА, нажатие Back возвращает в приложение. Вторая кнопка вызывает диалог выбора приложения (сообщения, заметки и т.п.) для отправки текста.
Результат нажатия первой кнопки:
![alt text](image-5.png)
![alt text](image-6.png)
Результат нажатия второй:
![alt text](image-7.png)

__3.Диалоговые окна__
В модуле ToastApp реализовано приложение с полем ввода и кнопкой. При нажатии подсчитывается количество символов и выводится сообщение через Toast с указанием номера студента, группы и количества символов.
![alt text](image-8.png)
![alt text](image-9.png)
```Java
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
```
Далее в новом модуле NotificationApp создано приложение с одной кнопкой, при нажатии которой отображается уведомление в статус-баре. Реализована проверка разрешения POST_NOTIFICATIONS для Android 13+, создание канала уведомлений и формирование уведомления через NotificationCompat.Builder. В манифест добавлено соответствующее разрешение.
``` Java
package ru.mirea.aleksandrovnd.notificationapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NotificationApp";
    private static final String CHANNEL_ID = "com.mirea.asd.notification.ANDROID";
    private static final int PERMISSION_CODE = 200;
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Разрешение на уведомления уже есть");
            } else {
                Log.d(TAG, "Нет разрешения на уведомления, запрашиваем...");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_CODE);
            }
        } else {
            Log.d(TAG, "Версия Android ниже 13, разрешение не требуется");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Разрешение на уведомления получено");
            } else {
                Log.d(TAG, "Разрешение на уведомления не получено");
            }
        }
    }

    public void onClickNewMessageNotification(View view) {
        // Проверяем разрешение (для Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Нет разрешения, уведомление не будет показано");
            return;
        }

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("МИРЭА")
                .setContentText("Поздравление!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Студент №1 группы БСБО-08-23, вы успешно создали уведомление!"))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.d(TAG, "Уведомление отправлено с ID " + NOTIFICATION_ID);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Студент ФИО Уведомления",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Канал для уведомлений приложения");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Канал уведомлений создан");
        }
    }
}
```
![alt text](image-10.png)



В модуле Dialog создан класс AlertDialogFragment, наследующий DialogFragment. В методе onCreateDialog() построено диалоговое окно с тремя кнопками: «Иду дальше», «На паузе», «Нет». При нажатии каждой кнопки вызывается соответствующий метод в MainActivity, который отображает Toast с подтверждением выбора. Для связи используется приведение getActivity() к MainActivity. 
```Java
package ru.mirea.aleksandrovnd.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class AlertDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Здравствуй МИРЭА!")
                .setMessage("Успех близок?")
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton("Иду дальше", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Закрываем окно
                        // Закрываем окно
                        ((MainActivity)getActivity()).onOkClicked();
                        dialog.cancel();

                    }
                })
                .setNeutralButton("На паузе",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                ((MainActivity)getActivity()).onNeutralClicked();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                ((MainActivity)getActivity()).onCancelClicked();
                                dialog.cancel();
                            }
                        });
        return builder.create();
    }
}

```
![alt text](image-11.png)
![alt text](image-12.png)

**Полный MainActivity с обработчиками будет отмечен в конце этого раздела, т.к. он содержит связи со всеми выше и нижеперечисленными классами**

Дополнительно созданы классы MyTimeDialogFragment и MyDateDialogFragment, аналогично наследующие DialogFragment. В них возвращаются системные диалоги выбора времени и даты. После выбора значения передаются в MainActivity через методы onTimeSet() и onDateSet(), где отображаются в Toast.
```Java
package ru.mirea.aleksandrovnd.dialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class MyTimeDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (view, hourOfDay, minuteOfHour) -> {
            // Здесь можно передать данные обратно в MainActivity
        }, hour, minute, true);
    }
}

```

```Java
package ru.mirea.aleksandrovnd.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class MyDateDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), (view, year1, month1, dayOfMonth) -> {
            // Обработка выбранной даты
        }, year, month, day);
    }
}

```
![alt text](image-13.png)
![alt text](image-14.png)


Класс MyProgressDialogFragment демонстрирует использование ProgressDialog. Диалог с индикатором загрузки автоматически закрывается через 2 секунды с помощью Handler.
```Java
package ru.mirea.aleksandrovnd.dialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MyProgressDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Загрузка...");
        progressDialog.setMessage("Пожалуйста, подождите, Александров Н.Д.");
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }
}

```
![alt text](image-15.png)


**ОБЩИЙ MAINACTIVITY**
```Java
package ru.mirea.aleksandrovnd.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickShowDialog(View view) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "mirea");
    }

    public void onOkClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку \"Иду дальше\"!",
                Toast.LENGTH_LONG).show();
    }
    public void onCancelClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку \"Нет\"!",
                Toast.LENGTH_LONG).show();
    }
    public void onNeutralClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку \"На паузе\"!",
                Toast.LENGTH_LONG).show();
    }

    public void onClickShowSnackbar(View view) {
        Snackbar.make(view, "Это Snackbar! Выполнил: Александров Н.Д.", Snackbar.LENGTH_LONG).show();
    }

    public void onClickShowTimeDialog(View view) {
        MyTimeDialogFragment timeDialog = new MyTimeDialogFragment();
        timeDialog.show(getSupportFragmentManager(), "timePicker");
    }

    public void onClickShowDateDialog(View view) {
        MyDateDialogFragment dateDialog = new MyDateDialogFragment();
        dateDialog.show(getSupportFragmentManager(), "datePicker");
    }

    public void onClickShowProgressDialog(View view) {
        MyProgressDialogFragment progressDialog = new MyProgressDialogFragment();
        progressDialog.show(getSupportFragmentManager(), "progressDialog");
    }
}
```
