package com.example.mobilegame

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)
    }

    fun goToMainMenu(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun exit(view: View?) {
        finish()
    }
}
