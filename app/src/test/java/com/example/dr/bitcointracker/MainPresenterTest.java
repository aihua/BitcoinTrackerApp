package com.example.dr.bitcointracker;

import android.app.Application;

import com.example.dr.bitcointracker.mvp.model.service.local.RealmService;
import com.example.dr.bitcointracker.mvp.model.service.local.models.ChartData;
import com.example.dr.bitcointracker.mvp.model.service.local.repository.ChartRepository;
import com.example.dr.bitcointracker.mvp.model.service.remote.ChartAPIService;
import com.example.dr.bitcointracker.mvp.presenter.base.BasePresenter;
import com.example.dr.bitcointracker.mvp.presenter.main.MainPresenter;
import com.example.dr.bitcointracker.mvp.view.main.MainMvpView;
import com.example.dr.bitcointracker.util.RxSchedulersOverrideRule;
import com.google.gson.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import rx.Observable;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainPresenterTest {

    @Mock MainMvpView mMockMainMvpView;
    @Mock ChartAPIService mChartAPIService;
    @Mock RealmService mRealmService;
    @Mock MainPresenter mMockMainPresenter;
    @Mock ChartRepository mChartRepository;
    private MainPresenter mMainPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();


    @Before
    public void setUp() {
        initMocks(this);
        mMainPresenter = new MainPresenter(mChartRepository, mChartAPIService);
        mMainPresenter.attachView(mMockMainMvpView);
    }

    @After
    public void tearDown() {mMainPresenter.detachView();}

    @Test
    public void load_remote_data_with_data_returned() {

        when(mChartAPIService.getAllBitcoinMarketPrice()).thenReturn(Observable.just(new JsonObject()));

        mMainPresenter.doApiCall();

        verify(mMockMainMvpView).showProgress();
        verify(mMockMainMvpView).hideProgress();

        verify(mChartRepository).insertDataFromBlockchain(any(JsonObject.class));
        verify(mMockMainMvpView, never()).updateChart(any(long[].class), any(float[].class), anyLong(), anyLong());
    }

    @Test
    public void load_remote_data_without_data_returned() {

        when(mChartAPIService.getAllBitcoinMarketPrice()).thenReturn(Observable.<JsonObject>empty());

        mMainPresenter.doApiCall();

        verify(mMockMainMvpView).showProgress();
        verify(mMockMainMvpView).hideProgress();

        verify(mChartRepository, never()).insertDataFromBlockchain(any(JsonObject.class));
    }

    @Test
    public void load_local_data_without_data_returned() {
        when(mChartRepository.getAllItems()).thenReturn(Observable.<List<ChartData>>empty());

        mMainPresenter.initialize();

        verify(mMockMainMvpView).showEmptyView();
        verify(mMockMainMvpView, never()).updateChart(any(long[].class), any(float[].class), anyLong(), anyLong());
        verify(mMockMainMvpView, never()).showChartView();
        verify(mMockMainMvpView, never()).showProgress();
        assertTrue(mMainPresenter.mLoadLocalDataSupscription != null);
    }

    @Test
    public void load_local_data_with_data_returned() {
        List<ChartData> testList = TestDataFactory.makeListChartPoint(20);
        when(mChartRepository.getAllItems()).thenReturn(Observable.just(testList));

        mMainPresenter.initialize();

        verify(mMockMainMvpView).hideEmptyView();
        verify(mMockMainMvpView).showChartView();
        // Only called in filter function
        verify(mMockMainMvpView).updateChart(any(long[].class), any(float[].class), anyLong(), anyLong());
        verify(mMockMainMvpView, never()).showProgress();
        assertTrue(mMainPresenter.mLoadLocalDataSupscription != null);
    }

    @Test
    public void start_refreshing_data() {

        when(mChartAPIService.getAllBitcoinMarketPrice()).thenReturn(Observable.just(new JsonObject()));

        mMainPresenter.refreshChartDataFromNetwork();

        assertTrue(mMainPresenter.mLoadRemoteDataSubscription != null);
    }

    @Test
    public void stop_refreshing_data() {

        when(mChartAPIService.getAllBitcoinMarketPrice()).thenReturn(Observable.just(new JsonObject()));

        mMainPresenter.stopRefreshingChartDataFromNetwork();

        assertTrue(mMainPresenter.mLoadRemoteDataSubscription == null);
    }
}
