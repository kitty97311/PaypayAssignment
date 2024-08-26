package com.paypay.charting.interfaces.dataprovider;

import com.paypay.charting.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
