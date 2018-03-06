package org.sugarandrose.app.util.manager

import android.content.res.Resources
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.R
import org.sugarandrose.app.injection.scopes.PerApplication
import org.sugarandrose.app.util.NetworkUnavailableException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerApplication
class ErrorManager @Inject
constructor(private val res: Resources) {

    fun showError(throwable: Throwable, errorConsumer: (message: String) -> Unit) {
        Timber.e(throwable)
        errorConsumer(getUserErrorMessage(throwable))
    }

    private fun getUserErrorMessage(throwable: Throwable): String = when (getErrorCause(throwable)) {
        ErrorCause.NETWORK_UNAVAILABLE -> res.getString(R.string.error_network_unavailable)
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