package com.example.apenadetect.custome.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.animation.LinearInterpolator;
import java.util.Random;

public class WaveView extends View {

    private Paint paint;
    private Path path;
    private float waveShift;  // Dịch chuyển của sóng
    private Random random;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE); // Màu sóng
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        path = new Path();
        random = new Random();

        // Khởi tạo ValueAnimator cho hiệu ứng sóng chuyển động
        ValueAnimator waveAnimator = ValueAnimator.ofFloat(0, 1);
        waveAnimator.setDuration(2000);  // Thời gian chạy của animation
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setInterpolator(new LinearInterpolator());
        waveAnimator.addUpdateListener(animation -> {
            waveShift = (float) animation.getAnimatedValue();
            invalidate();  // Vẽ lại View
        });
        waveAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Đặt lại path cho sóng
        path.reset();

        int width = getWidth();
        int height = getHeight();
        float waveHeight = 50 + random.nextInt(100);  // Độ cao ngẫu nhiên của sóng (50-150)
        float waveLength = width;  // Độ dài của sóng (có thể tùy chỉnh)

        // Vẽ đường sóng bằng Path
        path.moveTo(0, height / 2f);
        for (int i = 0; i < width; i++) {
            float y = (float) (waveHeight * Math.sin((i + waveShift * width) * 2 * Math.PI / waveLength)) + height / 2f;
            path.lineTo(i, y);
        }

        // Đóng path để fill phần dưới đường sóng
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.close();

        // Vẽ đường sóng lên canvas
        canvas.drawPath(path, paint);
    }
}
