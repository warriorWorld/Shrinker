package com.harbinger.shrinker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.harbinger.shrinker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        findViewById<Button>(R.id.image_btn).setOnClickListener {

        }
        findViewById<Button>(R.id.gif_btn).setOnClickListener {

        }
        findViewById<Button>(R.id.video_btn).setOnClickListener {

        }
    }
}