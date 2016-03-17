package com.example.dr.bitcointracker.mvp.view.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dr.bitcointracker.App;
import com.example.dr.bitcointracker.R;
import com.example.dr.bitcointracker.mvp.presenter.main.MainPresenter;
import com.example.dr.bitcointracker.mvp.view.base.BaseActivity;
import com.example.dr.bitcointracker.mvp.view.main.ui.ChartView;
import com.example.dr.bitcointracker.utils.NetworkUtil;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends BaseActivity implements MainMvpView, RangeSeekBar.OnRangeSeekBarChangeListener {

    @Inject MainPresenter mMainPresenter;

    @Bind(R.id.progressBar) MaterialProgressBar progressBar;
    @Bind(R.id.chart) ChartView chartView;
    @Bind(R.id.emptyWrap) RelativeLayout emptyWrap;
    @Bind(R.id.chartWrap) RelativeLayout chartWrap;
    @Bind(R.id.rangeBar) RangeSeekBar rangeBar;
    @Bind(R.id.txtFrom) TextView txtFrom;

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    BroadcastReceiver networkStateReceiver;

    /**
     * Return an Intent to start this Activity.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        mMainPresenter.attachView(this);

        // Customize rangebar
        rangeBar.setOnRangeSeekBarChangeListener(this);
        rangeBar.setNotifyWhileDragging(false);
        rangeBar.setTextAboveThumbsColor(android.R.color.transparent);



    }

    @Override
    protected void onStart() {
        super.onStart();
        mMainPresenter.initialize();
        // Register online / offline detection to start / stop interval update for chart
        networkStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                boolean connectivity = NetworkUtil.isNetworkConnected(getApplicationContext());
                if (!connectivity) {
                    Toast.makeText(getApplicationContext(), "OFFLINE", Toast.LENGTH_SHORT).show();
                    mMainPresenter.stopRefreshingChartDataFromNetwork();
                } else {
                    Toast.makeText(getApplicationContext(), "ONLINE", Toast.LENGTH_SHORT).show();
                    mMainPresenter.refreshChartDataFromNetwork();
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        try {
            registerReceiver(networkStateReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMainPresenter.stopRefreshingChartDataFromNetwork();
        try {
            unregisterReceiver(networkStateReceiver);
        } catch (Exception e){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                // Only initiate network reload if not currently loading
                if(progressBar.getVisibility()==View.GONE && NetworkUtil.isNetworkConnected(this))
                    mMainPresenter.refreshChartDataFromNetwork();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===== CONTRACT METHODS ========

    @Override
    public void showEmptyView() {
        emptyWrap.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, "There was en error: "+message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showChartView() {
        chartWrap.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateChart(long[] timestamps, float[] values, long minTimestamp, long maxTimestamp) {
        chartView.setChartData(values);
        rangeBar.setRangeValues(minTimestamp, maxTimestamp);

        Calendar calendar = Calendar.getInstance();
        long mills = (Long) rangeBar.getSelectedMaxValue()*1000;
        calendar.setTimeInMillis(mills);

        // If the max is selected we display 7 days from the past
        if(rangeBar.getSelectedMaxValue().longValue() >= maxTimestamp){
            calendar.add(Calendar.DAY_OF_MONTH, -7);
        }

        txtFrom.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void hideEmptyView() {
        emptyWrap.setVisibility(View.GONE);
    }

    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
        Log.i(App.LOGTAG, bar.getSelectedMaxValue() + "");
        mMainPresenter.filterChartDisplaySince(bar.getSelectedMaxValue());
    }
}
