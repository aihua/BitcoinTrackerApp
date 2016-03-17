package com.example.dr.bitcointracker.mvp.view.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.dr.bitcointracker.App;
import com.example.dr.bitcointracker.dagger.component.ActivityComponent;
import com.example.dr.bitcointracker.dagger.component.DaggerActivityComponent;
import com.example.dr.bitcointracker.dagger.module.ActivityModule;

public class BaseActivity extends AppCompatActivity {

    private ActivityComponent mActivityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // For dagger DI
    public ActivityComponent getActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(App.get(this).getComponent())
                    .build();
        }
        return mActivityComponent;
    }

}
