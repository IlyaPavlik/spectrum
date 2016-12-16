package ru.magflayer.spectrum.presentation.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.utils.FragmentUtils;

public class FragmentRouter {

    protected AppCompatActivity activity;
    protected FragmentManager fragmentManager;

    public FragmentRouter(AppCompatActivity activity) {
        this.activity = activity;
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    protected void replaceFragment(Fragment fragment, boolean backStack) {
        FragmentUtils.replaceFragment(fragmentManager, R.id.container, fragment, backStack);
    }
}
