package rest.rest.correctPostureNoti.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import rest.rest.MainActivity;
import rest.rest.R;

public class PostureNotiActivity extends Activity {

    Notification notification;
    private Context context;

    public PostureNotiActivity(Context parent) {
        this.context = parent;

        notification = new Notification(R.mipmap.ic_launcher, null, System.currentTimeMillis());
        NotificationManager nm = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews rView = new RemoteViews(parent.getPackageName(), R.layout.notification_correct_posture);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        notification.contentView = rView;
        notification.contentIntent = pIntent;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        nm.notify(1, notification);
    }

    public Notification getNoti() {
        return notification;
    }
}
