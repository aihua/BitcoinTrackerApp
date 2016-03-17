package com.example.dr.bitcointracker.dagger.component;

import android.app.Application;
import android.content.Context;

import com.example.dr.bitcointracker.dagger.annotation.ApplicationContext;
import com.example.dr.bitcointracker.dagger.module.ApplicationModule;
import com.example.dr.bitcointracker.mvp.model.service.local.RealmService;
import com.example.dr.bitcointracker.mvp.model.service.local.repository.ChartRepository;
import com.example.dr.bitcointracker.mvp.model.service.remote.ChartAPIService;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    //void inject(MainActivity activity);

    @ApplicationContext Context context();
    Application application();
    ChartRepository chartRepository();
    ChartAPIService chartApiService();
}
