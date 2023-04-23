package com.eece430L.inflaterates.utilities

import android.app.Activity
import android.app.Dialog
import com.eece430L.inflaterates.R
import java.lang.ref.WeakReference

// ChatGPT
class ProgressBarManager {

    private var progressBar: Dialog? = null
    private var currentActivity: WeakReference<Activity>? = null

    fun showProgressBar(activity: Activity) {
        currentActivity = WeakReference(activity)
        progressBar = Dialog(activity)
        progressBar?.apply {
            setContentView(R.layout.progress_bar)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    fun hideProgressBar() {
        progressBar?.dismiss()
        progressBar = null
        currentActivity?.clear()
        currentActivity = null
    }
}


