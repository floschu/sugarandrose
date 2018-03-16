package org.sugarandrose.app.ui.photo

import android.os.Bundle
import android.view.MenuItem
import com.davemorrissey.labs.subscaleview.ImageSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.ActivityPhotodetailBinding
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.NoOpViewModel
import org.sugarandrose.app.util.extensions.loadWithPicasso
import timber.log.Timber

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 * https://florianschuster.at/
 */

class PhotoDetailActivity : BaseActivity<ActivityPhotodetailBinding, NoOpViewModel<MvvmView>>(), MvvmView {

    companion object {
        const val EXTRA_IMG_URL_AND_TRANSITION_NAME = "com.tailoredapps.oenb.ui.photo.PhotoDetailActivity.EXTRA_IMG_URL_AND_TRANSITION_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindContentView(savedInstanceState, R.layout.activity_photodetail)

        postponeEnterTransition()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.title = ""
        }

        val url = intent.getStringExtra(EXTRA_IMG_URL_AND_TRANSITION_NAME)
        if (url == null || url.isEmpty()) {
            Timber.d("Missing EXTRA_IMG_URL_AND_TRANSITION_NAME for PhotoDetailActivity.")
            finish()
            return
        }

        binding.photoView.transitionName = url

        binding.photoView.isEnabled = false

        loadWithPicasso(url).observeOn(AndroidSchedulers.mainThread()).subscribe({
            binding.photoView.setImage(ImageSource.bitmap(it))
            binding.photoView.isEnabled = true
            startPostponedEnterTransition()
        }, {
            Timber.d("Error on refreshing image for PhotoDetailActivity: $url")
            finish()
        }).addTo(disposable)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFinishAfterTransition()
    }
}
