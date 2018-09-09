package ru.magflayer.spectrum.presentation.common.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentUtils {

    private FragmentUtils() {
    }

    public static void replaceFragment(FragmentManager fragmentManager, int containerId, Fragment fragment, boolean backStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (backStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }

        transaction.replace(containerId, fragment);

        transaction.commit();
    }

}
