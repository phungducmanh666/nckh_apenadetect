package com.example.apenadetect;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apenadetect.helpers.firebase.firestore.FirebaseFireStoreHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    Button btnUpdateFireBase;
    TextView txtNewBreathRate;

    EditText edtValue;
    TextView tvValue, tvMillis;

    ListenerRegistration registration;

    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setControl();
        setEvent();
    }

    private void setEvent() {
        FirebaseFireStoreHelper firebaseFireStoreHelper = FirebaseFireStoreHelper.gI();
        registration = firebaseFireStoreHelper.addTrackingData( (snapshot, exception) -> {
            for (DocumentChange dc : snapshot.getDocumentChanges()) {
                if(dc.getType() == DocumentChange.Type.ADDED){
                    Log.d("ADDED BREATH RATES", dc.getDocument().getId().toString());
                    Float value = dc.getDocument().get("value", Float.class);
                    Long millis = dc.getDocument().get("millis", Long.class);
                    runOnUiThread(() -> {
                        tvValue.setText(value.toString());
                        tvMillis.setText(millis.toString());
                    });
                }
            }
        });

        btnUpdateFireBase.setOnClickListener(view -> {
            float value = Float.valueOf(edtValue.getText().toString());
            Long millis = System.currentTimeMillis();
            Map<String, Object> data = new HashMap<>();
            data.put("value", value);
            data.put("millis", millis);
            firebaseFireStoreHelper.addData(data, reference -> {}, e -> {});
        });

//        chart
        Random rand = new Random();

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            int randomInt = rand.nextInt(101);
            entries.add(new Entry(i, randomInt));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Cubic Line Chart");
//        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);

        // Tô màu dưới đường line
        dataSet.setDrawFilled(true); // Kích hoạt fill
//        dataSet.setFillColor(Color.BLUE); // Màu fill dưới đường line

        // Đặt chế độ đường cong
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Tắt đường kẻ dọc (grid lines) trên trục X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false); // Ẩn đường kẻ dọc

        // Tắt đường kẻ ngang (grid lines) trên trục Y bên trái và bên phải
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // Ẩn đường kẻ ngang bên trái

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(false); // Ẩn đường kẻ ngang bên phải



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
        btnUpdateFireBase = findViewById(R.id.btnUpdateFireBase);
        tvValue = findViewById(R.id.tvValue);
        tvMillis = findViewById(R.id.tvMillis);
        edtValue = findViewById(R.id.edtValue);
        lineChart = findViewById(R.id.lineChart);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(registration != null){
            registration.remove();
        }
    }

    //    private void setEvent() {
//        FirebaseRealTimeDBHelper firebaseRealTimeDBHelper = FirebaseRealTimeDBHelper.getInstance();
//        firebaseRealTimeDBHelper.onNewBreathRate(breathRate -> {
//            runOnUiThread(() -> {
//                txtNewBreathRate.setText(Float.toString(breathRate.getValue()));
//            });
//        });
//    }

//    void updateFireBase(){
//        int x = 10;
//        int y = 13;
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference databaseReference = firebaseDatabase.getReference("arr");
////        databaseReference.child("userId").child("username").setValue("newUsername");
//        Query query = databaseReference.orderByKey().startAt(String.valueOf(x)).endAt(String.valueOf(y));
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<Integer> userList = new ArrayList<>();
//
//                // Lặp qua các kết quả nhận được trong phạm vi từ x đến y
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Integer user = snapshot.getValue(Integer.class);
//                    userList.add(user);
//                }
//
//                // Hiển thị hoặc xử lý danh sách users lấy được
//                for (Integer user : userList) {
//                    Log.d("Firebase", "VALUE: " + user);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Xử lý lỗi nếu có
//                Log.e("Firebase", "Error retrieving data", databaseError.toException());
//            }
//        });
//    }
}