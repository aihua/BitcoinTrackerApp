package com.example.dr.bitcointracker.dagger.component;

import com.example.dr.bitcointracker.dagger.annotation.PerActivity;
import com.example.dr.bitcointracker.dagger.module.ActivityModule;
import com.example.dr.bitcointracker.mvp.view.main.MainActivity;

import dagger.Component;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

}
