package com.example.mobilegame

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import java.util.Random

class GameView(var appContext: Context) : View(appContext) {
    var appHandler: Handler
    var devWidth: Int
    var devHeight: Int
    var net: Bitmap
    var bird: Bitmap
    var egg: Bitmap
    var runnable: Runnable
    var UPDATE_MILLIS: Long = 30
    var birdX: Int
    var birdY: Int
    var netX: Int
    var netY: Int
    var eggX: Int
    var eggY: Int
    var random: Random
    var eggAnimation = false
    var points = 0
    var TEXT_SIZE = 120f
    var textPaint: Paint
    var healthPaint: Paint
    var life = 3
    var birdSpeed: Int
    var mpScore: MediaPlayer?
    var mpHit: MediaPlayer?
    var mpFly: MediaPlayer?
    var mpCrack: MediaPlayer?

    init {
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        devWidth = size.x
        devHeight = size.y
        net = BitmapFactory.decodeResource(resources, R.drawable.basket)
        egg = BitmapFactory.decodeResource(resources, R.drawable.egg)
        bird = BitmapFactory.decodeResource(resources, R.drawable.bird)
        appHandler = Handler()
        runnable = Runnable { invalidate() }
        random = Random()
        birdX = devWidth + random.nextInt(300)
        birdY = random.nextInt(600)
        eggX = birdX + bird.getWidth() - 220
        eggY = birdY + bird.getHeight() - 100
        textPaint = Paint()
        textPaint.setColor(Color.rgb(255, 0, 0))
        textPaint.textSize = TEXT_SIZE
        textPaint.textAlign = Paint.Align.LEFT
        healthPaint = Paint()
        healthPaint.setColor(Color.GREEN)
        birdSpeed = 21 + random.nextInt(30)
        netX = devWidth / 2 - net.getWidth() / 2
        netY = devHeight - net.getHeight() - 40
        mpScore = MediaPlayer.create(context, R.raw.score)
        mpFly = MediaPlayer.create(context, R.raw.fly)
        mpHit = MediaPlayer.create(context, R.raw.wall_hit)
        mpCrack = MediaPlayer.create(context, R.raw.egg_crack)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val backgroundImage = BitmapFactory.decodeResource(resources, R.drawable.sky)
        val srcRect = Rect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight())
        val destRect = Rect(0, 0, canvas.width, canvas.height)
        canvas.drawBitmap(backgroundImage, srcRect, destRect, null)
        if (eggAnimation == false) {
            birdX -= birdSpeed
            eggX -= birdSpeed
        }
        if (birdX <= -bird.getWidth()) {
            if (mpHit != null) {
                mpHit!!.start()
            }
            birdX = devWidth + random.nextInt(300)
            birdY = random.nextInt(600)
            eggX = birdX + bird.getWidth() - 220
            eggY = birdY + bird.getHeight() - 100
            birdSpeed = 21 + random.nextInt(30)
            netX = bird.getWidth() + random.nextInt(devWidth - 2 * bird.getWidth())
            life--
            if (life == 0) {
                val intent = Intent(context, GameOver::class.java)
                intent.putExtra("points", points)
                context.startActivity(intent)
                (context as Activity).finish()
            }
        }
        if (eggAnimation) {
            eggY += 40
        }
        if (eggAnimation && eggX + egg.getWidth() - 90 >= netX && eggX + 100 <= netX + net.getWidth() && eggY + egg.getHeight() >= devHeight - net.getHeight() + 60 && eggY <= devHeight) {
            if (mpScore != null) {
                mpScore!!.start()
            }
            birdX = devWidth + random.nextInt(300)
            birdY = random.nextInt(600)
            eggX = birdX + bird.getWidth() - 220
            eggY = birdY + bird.getHeight() - 100
            birdSpeed = 21 + random.nextInt(30)
            points++
            netX = bird.getWidth() + random.nextInt(devWidth - 2 * bird.getWidth())
            eggAnimation = false
        }
        if (eggAnimation && eggY + egg.getHeight() >= devHeight) {
            if (mpCrack != null) {
                mpCrack!!.start()
            }
            life--
            if (life == 0) {
                val intent = Intent(context, GameOver::class.java)
                intent.putExtra("points", points)
                context.startActivity(intent)
                (context as Activity).finish()
            }
            birdX = devWidth + random.nextInt(300)
            birdY = random.nextInt(600)
            eggX = birdX + bird.getWidth() - 220
            eggY = birdY + bird.getHeight() - 100
            netX = bird.getWidth() + random.nextInt(devWidth - 2 * bird.getWidth())
            eggAnimation = false
        }
        canvas.drawBitmap(net, netX.toFloat(), netY.toFloat(), null)
        canvas.drawBitmap(bird, birdX.toFloat(), birdY.toFloat(), null)
        canvas.drawBitmap(egg, eggX.toFloat(), eggY.toFloat(), null)
        canvas.drawText("" + points, 20f, TEXT_SIZE, textPaint)
        if (life == 2) {
            healthPaint.setColor(Color.YELLOW)
        } else if (life == 1) {
            healthPaint.setColor(Color.RED)
        }
        canvas.drawRect(
            (devWidth - 200).toFloat(),
            30f,
            (devWidth - 200 + 60 * life).toFloat(),
            80f,
            healthPaint
        )
        if (life != 0) {
            handler.postDelayed(runnable, UPDATE_MILLIS)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (eggAnimation == false && touchX >= birdX && touchX <= birdX + bird.getWidth() && touchY >= birdY && touchY <= birdY + bird.getHeight()) {
                eggAnimation = true
            }
            if (mpFly != null) {
                mpFly!!.start()
            }
        }
        return true
    }
}
