package org.sugarandrose.app.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import org.sugarandrose.app.R
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.ui.main.MainActivity
import org.sugarandrose.app.ui.onboarding.OnboardingActivity
import java.util.concurrent.TimeUnit
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.widget.ImageView


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class LauncherActivity : AppCompatActivity() {
    private var disposable: Disposable? = null
    private var prefsRepo = SugarAndRoseApp.appComponent.prefRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val intent =
                if (prefsRepo.onboardingDone) Intent(this, MainActivity::class.java)
                else Intent(this, OnboardingActivity::class.java)

        val image = findViewById<ImageView>(R.id.iv_logo)
        val transition = ActivityOptionsCompat.makeSceneTransitionAnimation(this, image, ViewCompat.getTransitionName(image))

        disposable = Completable.timer(1, TimeUnit.SECONDS).subscribe {
            startActivity(intent, transition.toBundle())
            supportFinishAfterTransition()
        }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}