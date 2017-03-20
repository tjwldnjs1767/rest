package rest.rest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import rest.rest.correctPostureNoti.service.FaceDetectionService;
import rest.rest.eyesStretching.activity.EyesStretchingActivity;
import rest.rest.phoneUsageTimeNoti.service.phoneUsageTimeNotiService;
import rest.rest.reference.activity.ReferenceActivity;
import rest.rest.screenFilter.service.ScreenFilterService;

public class MainActivity extends TabActivity implements TabHost.OnTabChangeListener {

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private TabHost tabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbarTitle();
        makeTab();
        clickfaceDetectionTab();
        clickEyesStretchingTab();
        clickTimeUsePhoneTab();
        clickScreenFilterTab();
    }

    private void setToolbarTitle() {
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("\"휴식\"");
    }

    public void clickReferenceBtn(View v) {
        Intent intent = new Intent(MainActivity.this, ReferenceActivity.class);
        startActivity(intent);
        finish();
    }

    private void clickfaceDetectionTab() {
        SwitchCompat cameraOnBtn = (SwitchCompat) findViewById(R.id.cameraOnBtn);

        cameraOnBtn.setChecked(isFaceDetectionServiceRunning());

        cameraOnBtn.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton cb, boolean isChecking) {
                if (isChecking)
                    startService(new Intent(MainActivity.this, FaceDetectionService.class));
                else
                    stopService(new Intent(MainActivity.this, FaceDetectionService.class));
            }
        });
    }

    private void clickEyesStretchingTab() {
        ImageButton startBtn = (ImageButton) findViewById(R.id.startBtn);

        startBtn.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EyesStretchingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void clickTimeUsePhoneTab() {
        SwitchCompat phoneUsageTimeNotiOnBtn = (SwitchCompat) findViewById(R.id.phoneUsageTimeNotiOnBtn);

        phoneUsageTimeNotiOnBtn.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton cb, boolean isChecking) {
                if (isChecking)
                    startService(new Intent(MainActivity.this, phoneUsageTimeNotiService.class));
                else
                    stopService(new Intent(MainActivity.this, phoneUsageTimeNotiService.class));

            }
        });
    }

    private void clickScreenFilterTab() {
        SwitchCompat screenFilterOnBtn = (SwitchCompat) findViewById(R.id.screenFilterOnBtn);

        screenFilterOnBtn.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton cb, boolean isChecking) {
                if (isChecking && !isScreenFilterServiceRunning())
                    startService(new Intent(MainActivity.this, ScreenFilterService.class));
                else
                    stopService(new Intent(MainActivity.this, ScreenFilterService.class));

            }
        });
    }

    public boolean isScreenFilterServiceRunning() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("rest.brightnessrecognition.ScreenFilterService".equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    private void makeTab() {
        tabHost = getTabHost();
        tabHost.setOnTabChangedListener(this);

        tabHost.addTab(tabHost.newTabSpec("correctPostureNotiTab")
                .setContent(R.id.correctPostureNotiTab)
                .setIndicator(null, getResources().getDrawable(R.drawable.sit)));

        tabHost.addTab(tabHost.newTabSpec("eyesStretchingTab")
                .setContent(R.id.eyesStretchingTab)
                .setIndicator(null, getResources().getDrawable(R.drawable.eyes)));

        tabHost.addTab(tabHost.newTabSpec("usePhoneTimeTab")
                .setContent(R.id.usePhoneTimeTab)
                .setIndicator(null, getResources().getDrawable(R.drawable.usetime)));

        tabHost.addTab(tabHost.newTabSpec("blueLightTab")
                .setContent(R.id.blueLightTab)
                .setIndicator(null, getResources().getDrawable(R.drawable.bluelight)));

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#BBD1E8"));
        }
        tabHost.getTabWidget().setCurrentTab(0);
        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#A9E2C5"));
    }

    @Override
    public void onTabChanged(String tabId) {
        TextView tabName = (TextView) findViewById(R.id.tabName);

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#BBD1E8"));
        }
        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#A9E2C5"));

        if (tabId.equals("correctPostureNotiTab")) {
            tabName.setText("   바른 자세 알림");
        } else if (tabId.equals("eyesStretchingTab")) {
            tabName.setText("   눈 스트레칭");
        } else if (tabId.equals("usePhoneTimeTab")) {
            tabName.setText("   핸드폰 사용시간 알림");
        } else {
            tabName.setText("   블루라이트");
        }
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (intervalTime >= 0 && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 뒤로가기를 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isFaceDetectionServiceRunning() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("rest.rest.correctPostureNot.service.FaceDetectionService".equals(service.service.getClassName()))
                return true;
        }
        return false;
    }


}