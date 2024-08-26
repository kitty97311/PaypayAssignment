package com.paypay.charting.interfaces.dataprovider;

import com.paypay.charting.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
