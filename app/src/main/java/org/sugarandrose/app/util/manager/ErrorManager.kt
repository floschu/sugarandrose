package org.sugarandrose.app.util.manager

import android.content.res.Resources
import java.io.IOException
import javax.inject.Inject
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.R
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.feedback.Snacker
import org.sugarandrose.app.ui.base.feedback.Toaster
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.util.exceptions.NetworkUnavailableException
import org.sugarandrose.app.util.views.NoConnectionDialogFragment
import retrofit2.HttpException
import timber.log.Timber

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerActivity
class ErrorManager @Inject
constructor(
    private val res: Resources,
    private val navigator: Navigator,
    private val snacker: Snacker,
    private val toaster: Toaster
) {

    fun handleWithToast(throwable: Throwable, retryCallback: (() -> Unit)? = null) {
        alertUser(throwable, { toaster.show(it) }, retryCallback)
    }

    fun handleWithRetrySnack(throwable: Throwable, retryAction: () -> Unit) {
        alertUser(throwable, { snacker.show(it, res.getString(R.string.error_retry), retryAction) }, retryAction)
    }

    private fun alertUser(throwable: Throwable, messageCallback: (String) -> Unit, retryCallback: (() -> Unit)? = null) {
        if (throwable is NetworkUnavailableException) {
            navigator.showDialogFragment(NoConnectionDialogFragment().apply { dialogRetryCallback = retryCallback })
        } else {
            Timber.e(throwable)
            messageCallback.invoke(getUserErrorMessage(throwable))
        }
    }

    private fun getUserErrorMessage(throwable: Throwable): String = when (getErrorCause(throwable)) {
        ErrorCause.SERVER_UNAVAILABLE -> res.getString(R.string.error_server_unavailable)
        ErrorCause.SERVER_ERROR -> getUserErrorMessage(throwable as HttpException)
        ErrorCause.MISSING_ELEMENTS -> res.getString(R.string.error_server_error)
        else -> res.getString(R.string.error_other)
    }

    private fun getUserErrorMessage(httpException: HttpException): String {
        val defaultMessage = res.getString(R.string.error_server_error)
        val message = try {
            httpException.message() ?: defaultMessage
        } catch (e: Exception) {
            defaultMessage
        }
        @Suppress("ConstantConditionIf")
        return message + if (BuildConfig.DEBUG) "(${httpException.code()})" else ""
    }

    private fun getErrorCause(throwable: Throwable): ErrorCause = when (throwable) {
        is IOException -> ErrorCause.SERVER_UNAVAILABLE
        is HttpException -> ErrorCause.SERVER_ERROR
        is NoSuchElementException -> ErrorCause.MISSING_ELEMENTS
        is SecurityException -> ErrorCause.SECURITY_ERROR
        is NetworkUnavailableException -> ErrorCause.NETWORK_UNAVAILABLE
        else -> ErrorCause.OTHER
    }

    private enum class ErrorCause {
        NETWORK_UNAVAILABLE,
        SERVER_UNAVAILABLE,
        MISSING_ELEMENTS,
        SERVER_ERROR,
        SECURITY_ERROR,
        OTHER
    }
}
