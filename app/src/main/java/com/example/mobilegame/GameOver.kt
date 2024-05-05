package com.example.mobilegame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {
    private lateinit var tvPoints: TextView
    private lateinit var personalBest: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)
        tvPoints = findViewById(R.id.tvPoints) as TextView
        val points = intent.extras!!.getInt("points")
        tvPoints.text = points.toString()
        val pref = getSharedPreferences("MyPref", 0)
        var scoreSp = pref.getInt("scoreSp", 0)
        val editor = pref.edit()
        if (points > scoreSp) {
            scoreSp = points
            editor.putInt("scoreSp", scoreSp)
            editor.apply()
        }
        personalBest = findViewById(R.id.bestScore) as TextView
        personalBest.text = scoreSp.toString()
    }

    fun restart(view: View?) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToMainMenu(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
