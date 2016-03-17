package com.example.dr.bitcointracker.mvp.model.service.local;

import android.app.Application;
import android.os.Environment;
import android.support.v4.BuildConfig;
import android.util.Log;

import com.example.dr.bitcointracker.mvp.model.service.local.repository.ChartRepository;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@Singleton
public class RealmService {
    private final Application mApplication;

    public final int REALM_VERSION = 0;

    @Inject
    public RealmService(Application application){
        mApplication = application;

        // Set up realm depending on build configuration
        if (BuildConfig.DEBUG) {
            RealmConfiguration cfg = new RealmConfiguration.Builder(application)
                    .name("debug.realm")
                    .schemaVersion(REALM_VERSION)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            Realm.setDefaultConfiguration(cfg);
        } else {
            RealmConfiguration cfg = new RealmConfiguration.Builder(application)
                    .schemaVersion(REALM_VERSION)
                    .build();
            Realm.setDefaultConfiguration(cfg);
        }
    }
}
