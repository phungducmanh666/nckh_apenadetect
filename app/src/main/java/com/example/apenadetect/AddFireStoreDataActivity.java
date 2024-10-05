package com.example.apenadetect;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apenadetect.helpers.firebase.firestore.FirebaseFireStoreHelper;
import com.example.apenadetect.utils.TimeUitls;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddFireStoreDataActivity extends AppCompatActivity {

    Button btnAddData;

    Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_fire_store_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnAddData = findViewById(R.id.btnAddData);
        btnAddData.setOnClickListener(view -> {
            showDatePickerDialog();
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    setSelectedDate(TimeUitls.createDate(selectedDay, selectedMonth, selectedYear));
                }, year, month, day);
        datePickerDialog.show();
    }

    private void setSelectedDate(Date date){
        selectedDate = date;
        sendRandomData(date);
    }

    private void sendRandomData(Date date){
        Random r = new Random();
        float value = r.nextFloat();

        Map<String, Object> map = new HashMap<>();
        map.put("value", value);
        map.put("millis", date.getTime());

        FirebaseFireStoreHelper.gI().addData( map, null, null);
    }
}