package com.example.apenadetect;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apenadetect.helpers.firebase.firestore.FirebaseFireStoreHelper;
import com.example.apenadetect.models.BreathRate;
import com.example.apenadetect.models.ChartXFormatter;
import com.example.apenadetect.utils.TimeUitls;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ChartActivity extends AppCompatActivity {

    LineChart lineChart;

    Button btnChonNgay;

    TextView tvSelectedDate;

    Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        setControl();
        setEvent();
    }

    private void setEvent() {
        btnChonNgay.setOnClickListener(view -> {
            showDatePickerDialog();
        });
        setSelectedDate(new Date());
    }

    private void drawChart(List<Float> values){
        lineChart.setVisibility(View.VISIBLE);
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            float value = (float)values.get(i);
            entries.add(new Entry(i , value));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
//        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(0f);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(14f);

        lineChart.getDescription().setEnabled(false);

        // Tô màu dưới đường line
        dataSet.setDrawFilled(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(14f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextSize(14f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTextSize(14f);


        // Tạo LineData và set vào LineChart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh biểu đồ

        lineChart.setVisibleXRangeMaximum(10); // Hiển thị tối đa 5 điểm cùng lúc
        // Tự động di chuyển biểu đồ khi thêm điểm mới (tùy chọn)
//        lineChart.moveViewToX(dataSet.getEntryCount());

        lineChart.invalidate(); // Refresh biểu đồ

    }

    private void setControl() {
        lineChart = findViewById(R.id.lineChart);
        btnChonNgay = findViewById(R.id.btnChonNgay);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
    }

    public  void  setSelectedDate(Date newDate){
        this.selectedDate = newDate;
        tvSelectedDate.setText(TimeUitls.dateToString(newDate));
        getFirestoreData(newDate);
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = null;
        final Calendar calendar = Calendar.getInstance();

        if(selectedDate == null){
            calendar.setTime(new Date());
        }
        else{
            calendar.setTime(selectedDate);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    setSelectedDate(TimeUitls.createDate(selectedDay, selectedMonth, selectedYear));
                }, year, month, day);
        datePickerDialog.show();

    }

    public void getFirestoreData(Date date){
        FirebaseFireStoreHelper.gI().getBreathRatesInDate(date, (snapshot, exception) -> {
//            Log.d("CFVGBHNKJML", "SIZE: " + String.valueOf(snapshot.size()));
            List<Float> values = new ArrayList<>();
            for (DocumentSnapshot document : snapshot) {

                BreathRate breathRate = document.toObject(BreathRate.class);
                values.add(breathRate.getValue());
//                Log.d("CFVGBHNKJML", "Document ID: " + document.getId());
//                Log.d("CFVGBHNKJML", "Millis: " + millis);
            }
            Log.d("IHUKBJJDLASGHBKNDSF", "Size::: " + String.valueOf(values.size()));
//            redraw chart
            if(values.size() > 0){
                runOnUiThread(() -> {
                    drawChart(values);
                });
            }
            else{
                runOnUiThread(() -> {
                    lineChart.setVisibility(View.GONE);
                    Toast.makeText(this, "Không có dữ liệu", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}