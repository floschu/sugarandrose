package org.sugarandrose.app.ui.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.squareup.leakcanary.RefWatcher
import org.sugarandrose.app.BR
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.injection.components.ActivityComponent
import org.sugarandrose.app.injection.components.DaggerActivityComponent
import org.sugarandrose.app.injection.modules.ActivityModule
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.util.extensions.attachViewOrThrowRuntimeException
import io.realm.Realm
import javax.inject.Inject

/* Copyright 2016 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -------
 *
 * FILE MODIFIED 2017 Tailored Media GmbH
 * */

/* Base class for Activities when using a view model with data binding.
 * This class provides the binding and the view model to the subclass. The
 * view model is injected and the binding is created when the content view is set.
 * Each subclass therefore has to call the following code in onCreate():
 *    activityComponent().inject(this);
 *    setAndBindContentView(R.layout.my_activity_layout, savedInstanceState);
 *
 * After calling these methods, the binding and the view model is initialized.
 * saveInstanceState() and restoreInstanceState() methods of the view model
 * are automatically called in the appropriate lifecycle events when above calls
 * are made.
 *
 * Your subclass must implement the MvvmView implementation that you use in your
 * view model. */
abstract class BaseActivity<B : ViewDataBinding, VM : MvvmViewModel<*>> : AppCompatActivity(), MvvmView {


    // Inject a Realm INSTANCE into every Activity, since the INSTANCE
    // is cached and reused for a thread (avoids create/destroy overhead)
    @Inject protected lateinit var realm: Realm

    protected lateinit var binding: B
    @Inject protected lateinit var viewModel: VM

    @Inject
    protected lateinit var refWatcher: RefWatcher

    internal val activityComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .appComponent(org.sugarandrose.app.SugarAndRoseApp.appComponent)
                .build()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveInstanceState(outState)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        viewModel.detachView()
        refWatcher.watch(activityComponent)
        refWatcher.watch(viewModel)
        realm.close()
    }

    /* Sets the content view, creates the binding and attaches the view to the view model */
    protected fun setAndBindContentView(savedInstanceState: Bundle?, @LayoutRes layoutResID: Int) {
        binding = DataBindingUtil.setContentView<B>(this, layoutResID)
        binding.setVariable(BR.vm, viewModel)
        viewModel.attachViewOrThrowRuntimeException(this, savedInstanceState)
    }

    fun dimen(@DimenRes resId: Int): Int = resources.getDimension(resId).toInt()
    fun color(@ColorRes resId: Int): Int = ContextCompat.getColor(this, resId)
    fun integer(@IntegerRes resId: Int): Int = resources.getInteger(resId)
    fun string(@StringRes resId: Int): String = resources.getString(resId)
}
