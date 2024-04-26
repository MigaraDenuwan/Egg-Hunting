package com.example.mobilegame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import java.util.Random;
import android.os.Handler;

public class GameView extends View {
    int devWidth, devHeight;
    Bitmap net, bird, egg;
    Handler handler;
    Runnable runnable;
    long UPDATE_MILLIS = 30;
    int birdX, birdY;
    int netX, netY;
    int eggX, eggY;
    Random random;
    boolean eggAnimation = false;
    int points = 0;
    float TEXT_SIZE = 120;
    Paint textPaint;
    Paint healthPaint;
    int life = 3;
    Context context;
    int birdSpeed;
    MediaPlayer mpScore, mpHit, mpFly, mpCrack;

    public GameView(Context context) {
        super(context);
        this.context = context;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        devWidth = size.x;
        devHeight = size.y;
        net = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        egg = BitmapFactory.decodeResource(getResources(), R.drawable.egg);
        bird = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        random = new Random();
        birdX = devWidth + random.nextInt(300);
        birdY = random.nextInt(600);
        eggX = birdX + bird.getWidth() - 220;
        eggY = birdY + bird.getHeight() - 100;
        textPaint = new Paint();
        textPaint.setColor(Color.rgb(255,0,0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint = new Paint();
        healthPaint.setColor(Color.GREEN);
        birdSpeed = 21 + random.nextInt(30);
        netX = devWidth/2 - net.getWidth()/2;
        netY = devHeight - net.getHeight() - 40;
        mpScore = MediaPlayer.create(context, R.raw.score);
        mpFly = MediaPlayer.create(context, R.raw.fly);
        mpHit = MediaPlayer.create(context, R.raw.wall_hit);
        mpCrack = MediaPlayer.create(context, R.raw.egg_crack);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.sky);
        Rect srcRect = new Rect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
        Rect destRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawBitmap(backgroundImage, srcRect, destRect, null);

        if(eggAnimation == false){
            birdX -= birdSpeed;
            eggX -= birdSpeed;
        }
        if(birdX <= -bird.getWidth()){
            if(mpHit != null){
                mpHit.start();
            }
            birdX = devWidth + random.nextInt(300);
            birdY = random.nextInt(600);
            eggX = birdX + bird.getWidth() - 220;
            eggY = birdY + bird.getHeight() - 100;
            birdSpeed = 21 + random.nextInt(30);
            netX = bird.getWidth() + random.nextInt(devWidth - 2*bird.getWidth());
            life--;
            if(life == 0){
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra( "points", points);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        }
        if(eggAnimation){
            eggY += 40;
        }
        if(eggAnimation
        && (eggX + egg.getWidth() - 90 >= netX)
        && (eggX + 100 <= netX + net.getWidth())
        && (eggY + egg.getHeight() >= (devHeight - net.getHeight() + 60))
        && eggY <= devHeight){
            if (mpScore != null) {
                mpScore.start();
            }
            birdX = devWidth + random.nextInt(300);
            birdY = random.nextInt(600);
            eggX = birdX + bird.getWidth() - 220;
            eggY = birdY + bird.getHeight() - 100;
            birdSpeed = 21 + random.nextInt(30);
            points++;
            netX = bird.getWidth() + random.nextInt(devWidth - 2*bird.getWidth());
            eggAnimation = false;
        }
        if (eggAnimation && (eggY + egg.getHeight() >= devHeight)){
            if (mpCrack != null){
                mpCrack.start();
            }
            life--;
            if(life == 0){
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra( "points", points);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
            birdX = devWidth + random.nextInt(300);
            birdY = random.nextInt(600);
            eggX = birdX + bird.getWidth() - 220;
            eggY = birdY + bird.getHeight() - 100;
            netX = bird.getWidth() + random.nextInt(devWidth - 2*bird.getWidth());
            eggAnimation = false;
        }
        canvas.drawBitmap(net, netX, netY, null);
        canvas.drawBitmap(bird, birdX, birdY, null);
        canvas.drawBitmap(egg, eggX, eggY, null);
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
        if (life == 2){
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }
        canvas.drawRect(devWidth - 200, 30, devWidth - 200 + 60 * life, 80, healthPaint);
        if (life != 0){
            handler.postDelayed (runnable, UPDATE_MILLIS);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if (eggAnimation == false &&
                    (touchX >= birdX && touchX <= (birdX + bird.getWidth())
                    && touchY >= birdY && touchY <= (birdY + bird.getHeight()))){
                eggAnimation = true;
            }
            if (mpFly != null) {
                mpFly.start();
            }
        }
        return true;
    }
}
