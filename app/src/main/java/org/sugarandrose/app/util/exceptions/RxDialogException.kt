package org.sugarandrose.app.util.exceptions

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class RxDialogException(val type: Type) : Exception() {
    companion object {
        fun canceled(): RxDialogException = RxDialogException(Type.CANCELED)
        fun negative(): RxDialogException = RxDialogException(Type.NEGATIVE)
    }

    enum class Type {
        CANCELED,
        NEGATIVE
    }

    override val message: String?
        get() = type.toString()
}