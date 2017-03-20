package rest.rest.eyesStretching.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import rest.rest.MainActivity;
import rest.rest.R;

public class EyesStretchingActivity extends Activity {

    String[] eyesStretchingArr = new String[10];
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eyes_stretching);

        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        TextView toolBarName = (TextView) findViewById(R.id.toolbar_title);
        toolBarName.setText("눈 스트레칭");
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        final TextView textView = (TextView) findViewById(R.id.textView);

        eyesStretchingArr[0] = "초점을 맟추지 않은 채로 가볍게 위를 본다";
        eyesStretchingArr[1] = "눈을 감은 채로 숫자를 센다";
        eyesStretchingArr[2] = "눈을 최대한 부릅뜨고 숫자를 센다";
        eyesStretchingArr[3] = "양쪽 시선을 우측으로 고정한다";
        eyesStretchingArr[4] = "양쪽 시선을 좌측으로 고정한다";
        eyesStretchingArr[5] = "양쪽 시선을 위쪽으로 고정한다";
        eyesStretchingArr[6] = "양쪽 시선을 아래쪽으로 고정한다";
        eyesStretchingArr[7] = "양 집게 손가락을 이용해 눈 주위를 지그시 누르면서\n\n안쪽에서 바깥쪽으로 나선을 그리듯이 문질러준다";
        eyesStretchingArr[8] = "양손 집게와 중지, 약지 세 손가락으로 눈꺼풀 위를 가볍게 누른다.";

        backBtn.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EyesStretchingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        new CountDownTimer(30000, 3000) {

            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText(eyesStretchingArr[i]);
                vibrator.vibrate(500);
                i++;
            }

            @Override
            public void onFinish() {
                textView.setText("끝~~");
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EyesStretchingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
