package org.sugarandrose.app.util

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.util.AttributeSet
import io.reactivex.disposables.Disposable
import org.sugarandrose.app.util.extensions.slideIn
import org.sugarandrose.app.util.extensions.slideOut
import org.sugarandrose.app.util.extensions.isVisible


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class SlideOutBehavior<V : View>(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<V>(context, attrs) {
    private var lastVerticalScrollDirection = 0
    private var currentlyFading = false
    private var lastDisposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean = dependency is NestedScrollView
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int): Boolean = axes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        if (type == ViewCompat.TYPE_TOUCH) {
            if (!currentlyFading) {
                if (dyConsumed > 0 && child.isVisible()) {
                    currentlyFading = true
                    lastDisposable = child.slideOut(250).subscribe { currentlyFading = false }
                } else if (dyConsumed < 0 && !child.isVisible()) {
                    currentlyFading = true
                    lastDisposable = child.slideIn(250).subscribe { currentlyFading = false }
                }
                //todo show when at bottom
            } else if (currentlyFading && (dyConsumed < 0) != (lastVerticalScrollDirection < 0)) {
                child.clearAnimation()
                child.visibility = if (child.isVisible()) View.INVISIBLE else View.VISIBLE
                currentlyFading = false
            }
            lastVerticalScrollDirection = dyConsumed
        } else super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
    }
}
