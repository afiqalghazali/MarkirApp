package com.scifi.markirapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.scifi.markirapp.R
import com.scifi.markirapp.databinding.CustomAlertBinding

object InterfaceUtils {
    fun showAlert(
        context: Context,
        message: String?,
        isWarning: Boolean = false,
        primaryButtonText: String? = "OK",
        onPrimaryButtonClick: (() -> Unit)? = null,
        secondaryButtonText: String? = null,
        onSecondaryButtonClick: (() -> Unit)? = null,
    ) {
        val dialogView = CustomAlertBinding.inflate(LayoutInflater.from(context))
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView.root)
            .setCancelable(false)
            .create()
        dialogView.alertMessage.text = message
        dialogView.btnPrimary.text = primaryButtonText
        dialogView.btnPrimary.setTextColor(
            ContextCompat.getColor(
                context,
                if (isWarning) R.color.warning else R.color.primary_blue
            )
        )
        dialogView.btnPrimary.setOnClickListener {
            alertDialog.dismiss()
            onPrimaryButtonClick?.invoke()
        }
        if (secondaryButtonText != null) {
            dialogView.btnSecondary.visibility = View.VISIBLE
            dialogView.btnSecondary.text = secondaryButtonText
            dialogView.btnSecondary.setOnClickListener {
                alertDialog.dismiss()
                onSecondaryButtonClick?.invoke()
            }
        } else {
            dialogView.btnSecondary.visibility = View.GONE
        }
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }
}