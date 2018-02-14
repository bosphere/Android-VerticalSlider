package com.bosphere.verticalsliderdemo;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.bosphere.verticalslider.VerticalSlider;

public class MainActivity extends AppCompatActivity {

    private VerticalSlider mVerticalSlider;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVerticalSlider = findViewById(R.id.vertical_slider);
        mTextView = findViewById(R.id.text_view);

        mVerticalSlider.setOnSliderProgressChangeListener(new VerticalSlider.OnProgressChangeListener() {
            @Override
            public void onProgress(float progress) {
                mTextView.setText(String.format("Progress: %.1f%%", progress * 100f));
            }
        });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVerticalSlider.setProgress(0f);
                mTextView.setText("Move the slider!");

                ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                animator.setStartDelay(1000L);
                animator.setDuration(3000L);
                animator.setInterpolator(new DecelerateInterpolator(1.5f));
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mVerticalSlider.setProgress((Float) animation.getAnimatedValue(), true);
                    }
                });
                animator.start();
            }
        });
    }
}
