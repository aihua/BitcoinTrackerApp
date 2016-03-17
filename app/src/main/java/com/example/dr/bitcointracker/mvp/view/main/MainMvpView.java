package com.example.dr.bitcointracker.mvp.view.main;

import com.example.dr.bitcointracker.mvp.model.service.local.models.ChartData;
import com.example.dr.bitcointracker.mvp.view.base.MvpView;

import java.util.List;

import io.realm.RealmResults;

/******************************************************
 * Contract between view and presenter
 *****************************************************/
public interface MainMvpView extends MvpView {

    // No data exist -> empty screen
    void showEmptyView();

    // Generic error occured -> show toast
    void showError(String message);

    // Network call active -> show progress below actionbar
    void showProgress();

    // Network call finished -> hide progress
    void hideProgress();

    // Chart view is hidden by default. When data loaded then show
    void showChartView();

    // Hide empty view when data is loaded
    void hideEmptyView();

    /*
    * Params
    *   timestamps - The extracted timestamps from data
    *   values     - The extracted values from data
    *   minTimestamp - Current minimum timestamp in data
    *   maxTimestamp - Current maximum timestamp in data
    * */
    void updateChart(long[] timestamps, float[] values, long minTimestamp, long maxTimestamp);
}
