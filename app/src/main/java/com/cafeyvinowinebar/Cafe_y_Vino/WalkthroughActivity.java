package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cafeyvinowinebar.Cafe_y_Vino.Adapters.SliderAdapter;

/**
 * First screen that an unregistered user sees
 * Walks them through the basic functionality of the app using a ViewPager
 */

public class WalkthroughActivity extends AppCompatActivity {

    private LinearLayout dotsLayout;
    private TextView[] dots;
    private Button btnOmitir;

    public static Intent newIntent(Context context) {
        return new Intent(context, WalkthroughActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);

        ViewPager slidePager = findViewById(R.id.slidePager);
        dotsLayout = findViewById(R.id.dotsLayout);
        btnOmitir = findViewById(R.id.btnOmitir);

        SliderAdapter sliderAdapter = new SliderAdapter(this);

        slidePager.setAdapter(sliderAdapter);
        slidePager.addOnPageChangeListener(viewListener);

        addDotsIndicator(0);

        btnOmitir.setOnClickListener(v -> startActivity(BienvenidoActivity.newIntent(getBaseContext())));
    }

    /**
     * adds an array of dots textView, highlights the dot corresponding to the current page
     */
    public void addDotsIndicator(int position) {
        dots = new TextView[4];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setTypeface(ResourcesCompat.getFont(this, R.font.star_dings));
            dots[i].setPadding(5,5,5,5);
            dots[i].setText("z");
            dots[i].setTextSize(20);
            dots[i].setTextColor(getColor(R.color.dark_olive));

            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(Color.WHITE);
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);

            if (position == dots.length - 1) {
                btnOmitir.setText("OK");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}