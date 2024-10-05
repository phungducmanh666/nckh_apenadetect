package com.example.apenadetect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.apenadetect.custome.views.CircleWaveView;
import com.example.apenadetect.helpers.firebase.firestore.FirebaseFireStoreHelper;
import com.example.apenadetect.models.BreathRate;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private CircleWaveView circleWaveView;
    private Handler handler = new Handler();
    private Runnable runnable;

    ListenerRegistration listenerRegistration;

    Button btnBieuDo;
    TextView tvNhipTho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setControl();
        setEvent();

        Log.d("DCFVGBHNKJMLSF", "TIME: " + String.valueOf(System.currentTimeMillis()));
    }

    private void setEvent() {

        btnBieuDo.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this ,ChartActivity.class);
            startActivity(intent);
        });

// random breath rate
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                float newRadius = 1 + (float) Math.random() * 100;
//                circleWaveView.setMaxStrokeWidth(newRadius);
//                handler.postDelayed(this, 1000);
//            }
//        };
//
//        handler.post(runnable);

        listenerRegistration = FirebaseFireStoreHelper.gI().addTrackingBreathCurrent((snapshot, exception) -> {
            // on error
            if(exception != null){
                return;
            }
            // on success
            if(snapshot != null){
                Log.d("DCFVGBHNKJMLSF", "Size::: " + String.valueOf(snapshot.getDocumentChanges().size()));
                if(snapshot.getDocumentChanges().size() > 0){
                    Log.d("DCFVGBHNKJMLSF", "Breath rate: " + "CO data");
                    DocumentSnapshot documentSnapshot = snapshot.getDocuments().get(0);
                    BreathRate breathRate = documentSnapshot.toObject(BreathRate.class);
                    float val = breathRate.getValue();
                    circleWaveView.setMaxStrokeWidth(val);
                    runOnUiThread(() -> {
                        tvNhipTho.setText(String.valueOf(val));
                    });
                    Log.d("DCFVGBHNKJMLSF", "Breath rate: " + String.valueOf(breathRate.getValue()));
                }
            }
        });

    }

    private void setControl() {
        circleWaveView = findViewById(R.id.animatedCircleView);
        btnBieuDo = findViewById(R.id.btnBieuDo);
        tvNhipTho = findViewById(R.id.tvNhipTho);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
    }
}