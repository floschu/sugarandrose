package org.sugarandrose.app.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import org.sugarandrose.app.R
import org.sugarandrose.app.ui.main.MainActivity
import java.util.concurrent.TimeUnit

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class LauncherActivity : AppCompatActivity() {
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        disposable = Completable.timer(1, TimeUnit.SECONDS).subscribe {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}