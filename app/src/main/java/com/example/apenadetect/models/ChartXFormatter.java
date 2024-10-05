package com.example.apenadetect.models;

import com.example.apenadetect.utils.TimeUitls;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class ChartXFormatter extends ValueFormatter {
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        // Định dạng giá trị ở đây, ví dụ: chuyển đổi thành chuỗi
        return TimeUitls.millisToDate((long) value);
    }
}
