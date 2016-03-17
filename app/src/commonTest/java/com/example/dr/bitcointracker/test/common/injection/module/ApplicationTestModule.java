package com.example.dr.bitcointracker.test.common.injection.module;


import android.app.Application;
import android.content.Context;

import com.example.dr.bitcointracker.dagger.annotation.ApplicationContext;
import com.example.dr.bitcointracker.mvp.model.service.local.RealmService;
import com.example.dr.bitcointracker.mvp.model.service.local.repository.ChartRepository;
import com.example.dr.bitcointracker.mvp.model.service.remote.ChartAPIService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;


/*Provides application-level dependencies for an app running on a testing environment
        *This allows injecting mocks if necessary.
        */

@Module
public class ApplicationTestModule {

    private final Application mApplication;

    public ApplicationTestModule(Application application) {
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

    /************* MOCKS *************/
    @Provides
    @Singleton
    ChartAPIService provideChartAPIService() {
        return mock(ChartAPIService.class);
    }

    @Provides
    @Singleton
    ChartRepository provideChartRepository(RealmService realmService) {
        return mock(ChartRepository.class);
    }

}
