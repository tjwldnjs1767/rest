package rest.rest.reference.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import rest.rest.MainActivity;
import rest.rest.R;

public class ReferenceActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference);

        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);

        TextView toolBarName = (TextView) findViewById(R.id.toolbar_title);
        toolBarName.setText("개발자");

        TextView name = (TextView) findViewById(R.id.context);
        name.setText("김소연\t\tthdu8962@naver.com\n\n" +
                "박규리\t\tpkr91@naver.com\n\n" +
                "서지원\t\tmnm22@naver.com\n\n" +
                "송시은\t\t00sieun0608@naver.com");

        backBtn.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReferenceActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        goMenuActivity();
    }

    public void clickBackBtn(View v) {
        goMenuActivity();
    }

    public void goMenuActivity() {
        Intent intent = new Intent(ReferenceActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
