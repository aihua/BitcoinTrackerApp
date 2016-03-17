package com.example.dr.bitcointracker.dagger.module;

import android.app.Application;
import android.content.Context;

import com.example.dr.bitcointracker.dagger.annotation.ApplicationContext;
import com.example.dr.bitcointracker.mvp.model.service.local.RealmService;
import com.example.dr.bitcointracker.mvp.model.service.local.repository.ChartRepository;
import com.example.dr.bitcointracker.mvp.model.service.remote.ChartAPIService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;


/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }


    @Provides
    @Singleton
    ChartAPIService provideChartAPIService() {
        return ChartAPIService.Creator.newChartAPIService();
    }

    @Provides
    @Singleton
    ChartRepository provideChartRepository(RealmService realmService) {
        return new ChartRepository(realmService);
    }

}
