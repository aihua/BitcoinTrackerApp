package com.example.dr.bitcointracker.mvp.presenter.main;

import android.util.Log;

import com.example.dr.bitcointracker.App;
import com.example.dr.bitcointracker.mvp.model.service.local.RealmService;
import com.example.dr.bitcointracker.mvp.model.service.local.models.ChartData;
import com.example.dr.bitcointracker.mvp.model.service.local.repository.ChartRepository;
import com.example.dr.bitcointracker.mvp.model.service.remote.ChartAPIService;
import com.example.dr.bitcointracker.mvp.presenter.base.BasePresenter;
import com.example.dr.bitcointracker.mvp.view.main.MainMvpView;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/********************************************************
 * Presenter of the main view (MainActivity)
 * Handles subscriptions to API, manages subsciptions to data changes
 * on realm query
 * Sends method calls to views
 ******************************************************/
public class MainPresenter extends BasePresenter<MainMvpView> {

    private final int POOL_INTERVAL_SECONDS = 5;

    private final ChartAPIService mChartAPIService;
    private final ChartRepository mChartRepository;

    private boolean isRequestRunning = false;

    // Subscriptions to ChartAPIService and local database
    public Subscription mLoadLocalDataSupscription;
    public Subscription mLoadRemoteDataSubscription;

    private List<ChartData> mData = new ArrayList<>();
    private long mMinimumTimestamp = 0;
    private long mMaximumTimestamp = 0;

    // Keeps track of what the user selected in daterange
    private long mSelectedTimestampSince = 0;

    @Inject
    public MainPresenter(ChartRepository chartRepository, ChartAPIService chartAPIService) {
        mChartRepository = chartRepository;
        mChartAPIService = chartAPIService;
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mLoadLocalDataSupscription != null) mLoadLocalDataSupscription.unsubscribe();
        if (mLoadRemoteDataSubscription != null) mLoadRemoteDataSubscription.unsubscribe();
    }

    public void initialize() {
        checkViewAttached();
        if (mData.size() == 0) {
            getMvpView().showEmptyView();
        } else {
            getMvpView().hideEmptyView();
            getMvpView().showChartView();
        }

        // Init fetching & observing local database (realm) data
        mLoadLocalDataSupscription = mChartRepository.getAllItems()
                .observeOn(AndroidSchedulers.mainThread())
                        //.subscribeOn(Schedulers.io()) <- This currently does not work (https://realm.io/news/realm-java-0.87.0#work-in-progress)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.i(App.LOGTAG, "Start loading local data");
                    }
                })
                .subscribe(new Subscriber<List<ChartData>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e("ERROR", "There was an error loading the data : " + e.getMessage());
                        getMvpView().showError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<ChartData> data) {

                        Log.v(App.LOGTAG, "There are " + data.size() + " entries");

                        if (data.size() > 0) {
                            mData = data;

                            mMinimumTimestamp = data.get(0).getTimestamp();
                            mMaximumTimestamp = data.get(data.size() - 1).getTimestamp();

                            getMvpView().hideEmptyView();
                            getMvpView().showChartView();

                            if (mSelectedTimestampSince == 0)
                                mSelectedTimestampSince = mMaximumTimestamp;

                            filterChartDisplaySince(getSelectedTimestamp());
                        } else {
                            getMvpView().showEmptyView();
                        }
                    }
                });
    }

    // Returns the timestamp of date the user selected with a slider
    public long getSelectedTimestamp() {
        return mSelectedTimestampSince;
    }

    // Stops interval (pooling) network
    public void stopRefreshingChartDataFromNetwork() {
        if (mLoadRemoteDataSubscription != null)
            mLoadRemoteDataSubscription.unsubscribe();
        isRequestRunning = false;
    }

    // Pooling method - fetch api data
    public void refreshChartDataFromNetwork() {
        // Pooling
        mLoadRemoteDataSubscription = Observable.interval(0, POOL_INTERVAL_SECONDS, TimeUnit.SECONDS)
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        // Avoid calling multiple times on slow network
                        if (isRequestRunning)
                            return;

                        doApiCall();
                    }
                });
    }

    public void doApiCall() {
        mChartAPIService.getAllBitcoinMarketPrice()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        isRequestRunning = true;
                        getMvpView().showProgress();
                    }
                })
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().hideProgress();
                        isRequestRunning = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ERROR", "There was an error loading the data : " + e.getMessage());
                        getMvpView().hideProgress();
                        getMvpView().showError(e.getMessage());
                        isRequestRunning = false;
                    }

                    @Override
                    public void onNext(JsonObject data) {
                        mChartRepository.insertDataFromBlockchain(data);
                    }
                });
    }

    // Called usually from view when user changes slider.
    // Returns a subset of the complete data in database
    public void filterChartDisplaySince(Number selectedMaxValue) {

        // If the rangebar selection is greater than what we have then we override the range to a default of last 7 days
        if (selectedMaxValue.longValue() >= mMaximumTimestamp) {
            selectedMaxValue = mMaximumTimestamp - (7 * 24 * 60 * 60);
        }

        // Keep track of what the user selected
        mSelectedTimestampSince = selectedMaxValue.longValue();

        List<ChartData> tmpList = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getTimestamp() >= selectedMaxValue.longValue()) {
                tmpList.add(mData.get(i));
            }
        }

        getMvpView().updateChart(extractTimestamps(tmpList), extractValues(tmpList), mMinimumTimestamp, mMaximumTimestamp);
    }

    // Helper function - extract all values from list
    private float[] extractValues(List<ChartData> data) {
        float[] ret = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            ret[i] = data.get(i).getValue();
        }
        return ret;
    }

    // Helper function - extract all timestamps from list
    private long[] extractTimestamps(List<ChartData> data) {
        long[] ret = new long[data.size()];
        for (int i = 0; i < data.size(); i++) {
            ret[i] = data.get(i).getTimestamp();
        }
        return ret;
    }

    public boolean isRequestRunning() {
        return isRequestRunning;
    }

    public void setIsRequestRunning(boolean isRequestRunning) {
        this.isRequestRunning = isRequestRunning;
    }
}