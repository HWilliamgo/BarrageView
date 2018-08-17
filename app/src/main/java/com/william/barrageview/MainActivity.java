package com.william.barrageview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private BarrageView mBarrage;

    private Random random = new Random();
    private static final long DELAY_TIME = 200;

    private Runnable createBarrageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        createBarrageTask = new Runnable() {
            @Override
            public void run() {
                int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                int textSize = random.nextInt(50) + 50;
                mBarrage.send("你好", color, textSize);
                mBarrage.postDelayed(this, DELAY_TIME);
            }
        };
    }

    private void initView() {
        mBarrage = findViewById(R.id.barrage);
        mBarrage.setSpeed(8);

        Button mSendCenter = findViewById(R.id.sendCenter);
        mSendCenter.setOnClickListener(this);
        Button mClear = findViewById(R.id.clear);
        mClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendCenter:
                mBarrage.sendCenter("早上好！");
                break;
            case R.id.clear:
                mBarrage.clearScreen();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBarrage.post(createBarrageTask);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBarrage.removeCallbacks(createBarrageTask);
    }
}
