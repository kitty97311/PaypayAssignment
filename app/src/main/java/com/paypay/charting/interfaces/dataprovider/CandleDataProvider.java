package com.paypay.charting.interfaces.dataprovider;

import com.paypay.charting.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
