package ru.magflayer.colorpointer.presentation.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.lang.annotation.Annotation;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class cls = getClass();
        if (!cls.isAnnotationPresent(Layout.class)) return;
        Annotation annotation = cls.getAnnotation(Layout.class);
        Layout layout = (Layout) annotation;
        setContentView(layout.id());
    }
}
