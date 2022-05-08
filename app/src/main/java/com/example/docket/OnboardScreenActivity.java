package com.example.docket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnboardScreenActivity extends AppCompatActivity {

    //Variables
    ViewPager viewPager;
    LinearLayout dotsLayout;
    ViewPagerAdapter viewPagerAdapter;
    TextView[] dots;
    Button backBtn, nextBtn, skipBtn, letsGetStarted;
    Animation animation;
    int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // hide the statusBar
        setContentView(R.layout.activity_onboard_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // hide the action bar
        }

        //Hooks
        backBtn = findViewById(R.id.back_left_btn);
        nextBtn = findViewById(R.id.next_btn);
        skipBtn = findViewById(R.id.skip_btn);
        letsGetStarted = findViewById(R.id.get_started_btn);
        viewPager = findViewById(R.id.slideViewPager);
        dotsLayout = findViewById(R.id.dots);

        //Call adapter
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        //Dots
        addDots(0);
        viewPager.addOnPageChangeListener(viewListener);

        // back button task
        backBtn.setOnClickListener(view -> {
            if (getItem(0) > 0) {
                viewPager.setCurrentItem(getItem(-1), true);
            }
        });

        // next button task
        nextBtn.setOnClickListener(view -> {
            if (getItem(0) < 5) {
                viewPager.setCurrentItem(getItem(1), true);
            }
        });

        // skip button task
        skipBtn.setOnClickListener(view -> {
            Intent i = new Intent(OnboardScreenActivity.this, GetStartActivity.class);
            startActivity(i);
            finish();
        });

        // letsGetStarted button task
        letsGetStarted.setOnClickListener(view -> {
            Intent i = new Intent(OnboardScreenActivity.this, GetStartActivity.class);
            startActivity(i);
            finish();
        });

    }

    // creating dots Method
    private void addDots(int position) {

        dots = new TextView[6];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.grey));
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.light_yellow));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        // showing dots and skip buttons when user interact with it
        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;

            if (position == 0) {
                backBtn.setVisibility(View.INVISIBLE);
            } else {
                backBtn.setVisibility(View.VISIBLE);
            }

            if (position > 4) {
                nextBtn.setVisibility(View.INVISIBLE);
            } else {
                nextBtn.setVisibility(View.VISIBLE);
            }

            if (position == 0) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else if (position == 2) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else if (position == 3) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else if (position == 4) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else {

                animation = AnimationUtils.loadAnimation(OnboardScreenActivity.this, R.anim.slide_anim);
                letsGetStarted.setAnimation(animation);
                letsGetStarted.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
}