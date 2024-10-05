package com.example.apenadetect.helpers.firebase.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class FirebaseFireStoreHelper {

    private static FirebaseFireStoreHelper instance;

    public  static FirebaseFireStoreHelper gI(){
        if(instance == null)
            instance = new FirebaseFireStoreHelper();
        return  instance;
    }

    FirebaseFirestore db;

//    public  final  static String firestoreName = "breath_rates";
    public  final  static String firestoreName = "breath_rates";

    private FirebaseFireStoreHelper(){
        db = FirebaseFirestore.getInstance();
    }

    public void addData(Map<String, Object> data, I_OnSuccess onSuccess, I_OnError onError){
        db.collection(firestoreName).add(data).addOnSuccessListener(reference -> {
            if(onSuccess != null){
                onSuccess.exec(reference);
            }
        }).addOnFailureListener(e -> {
            if(onError != null){
                onError.exec(e);
            }
        });
    }

    public ListenerRegistration  addTrackingData(I_OnTracking onTracking){

        Query query = db.collection(firestoreName);
        ListenerRegistration registration = query.addSnapshotListener((value, error) -> {
            onTracking.exec(value, error);
        });

        return registration;
    }

    public ListenerRegistration  addTrackingBreathCurrent(I_OnTracking onTracking){

//        Query query = db.collection(firestoreName).whereGreaterThanOrEqualTo("millis", (System.currentTimeMillis() / 1000) * 1000).orderBy("millis", Query.Direction.DESCENDING);
        Query query = db.collection(firestoreName).orderBy("millis", Query.Direction.DESCENDING).limit(1);
        ListenerRegistration registration = query.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Xử lý lỗi nếu có
                Log.w("Firestore", "Listen failed.", error);
                return;
            }

            if (value != null && !value.isEmpty()) {
                onTracking.exec(value, null);
            } else {
                Log.d("Firestore", "No data found");
            }
        });

        return registration;
    }

    public void getBreathRatesInDate(Date date, I_OnTracking onTracking){
        // Thiết lập thời điểm bắt đầu của ngày (00:00:00.000)
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(date);
        calendarStart.set(Calendar.HOUR_OF_DAY, 0); // Thiết lập giờ (00)
        calendarStart.set(Calendar.MINUTE, 0);      // Thiết lập phút (00)
        calendarStart.set(Calendar.SECOND, 0);      // Thiết lập giây (00)
        calendarStart.set(Calendar.MILLISECOND, 0); // Thiết lập milliseconds (000)

        // Thiết lập thời điểm kết thúc của ngày (23:59:59.999)
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(date);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23); // Thiết lập giờ (23)
        calendarEnd.set(Calendar.MINUTE, 59);      // Thiết lập phút (59)
        calendarEnd.set(Calendar.SECOND, 59);      // Thiết lập giây (59)
        calendarEnd.set(Calendar.MILLISECOND, 999); // Thiết lập milliseconds (999)

        // Lấy thời điểm bắt đầu và kết thúc theo milliseconds
        long millisStart = calendarStart.getTimeInMillis();
        long millisEnd = calendarEnd.getTimeInMillis();

        Log.d("IHUKBJJDLASGHBKNDSF", "Millis start::: " + String.valueOf(millisStart));
        Log.d("IHUKBJJDLASGHBKNDSF", "Millis end::: " + String.valueOf(millisEnd));

        // Truy vấn trong khoảng thời gian từ đầu ngày đến cuối ngày
        Query query = db.collection(firestoreName)
                .whereGreaterThanOrEqualTo("millis", millisStart)
                .whereLessThanOrEqualTo("millis", millisEnd);

        // Thực hiện truy vấn ở đây (ví dụ như thêm Listener hoặc xử lý dữ liệu)
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                    onTracking.exec(task.getResult(), null);
            }
        });
    }
}
