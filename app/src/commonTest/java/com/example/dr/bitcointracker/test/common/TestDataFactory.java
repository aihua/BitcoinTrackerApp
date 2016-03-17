package com.example.dr.bitcointracker;

import com.example.dr.bitcointracker.mvp.model.service.local.models.ChartData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;


/**
 * Factory class that makes instances of data models with random field values.
 * The aim of this class is to help setting up test fixtures.
 */
public class TestDataFactory {

    public static String randomUuid() {
        return UUID.randomUUID().toString();
    }

    public static List<ChartData> makeListChartPoint(int number) {
        List<ChartData> ribots = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            ribots.add(makeChartPoint());
        }
        return ribots;
    }

    public static ChartData makeChartPoint() {
        ChartData point = new ChartData();
        point.setValue(new Random().nextInt(65536)-32768);
        point.setTimestamp(new Random().nextLong());
        return point;
    }
}
