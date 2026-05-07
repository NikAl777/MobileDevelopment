package ru.mirea.aleksandrovnd.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
public class MyLooper extends Thread {
    public Handler mHandler;
    private Handler mainHandler;

    public MyLooper(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                String text = data.getString("KEY");
                // Имитация задержки (возраст секунд)
                try {
                    int age = Integer.parseInt(data.getString("AGE"));
                    Thread.sleep(age * 1000L);
                } catch (Exception ignored) {}
                // Отправка результата обратно в главный поток
                Message reply = Message.obtain();
                Bundle replyData = new Bundle();
                replyData.putString("result", "Обработано: " + text);
                reply.setData(replyData);
                mainHandler.sendMessage(reply);
            }
        };
        Looper.loop();
    }
}
