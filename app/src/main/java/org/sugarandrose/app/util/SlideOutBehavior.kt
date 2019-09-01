package org.sugarandrose.app.util

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import io.reactivex.disposables.Disposable
import org.sugarandrose.app.util.extensions.isVisible
import org.sugarandrose.app.util.extensions.slideIn
import org.sugarandrose.app.util.extensions.slideOut


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class SlideOutBehavior<V : View>(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<V>(context, attrs) {
    private var currentlySliding = false
    private var atBottom = false
    private var lastVerticalScrollDirection = 0
    private var lastDisposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean =
            if (dependency is NestedScrollView) {
                dependency.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                    atBottom = scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight
                    if (atBottom && (!child.isVisible() || (child.isVisible() && currentlySliding))) {
                        if (currentlySliding) child.clearAnimation()
                        currentlySliding = true
                        lastDisposable = child.slideIn(250).subscribe { currentlySliding = false }
                    }
                })
                true
            } else false

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int): Boolean = axes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        if (type == ViewCompat.TYPE_TOUCH) {
            if (!currentlySliding) {
                if (dyConsumed < 0 && !child.isVisible()) {
                    currentlySliding = true
                    lastDisposable = child.slideIn(250).subscribe { currentlySliding = false }
                } else if (dyConsumed > 0 && child.isVisible()) {
                    currentlySliding = true
                    lastDisposable = child.slideOut(250).subscribe { currentlySliding = false }
                }
            } else if (currentlySliding && (dyConsumed < 0) != (lastVerticalScrollDirection < 0)) {
                child.clearAnimation()
                child.visibility = if (child.isVisible()) View.INVISIBLE else View.VISIBLE
                currentlySliding = false
            }
            lastVerticalScrollDirection = dyConsumed
        } else super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
    }
}
