package org.sugarandrose.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity



/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */


class IntentForwardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.setClass(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
