package com.example.dr.bitcointracker.mvp.model.service.local.repository;

import com.example.dr.bitcointracker.mvp.model.service.local.RealmService;
import com.example.dr.bitcointracker.mvp.model.service.local.models.ChartData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class ChartRepository {
    private final RealmService realmService;

    @Inject
    public ChartRepository(RealmService realmService){
        this.realmService = realmService;
    }

    // Return an observable list of converted POJOs
    public Observable<List<ChartData>> getAllItems(){
        final Realm realm = Realm.getDefaultInstance();
        return realm.where(ChartData.class).findAll().<List<ChartData>>asObservable()
                .map(new Func1<List<ChartData>, List<ChartData>>() {
                    @Override
                    public List<ChartData> call(List<ChartData> data) {
                        // Convert live object to a static copy
                        return realm.copyFromRealm(data);
                    }
                });
    }

    // Parse blockchain data and insert into ChardData table
    public void insertDataFromBlockchain(JsonObject data){
        JsonArray valuesArray = data.get("values").getAsJsonArray();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for(int i = 0; i < valuesArray.size(); i++){
            ChartData dataRow = new ChartData();
            dataRow.setTimestamp(valuesArray.get(i).getAsJsonObject().get("x").getAsLong());
            dataRow.setValue(valuesArray.get(i).getAsJsonObject().get("y").getAsFloat());

            realm.copyToRealmOrUpdate(dataRow);
        }
        realm.commitTransaction();
    }
}
