package ru.magflayer.colorpointer.presentation.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.utils.FragmentUtils;

public class FragmentRouter {

    private FragmentManager fragmentManager;

    public FragmentRouter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    protected void replaceFragment(Fragment fragment, boolean backStack) {
        FragmentUtils.replaceFragment(fragmentManager, R.id.container, fragment, backStack);
    }

}
