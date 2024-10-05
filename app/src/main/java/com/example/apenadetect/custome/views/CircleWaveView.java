package com.example.apenadetect.custome.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.apenadetect.R;

public class CircleWaveView extends View {

        private Paint paint;
        private float radius;           // Bán kính hiện tại
        private float strokeWidth;      // Độ dày của đường viền
        private float maxStrokeWidth;   // Độ dày tối đa của đường viền
        private ValueAnimator strokeAnimator; // Animator cho độ dày

        private boolean isReload = false;
        private boolean isReversed = false;

        public CircleWaveView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs);
        }

        private void init(Context context, AttributeSet attrs) {
            paint = new Paint();
            paint.setColor(0xFF6200EE); // Màu đường viền
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);

            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleWaveView, 0, 0);

            try {
                radius = a.getFloat(R.styleable.CircleWaveView_circle_radius, 100);
//                Log.d("UHKJBNKFLSDFD", String.format("RADIUS:: %d", 12));
                maxStrokeWidth = a.getFloat(R.styleable.CircleWaveView_circle_maxStrokeWidth, 10);

                Log.d("RADIUS", String.valueOf(radius));
            }
            catch(Exception ex){
                Log.d("UYGHVBJKNLDSFNDS", ex.getMessage());
            }
            finally {
                a.recycle(); // Giải phóng tài nguyên
            }
            maxStrokeWidth = 20; // Độ dày tối đa của đường viền
            strokeWidth = 5; // Độ dày khởi đầu

            // Khởi động animation
            startAnimation();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setColor(Color.parseColor("#00C5FF"));
            paint.setStrokeWidth(strokeWidth); // Thiết lập độ dày của đường viền
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, paint); // Vẽ hình tròn
        }

        private void startAnimation() {
            // Tạo animator cho độ dày
            if(strokeAnimator != null){
                strokeAnimator.cancel();
            }

            float _max = maxStrokeWidth > 5 ? maxStrokeWidth : 5;

            strokeAnimator = ValueAnimator.ofFloat(5, _max);
            strokeAnimator.setDuration(500); // Thời gian animation
            strokeAnimator.setRepeatCount(ValueAnimator.INFINITE); // Lặp lại vô hạn
            strokeAnimator.setRepeatMode(ValueAnimator.REVERSE); // Lặp lại theo chiều ngược lại
            strokeAnimator.setInterpolator(new AccelerateDecelerateInterpolator()); // Hiệu ứng mượt mà

            strokeAnimator.addUpdateListener(animation -> {
                strokeWidth = (float) animation.getAnimatedValue(); // Cập nhật độ dày
                invalidate();
            });

            strokeAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);

                    if(isReload && isReversed){
                        startAnimation();
                        isReload = false;
                    }


                    isReversed = !isReversed;
                }
            });

            strokeAnimator.start(); // Bắt đầu animation


        }

        // Hàm để set lại bán kính từ lớp bên ngoài
        public void setRadius(float newRadius) {
            radius = newRadius;
            invalidate(); // Yêu cầu vẽ lại View
        }

        // Hàm để set lại độ dày tối đa của đường viền
        public void setMaxStrokeWidth(float newMaxStrokeWidth) {
            maxStrokeWidth = newMaxStrokeWidth;
            isReload = true;
        }
    }

