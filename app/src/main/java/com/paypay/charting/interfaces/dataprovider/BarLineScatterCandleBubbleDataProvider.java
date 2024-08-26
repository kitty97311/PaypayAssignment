package com.paypay.charting.interfaces.dataprovider;

import com.paypay.charting.components.YAxis.AxisDependency;
import com.paypay.charting.data.BarLineScatterCandleBubbleData;
import com.paypay.charting.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
