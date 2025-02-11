package com.example.betheclub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.betheclub.R

/**
 * The HelpActivity class displays the help screen to the user.
 *
 * This activity provides information or instructions on how to use the application.
 *
 * Layout: activity_help.xml
 */
class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
    }
}