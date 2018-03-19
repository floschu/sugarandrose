package org.sugarandrose.app.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import org.sugarandrose.app.R
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.data.local.PrefRepo
import org.sugarandrose.app.ui.main.MainActivity
import org.sugarandrose.app.ui.onboarding.OnboardingActivity
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class LauncherActivity : AppCompatActivity() {
    private var prefRepo = SugarAndRoseApp.appComponent.prefRepo()
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val intent = if (prefRepo.onboardingComplete) Intent(this, MainActivity::class.java)
        else Intent(this, OnboardingActivity::class.java)

        disposable = Completable.timer(1, TimeUnit.SECONDS).subscribe {
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}