package com.scifi.markirapp.utils

import android.app.Activity
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.scifi.markirapp.ui.view.WelcomeActivity

object FirebaseAuthUtils {
    val instance: FirebaseAuth by lazy {
        Firebase.auth
    }

    fun sessionEndedAlert(activity: Activity) {
        AppsUtils.showAlert(
            activity,
            isWarning = true,
            message = "Your session has ended. Please log in again",
            onPrimaryButtonClick = {
                activity.startActivity(Intent(activity, WelcomeActivity::class.java))
                activity.finish()
            }
        )
    }
}