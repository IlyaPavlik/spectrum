package ru.magflayer.spectrum.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;

import ru.magflayer.spectrum.R;

public class DialogUtils {

    private DialogUtils() {
    }

    @StringRes
    private static final int positiveButtonRes = R.string.dialog_ok;
    @StringRes
    private static final int negativeButtonRes = R.string.dialog_cancel;

    public static Dialog buildYesNoDialog(Context context, String title, String message, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonRes, clickListener);
        builder.setNegativeButton(negativeButtonRes, (dialogInterface, i) -> dialogInterface.dismiss());

        return builder.create();
    }

    public static Dialog buildViewDialog(Context context, String title, View view, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setView(view);
        builder.setPositiveButton(positiveButtonRes, clickListener);

        return builder.create();
    }
}
