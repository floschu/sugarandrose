package org.sugarandrose.app.util.views

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import org.sugarandrose.app.R
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.util.extensions.isNetworkAvailable
import timber.log.Timber

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class NoConnectionDialogFragment : DialogFragment() {
    private var disposable: Disposable? = null
    private var backPressCount = 0
    private lateinit var image: ImageView
    private val toaster = SugarAndRoseApp.appComponent.toaster()
    private val rotationAnimation = RotateAnimation(
        0f, 1080f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    ).apply {
        duration = 1500
        interpolator = FastOutSlowInInterpolator()
    }

    var dialogRetryCallback: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (showsDialog && dialog?.window != null) {
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
        }

        val view = inflater.inflate(R.layout.fragment_no_connection, container, false)
        image = view.findViewById(R.id.iv)
        image.setOnClickListener { toaster.show(R.string.no_connection) }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                if (backPressCount < 5) backPressCount++
                else toaster.show(R.string.no_connection)
            }
        }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        Observable.interval(1, 3, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                image.startAnimation(rotationAnimation)
                if (requireActivity().isNetworkAvailable) {
                    dialogRetryCallback?.invoke()
                    dismiss()
                }
            }, Timber::e)
            .let { disposable = it }
    }

    override fun onDismiss(dialog: DialogInterface) {
        rotationAnimation.cancel()
        disposable?.dispose()
        super.onDismiss(dialog)
    }
}
