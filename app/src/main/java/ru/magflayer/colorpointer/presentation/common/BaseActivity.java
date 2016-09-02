package ru.magflayer.colorpointer.presentation.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.lang.annotation.Annotation;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<Router> extends AppCompatActivity {

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class cls = getClass();
        if (!cls.isAnnotationPresent(Layout.class)) return;
        Annotation annotation = cls.getAnnotation(Layout.class);
        Layout layout = (Layout) annotation;
        setContentView(layout.id());

        unbinder = ButterKnife.bind(this);

        inject();
    }

    public abstract Router getRouter();

    protected abstract void inject();

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
