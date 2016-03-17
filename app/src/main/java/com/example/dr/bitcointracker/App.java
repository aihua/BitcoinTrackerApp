package com.example.dr.bitcointracker;

import android.app.Application;
import android.content.Context;

import com.example.dr.bitcointracker.dagger.component.ApplicationComponent;
import com.example.dr.bitcointracker.dagger.component.DaggerApplicationComponent;
import com.example.dr.bitcointracker.dagger.module.ApplicationModule;


public class App extends Application{

    public static final String LOGTAG = "BitcoinTrack";
    private ApplicationComponent mApplicationComponent;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
