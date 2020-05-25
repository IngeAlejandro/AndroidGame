package com.example.final_juego;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

import android.content.Intent;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private int screenWidth, screenHeight;
    private ImageView up, down, left, right;
    private TextView txtScore, txtTime;
    int score = 0;
    private float upY, upX;
    private float downY, downX;
    private float leftY, leftX;
    private float rightY, rightX;
    private Timer timer = new Timer();
    private Handler handler= new Handler();
    int upPoints, downPoints, leftPoints, rightPoints;
    private SoundPool soundPool;
    int sound1, sound2, sound3, sound4;
    int upSound, downSound, leftSound, rightSound;
    boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        up = (ImageView) findViewById(R.id.imageView);
        down = (ImageView) findViewById(R.id.imageView2);
        right = (ImageView) findViewById(R.id.imageView3);
        left = (ImageView) findViewById(R.id.imageView4);

        txtScore = (TextView) findViewById(R.id.textScore);
        txtTime = (TextView) findViewById(R.id.textTime);

        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        screenWidth = size.x;

        up.setX(-80.0f);
        up.setY(-80.0f);
        down.setX(-80.0f);
        down.setY(screenHeight + 80.0f);
        right.setY(screenHeight + 80.0f);
        right.setX(screenWidth + 80.0f);
        left.setY(-80.0f);
        left.setX(-80.0f);

        showRandomUnit("up");
        showRandomUnit("down");
        showRandomUnit("right");
        showRandomUnit("left");

        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();

        sound1 = soundPool.load(getApplicationContext(), R.raw.militar, 1);
        sound2 = soundPool.load(getApplicationContext(), R.raw.horse, 1);
        sound3 = soundPool.load(getApplicationContext(), R.raw.yao, 1);
        sound4 = soundPool.load(getApplicationContext(), R.raw.female, 1);

        startTimer();
        startMoving();
    }

    public void showRandomUnit(String id){
        shuffleUnits();
        switch (id){
            case "up":
                up.setImageResource(unitsArray[0].getImage());
                upPoints = unitsArray[0].getValue();
                upSound = unitsArray[0].getSound();
                break;
            case "down":
                down.setImageResource(unitsArray[0].getImage());
                downPoints = unitsArray[0].getValue();
                downSound = unitsArray[0].getSound();
                break;
            case "right":
                right.setImageResource(unitsArray[0].getImage());
                rightPoints = unitsArray[0].getValue();
                rightSound = unitsArray[0].getSound();
                break;
            case "left":
                left.setImageResource(unitsArray[0].getImage());
                leftPoints = unitsArray[0].getValue();
                leftSound = unitsArray[0].getSound();
                break;
            default:
                break;
        }
    }

    Units u1 = new Units(R.drawable.military1, 1, 1);
    Units u2 = new Units(R.drawable.military2, 1, 1);
    Units u3 = new Units(R.drawable.military3, 1, 1);
    Units u4 = new Units(R.drawable.military4, 1, 1);
    Units u5 = new Units(R.drawable.military5, 1, 2);
    Units u6 = new Units(R.drawable.military6, 1, 1);
    Units u7 = new Units(R.drawable.vill1, -5, 4);
    Units u8 = new Units(R.drawable.vill2, -3, 3);

    Units[] unitsArray = new Units[]{u1,u2,u3,u4,u5,u6,u7,u8};

    public void shuffleUnits(){
        Collections.shuffle(Arrays.asList(unitsArray));
    }

    public void increaseScore(View view, String id){
        switch (id){
            case "up":
                soundPool.play(upSound, 1, 1, 0, 0, 1);
                score += upPoints;
                txtScore.setText("" + score);
                up.setOnClickListener(null);
                up.setVisibility(View.INVISIBLE);
                break;
            case "down":
                soundPool.play(downSound, 1, 1, 0, 0, 1);
                score += downPoints;
                txtScore.setText("" + score);
                down.setOnClickListener(null);
                down.setVisibility(View.INVISIBLE);
                break;
            case "right":
                soundPool.play(rightSound, 1, 1, 0,0, 1);
                score += rightPoints;
                txtScore.setText("" + score);
                right.setOnClickListener(null);
                right.setVisibility(View.INVISIBLE);
                break;
            case "left":
                soundPool.play(leftSound, 1, 1, 0,0, 1);
                score += leftPoints;
                txtScore.setText("" + score);
                left.setOnClickListener(null);
                left.setVisibility(View.INVISIBLE);
                break;
            default:
                break;

        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    public void startTimer(){
            CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txtTime.setText("" + millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    txtTime.setText("Time's up");
                    Intent resultScreen = new Intent(getApplicationContext(), ResultActivity.class);
                    resultScreen.putExtra("SCORE", score);
                    startActivity(resultScreen);
                    finish();
                }
            }.start();
        }

    public void startMoving(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changePos();
                    }
                });
            }
        }, 0, 10);
    }

    public void changePos(){
        //UP
        upY -= 10;
        if (up.getY() + up.getHeight() < 0){
            up.setVisibility(View.VISIBLE);
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseScore(v, "up");
                }
            });
            upX = (float) Math.floor(Math.random() * (screenWidth - up.getWidth()));
            upY = screenHeight + 100.0f;
            showRandomUnit("up");

        }
        up.setX(upX);
        up.setY(upY);

        //DOWN
        downY += 10;
        if (down.getY() > screenHeight){
            down.setVisibility(View.VISIBLE);
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseScore(v, "down");
                }
            });
            downX = (float) Math.floor(Math.random() * (screenWidth - down.getWidth()));
            downY = - 100.0f;
            showRandomUnit("down");
        }
        down.setX(downX);
        down.setY(downY);

        //RIGHT
        rightX += 10;
        if(right.getX() > screenWidth){
            right.setVisibility(View.VISIBLE);
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseScore(v, "right");
                }
            });
            rightY = (float) Math.floor(Math.random() * (screenHeight - right.getHeight()));
            rightX = - 100.0f;
            showRandomUnit("right");
        }
        right.setX(rightX);
        right.setY(rightY);

        //LEFT
        leftX -= 10;
        if(left.getX() + left.getWidth() < 0){
            left.setVisibility(View.VISIBLE);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseScore(v, "left");
                }
            });
            leftY = (float) Math.floor(Math.random() * (screenHeight - left.getHeight()));
            leftX = - screenWidth + 100.0f;
            showRandomUnit("left");
        }
        left.setX(leftX);
        left.setY(leftY);
    }
}
