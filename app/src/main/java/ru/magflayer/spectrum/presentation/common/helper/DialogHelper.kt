package ru.magflayer.spectrum.presentation.common.helper

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import ru.magflayer.spectrum.R

object DialogHelper {

    @StringRes
    private val positiveButtonRes = R.string.dialog_yes

    @StringRes
    private val negativeButtonRes = R.string.dialog_no

    fun buildYesNoDialog(
        context: Context,
        title: String,
        message: String,
        clickListener: DialogInterface.OnClickListener,
    ): Dialog {
        val builder = AlertDialog.Builder(context)

        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveButtonRes, clickListener)
        builder.setNegativeButton(negativeButtonRes) { dialogInterface, i -> dialogInterface.cancel() }

        return builder.create()
    }

    fun buildViewDialog(
        context: Context,
        title: String,
        view: View,
        clickListener: DialogInterface.OnClickListener,
    ): Dialog {
        val builder = AlertDialog.Builder(context)

        builder.setTitle(title)
        builder.setView(view)
        builder.setPositiveButton(positiveButtonRes, clickListener)

        return builder.create()
    }
}
