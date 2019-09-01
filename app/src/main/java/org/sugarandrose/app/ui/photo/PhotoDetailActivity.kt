package org.sugarandrose.app.ui.photo

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import coil.Coil
import coil.api.load
import coil.target.ViewTarget
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.ActivityPhotodetailBinding
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.NoOpViewModel

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 * https://florianschuster.at/
 */

class PhotoDetailActivity : BaseActivity<ActivityPhotodetailBinding, NoOpViewModel<MvvmView>>(), MvvmView {

    companion object {
        const val EXTRA_IMG_URL_AND_TRANSITION_NAME = "PhotoDetailActivity.EXTRA_IMG_URL_AND_TRANSITION_NAME"
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
        if (url == null || url.isEmpty()) finish()
        else {
            binding.photoView.transitionName = url
            binding.photoView.isEnabled = false

            Coil.load(this, url) {
                lifecycle(this@PhotoDetailActivity)
                crossfade(true)
                target(object : ViewTarget<SubsamplingScaleImageView> {
                    override val view: SubsamplingScaleImageView get() = binding.photoView

                    override fun onSuccess(result: Drawable) {
                        super.onSuccess(result)
                        binding.photoView.setImage(ImageSource.bitmap((result as BitmapDrawable).bitmap))
                        binding.photoView.isEnabled = true
                        startPostponedEnterTransition()
                    }

                    override fun onError(error: Drawable?) {
                        super.onError(error)
                        finish()
                    }
                })
            }
        }
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
