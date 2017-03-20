package rest.rest.phoneUsageTimeNoti.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import rest.rest.R;
import rest.rest.notification.activity.MakeNotificationActivity;
import rest.rest.phoneUsageTimeNoti.activity.UsageTimeNotiActivity;
import rest.rest.phoneUsageTimeNoti.thread.phoneUsageTimeNotiThread;

public class phoneUsageTimeNotiService extends Service {

    NotificationManager Notifi_M;
    phoneUsageTimeNotiThread thread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UsageTimeNotiActivity notiControl = new UsageTimeNotiActivity(this);
        startForeground(2, notiControl.getNoti());

        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new phoneUsageTimeNotiThread(handler);
        thread.start();
        return START_STICKY;
    }


    public void onDestroy() {
        thread.stopForever();
        thread = null;
    }

    public void makeNotification(int i) {

        Resources res = getResources();

        Intent notificationIntent = new Intent();
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName(this, MakeNotificationActivity.class);
        notificationIntent.setComponent(cn);
        notificationIntent.putExtra("notificationId", 9999);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle("사용한지 " + i + "시간 지났습니다.")
                .setContentText("장시간 사용은 건강에 해로울 수 있습니다")
                .setTicker("사용한지 " + i + "시간 지났습니다.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }

    class myServiceHandler extends Handler {
        int i = 0;

        @Override
        public void handleMessage(android.os.Message msg) {

            if (i != 0)
                makeNotification(i);

            i++;
        }
    }
}