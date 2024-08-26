package com.paypay.charting.interfaces.dataprovider;

import com.paypay.charting.components.YAxis;
import com.paypay.charting.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
