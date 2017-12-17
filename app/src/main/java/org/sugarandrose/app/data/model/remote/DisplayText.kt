package org.sugarandrose.app.data.model.remote

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PaperParcel
class DisplayText(val rendered: String) : PaperParcelable {
    companion object {
        val CREATOR = PaperParcelDisplayText.CREATOR
    }
}
