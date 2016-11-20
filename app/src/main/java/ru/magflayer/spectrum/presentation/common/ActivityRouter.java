package ru.magflayer.spectrum.presentation.common;

import android.content.Context;
import android.content.Intent;

public class ActivityRouter {

    private Context context;

    public ActivityRouter(Context context) {
        this.context = context;
    }

    protected void startActivity(Class activityClass, boolean isNew) {
        Intent intent = new Intent(context, activityClass);
        if (isNew) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
    }

}
