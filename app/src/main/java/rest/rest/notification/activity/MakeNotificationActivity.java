package rest.rest.notification.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import rest.rest.R;

public class MakeNotificationActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        CharSequence sequence = "전달 받은 값은 ";
        int id = 0;

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            sequence = "error";
        } else {
            id = extras.getInt("notificationId");
        }
        TextView textView = (TextView) findViewById(R.id.textView);
        sequence = sequence + " " + id;
        textView.setText(sequence);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        nm.cancel(id);
    }
}