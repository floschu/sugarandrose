package org.sugarandrose.app.ui.base

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import javax.inject.Inject
import org.sugarandrose.app.BR
import org.sugarandrose.app.injection.components.ActivityViewHolderComponent
import org.sugarandrose.app.injection.components.DaggerActivityViewHolderComponent
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.util.extensions.attachViewOrThrowRuntimeException
import org.sugarandrose.app.util.extensions.castWithUnwrap

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
 * ------
 *
 * FILE MODIFIED 2017 Tailored Media GmbH
 */

/* Base class for ViewHolders when using a view model in an Activity with data binding.
 * This class provides the binding and the view model to the subclass. The
 * view model is injected and the binding is created when the content view is bound.
 * Each subclass therefore has to call the following code in the constructor:
 *    getViewHolderComponent().inject(this);
 *    bindContentView(view);
 *
 * After calling these methods, the binding and the view model is initialized.
 * saveInstanceState() and restoreInstanceState() are not called/used for ViewHolder
 * view models.
 *
 * Your subclass must implement the MvvmView implementation that you use in your
 * view model. */
abstract class BaseActivityViewHolder<B : ViewDataBinding, VM : MvvmViewModel<*>>(itemView: View) : RecyclerView.ViewHolder(itemView), MvvmView {

    protected lateinit var binding: B
    @Inject lateinit var viewModel: VM
        protected set

    protected val viewHolderComponent: ActivityViewHolderComponent by lazy {
        DaggerActivityViewHolderComponent.builder()
                .activityComponent(itemView.context.castWithUnwrap<BaseActivity<*, *>>()?.activityComponent)
                .build()
    }

    init {
        try {
            ActivityViewHolderComponent::class.java.getDeclaredMethod("inject", this::class.java).invoke(viewHolderComponent, this)
        } catch (e: NoSuchMethodException) {
            throw RtfmException("You forgot to add \"fun inject(viewHolder: ${this::class.java.simpleName})\" in ActivityViewHolderComponent")
        }
    }

    protected fun bindContentView(view: View) {
        binding = DataBindingUtil.bind(view)!!
        binding.setVariable(BR.vm, viewModel)
        viewModel.attachViewOrThrowRuntimeException(this, null)
    }

    fun executePendingBindings() {
        binding.executePendingBindings()
    }
}
