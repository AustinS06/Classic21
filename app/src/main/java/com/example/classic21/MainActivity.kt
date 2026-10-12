package com.example.classic21

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvRecord: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvRecord = findViewById(R.id.tvRecord)
        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnRules = findViewById<Button>(R.id.btnRules)

        updateRecord()

        btnPlay.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        btnRules.setOnClickListener {
            startActivity(Intent(this, RulesActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateRecord()
    }

    private fun updateRecord() {
        val prefs = getSharedPreferences("blackjack_prefs", Context.MODE_PRIVATE)
        val wins = prefs.getInt("wins", 0)
        val losses = prefs.getInt("losses", 0)
        tvRecord.text = "Wins: $wins  |  Losses: $losses"
    }
}
