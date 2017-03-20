package rest.rest.screenFilter.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import rest.rest.screenFilter.activity.ScreenFilterNotiActivity;
import rest.rest.screenFilter.application.SaveLightValue;


public class ScreenFilterService extends Service implements SensorEventListener {

    private View mView;

    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    SensorManager sensorManager;
    Sensor lightSensor;
    float lightValue;
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);

        mView = new MyLoadView(this);

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mView, mParams);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        ScreenFilterNotiActivity notiControl = new ScreenFilterNotiActivity(this);
        startForeground(3, notiControl.getNoti());

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
        mView = null;
        sensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightValue = event.values[0];
            SaveLightValue slv = (SaveLightValue) getApplication();
            slv.setLightValue(lightValue);
        }
    }

    public class MyLoadView extends View {

        private Paint mPaint;

        public MyLoadView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setTextSize(100);
            mPaint.setARGB(200, 10, 10, 10);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            SaveLightValue slv = (SaveLightValue) getApplication();
            lightValue = slv.getLightValue();
            String str = "현재 조도 : " + lightValue + " lux";
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
            Log.d("rest", str);
            if (lightValue <= 2.0)// TODO: 2017-01-03 조도 기준
                canvas.drawARGB(100, 255, 212, 0);

            handler.postDelayed(runnable, 1000);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SaveLightValue slv = (SaveLightValue) getApplication();
                lightValue = slv.getLightValue();

                invalidate();
                handler.postDelayed(this, 15000);
            }
        };


        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}